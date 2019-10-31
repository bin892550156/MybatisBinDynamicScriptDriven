package bin.common.driver.assigment;

import bin.common.driver.BinKey;
import bin.common.driver.exception.BinResloveSqlException;
import bin.common.driver.helper.SqlFragmentHelper;
import bin.common.driver.keyhandler.*;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析BIN SQL操作的控制器.
 * <p>
 *     用于解析BinSQL,提取出BinSQL中的所有BinKey表达式，交给对应的{@link BinKeyHandler}进行解析处理，
 *     然后返回完整的可执行的SQL脚本
 * </p>
 */
public class BinKeyAssigment {

    /**
     * 日志
     */
    Log log= LogFactory.getLog(BinKeyAssigment.class);

    /**
     * Bin SQL表达式前缀
     */
    String prefix= BinKey.PREFIX;
    /**
     * Bin SQL表达式后缀
     */
    String suffix= BinKey.SUFFIX;

    /**
     * 用于存放 {@link BinKey} 的KEY常量 和 与之对应{@link BinKeyHandler} 的映射
     */
    private Map<String, BinKeyHandler> keyHandlerMap;

    /**
     * Mybatis全局配置信息
     */
    private Configuration configuration;

    /**
     * SQL碎片帮助类
     */
    private SqlFragmentHelper sqlFragmentHelper;

    /**
     *
     * @param configuration Mybatsi全局配置信息
     */
    public BinKeyAssigment(Configuration configuration) {
        sqlFragmentHelper=new SqlFragmentHelper();
        this.configuration=configuration;
        keyHandlerMap=new HashMap<>();
        keyHandlerMap.put(BinKey.KEY_IF,new IfBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_IS_NOT_EMPTY, new IsNotEmplyBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_IS_EMPTY,new IsEmplyBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_IS_NOT_NULL,new IsNotNullBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_IS_NULL,new IsNullBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_CASE,new CaseBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_IN,new InBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_UPDATE,new UpdateBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_UPDATE_NOT_EMPTY,new UpdateNotEmptyBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_UPDATE_NOT_NULL,new UpdateNotNullBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_INSERT,new InsertBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_INSERT_NOT_EMPTY,new InsertNotEmptyBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_INSERT_NOT_NULL,new InsertNotNullBinKeyHandler(configuration));
        keyHandlerMap.put(BinKey.KEY_INSERT_FOR_EACH,new InsertForeachBinKeyHandler(configuration));
    }

    /**
     * 解析 Bin SQL，取出里面 {@code sql} 的所有'['开头，']'结尾的表达式，根据表达式找到对应的 {@link BinKeyHandler}
     * 交由对应的 {@link BinKeyHandler} 进行解析，得到SQL碎片然后覆盖{@code sql}的表达式
     * @param sql binSQL
     * @param paramObject 参数对象
     * @return 可执行的SQL脚本
     * @throws BinResloveSqlException 目前Bin表达式暂时不支持嵌套，所有嵌套表达式会抛出异常；
     *                  如果没有找到BIN表达式对应的 {@link BinKeyHandler} 也会抛出异常。
     */
    public String resloveSql(String sql,Object paramObject){
        StringBuilder temp=new StringBuilder(sql);
        int prefixIndex=temp.indexOf(prefix);
        int suffixIndex=temp.indexOf(suffix);
        boolean first=true;
        while(prefixIndex!=-1 && suffixIndex!=-1){
            String binKeySqlFrag=temp.substring(prefixIndex,suffixIndex+1);
            if(binKeySqlFrag.indexOf(prefix,1)!=-1){
                throw new BinResloveSqlException(String.format("can not nest bin expression in bin SQL : %s ",
                        sql));
            }
            Map.Entry<String, BinKeyHandler> bestMatchEntry= getBestMatchBinKeyHandler(binKeySqlFrag);
            if(bestMatchEntry==null){
                throw new BinResloveSqlException(String.format(" not found correspond BinKeyHandler in %s , bin SQL : ",
                        binKeySqlFrag,sql));
            }
            BinKeyHandler binKeyHandler =bestMatchEntry.getValue();
            String key=bestMatchEntry.getKey();
            BinKeySqlInfo binKeySqlInfo =revsoleSqlFrag(binKeySqlFrag,key);
            String resolvedScript= binKeyHandler.resolve(binKeySqlInfo,paramObject);
            if(first&&isNotEmpty(resolvedScript)){
                boolean isWhereTailFlag=sqlFragmentHelper.isWhereAsTail(temp.toString(),prefixIndex);
                if(isWhereTailFlag){
                    resolvedScript=sqlFragmentHelper.correctSqlFragPrefix(resolvedScript);
                }
                first=false;
            }
            if(log.isDebugEnabled()){
                log.debug(String.format("bin SQL : %s . binSqlKeyInfo : %s . parsed result : %s .",
                        sql, binKeySqlInfo.toString(),resolvedScript));
            }
            temp.replace(prefixIndex,suffixIndex+1,resolvedScript);
            prefixIndex=temp.indexOf(prefix);
            suffixIndex=temp.indexOf(suffix);
        }
        String correctedSql=sqlFragmentHelper.correctSqlFragSuffix(temp.toString());
        if(log.isDebugEnabled()){
            log.debug(String.format("bin SQL : %s ==> sql : %s",sql,correctedSql));
        }
        return correctedSql;
    }

    /**
     * 获取最匹配的Bin SQL 表达式处理器
     * @param sqlFrag SQL碎片
     * @return 最匹配的Bin SQL 表达式处理器
     */
    public Map.Entry<String,BinKeyHandler> getBestMatchBinKeyHandler(String sqlFrag){
        Map.Entry<String,BinKeyHandler> bestMatchEntry=null;
        for(Map.Entry<String,BinKeyHandler> entry:keyHandlerMap.entrySet()){
            String key=entry.getKey();
            if(sqlFrag.startsWith(key)){
                char keyEndCharinSqlFrag=sqlFrag.charAt(key.length());
                if(keyEndCharinSqlFrag=='_'||keyEndCharinSqlFrag==']'||keyEndCharinSqlFrag==':'){
                    if(bestMatchEntry!=null){
                        String bestMatchBinKey=bestMatchEntry.getKey();
                        if(bestMatchBinKey.length()<key.length()){
                            bestMatchEntry=entry;
                        }
                    }else{
                        bestMatchEntry=entry;
                    }
                }
            }
        }
        return bestMatchEntry;
    }

    /**
     * 判断 {@code str} 是否即不为null又不为空字符串
     * @param str 字符串
     * @return {@code str} 是否即不为null又不为空字符串时返回true；否则返回false
     */
    private boolean isNotEmpty(String str){
        return str!=null && !str.isEmpty();
    }

    /**
     * 解析 {@code binSQLExpression} 将其封装成 {@link BinKeySqlInfo}
     * @param binSQLExpression BinSQL表达式
     * @param binKey Bin关键词
     * @return 对应 {@code binSQLExpression} 的 {@link BinKeySqlInfo}
     */
    private BinKeySqlInfo revsoleSqlFrag(String binSQLExpression, String binKey){
        char[] sqlFragCharArr=binSQLExpression.toCharArray();
        int separatorIndex=-1;
        String resolvedSqlFrg=null;
        int lBarchetIndex=-1;
        int rBarchetIndex=-1;
        String strInBarackets=null;
        List<String> mapKeyList=new ArrayList<>();
        String propertyExpression=null;
        int mapKeyPrefixIndex=-1;
        int mapKeySuffixIndex=-1;
        for (int i = 1; i < sqlFragCharArr.length-1; i++) {
            char c=sqlFragCharArr[i];
            if(c==':'){
                separatorIndex=i;
                int propertyExpressionStartIndex=binKey.length()+1;
                if(propertyExpressionStartIndex<i)
                    propertyExpression=binSQLExpression.substring(propertyExpressionStartIndex,i);
                resolvedSqlFrg=binSQLExpression.substring(separatorIndex+1,binSQLExpression.length()-1);
            }
            if(c=='('){
                lBarchetIndex=i;
            }
            if(c==')'){
                rBarchetIndex=i;
            }
            if(lBarchetIndex!=-1&&rBarchetIndex!=-1){
                strInBarackets=binSQLExpression.substring(lBarchetIndex+1,rBarchetIndex);
            }
            if(c=='{'){
                mapKeyPrefixIndex=i;
            }
            if(c=='}'){
                mapKeySuffixIndex=i;
            }
            if(mapKeyPrefixIndex!=-1&&mapKeySuffixIndex!=-1){
                String mapKey=binSQLExpression.substring(mapKeyPrefixIndex+1,mapKeySuffixIndex);
                mapKeyList.add(mapKey);
                mapKeyPrefixIndex=-1;
                mapKeySuffixIndex=-1;
            }
        }
        if( !isWriteOperationBinKey(binKey) &&
                (propertyExpression==null||propertyExpression.isEmpty()||separatorIndex==-1)){
            throw new BinResloveSqlException(" not found perperty expression in : "+binSQLExpression);
        }
        BinKeySqlInfo binKeySqlInfo =new BinKeySqlInfo();
        binKeySqlInfo.setBinKeySqlFragment(binSQLExpression);
        binKeySqlInfo.setPropertyExpression(propertyExpression);
        binKeySqlInfo.setMapKeyList(mapKeyList);
        binKeySqlInfo.setStrInBarackets(strInBarackets);
        binKeySqlInfo.setResolvedSqlFrg(resolvedSqlFrg);
        binKeySqlInfo.setlBarchetIndex(lBarchetIndex);
        binKeySqlInfo.setrBarchetIndex(rBarchetIndex);
        binKeySqlInfo.setSeparatorIndex(separatorIndex);
        return binKeySqlInfo;
    }

    private boolean isWriteOperationBinKey(String binKey){
        return binKey.equals(BinKey.KEY_INSERT) || binKey.equals(BinKey.KEY_INSERT_NOT_EMPTY)||binKey.equals(BinKey.KEY_INSERT_NOT_NULL)
                    ||binKey.equals(BinKey.KEY_UPDATE)||binKey.equals(BinKey.KEY_UPDATE_NOT_EMPTY)||binKey.equals(BinKey.KEY_UPDATE_NOT_NULL);
    }

    /**
     * BinSQL表达式封装类
     * <p>
     *     e.g.'[IS_NOT_EMPTY_name&&age: name=#{name} and age=#{age}]',会分解成：
     *     <ol>
     *         <li>{@link BinKeySqlInfo#binKeySqlFragment} 表示 Bin SQL 表达式.即 '[IS_NOT_EMPTY_name&&age: name=#{name} and age=#{age}]'</li>
     *         <li>{@link BinKeySqlInfo#propertyExpression} 表示 BinKey加'_'到':'的字符串。即 'name&&age' </li>
     *         <li>{@link BinKeySqlInfo#mapKeyList} 表示 所有位于'{'到'}'之间的字符串.即 '[name,age]' </li>
     *         <li>{@link BinKeySqlInfo#strInBarackets} 表示'('到')'之间的表达式。例如 '[id in (#{id},)]' => '#{id},'</li>
     *         <li>{@link BinKeySqlInfo#resolvedSqlFrg} 表示 Bin SQL 表达式中 ':' 后面的字符串。即'name=#{name} and age=#{age}'</li>
     *         <li>{@link BinKeySqlInfo#lBarchetIndex}表示'('字符的索引位置</li>
     *         <li>{@link BinKeySqlInfo#rBarchetIndex}表示')'字符的索引位置</li>
     *         <li>{@link BinKeySqlInfo#separatorIndex}表示':'字符的索引位置</li>
     *     </ol>
     * </p>
     */
    public class BinKeySqlInfo {
        /**
         * Bin SQL 表达式
         */
        private String binKeySqlFragment;
        /**
         * BinKey加'_'到':'的字符串
         */
        private String propertyExpression;
        /**
         * 所有位于'{'到'}'之间的字符串
         */
        private List<String> mapKeyList;
        /**
         * '('到')'之间的表达式
         */
        private String strInBarackets;
        /**
         * Bin SQL 表达式中 ':' 后面的字符串
         */
        private String resolvedSqlFrg;
        /**
         * '('字符的索引位置
         */
        private int lBarchetIndex;
        /**
         * ')'字符的索引位置
         */
        private int rBarchetIndex;
        /**
         * ':'字符的索引位置
         */
        private int separatorIndex;

        public String getPropertyExpression() {
            return propertyExpression;
        }

        public void setPropertyExpression(String propertyExpression) {
            this.propertyExpression = propertyExpression;
        }

        public String getBinKeySqlFragment() {
            return binKeySqlFragment;
        }

        public void setBinKeySqlFragment(String binKeySqlFragment) {
            this.binKeySqlFragment = binKeySqlFragment;
        }

        public List<String> getMapKeyList() {
            return mapKeyList;
        }

        public void setMapKeyList(List<String> mapKeyList) {
            this.mapKeyList = mapKeyList;
        }

        public String getStrInBarackets() {
            return strInBarackets;
        }

        public void setStrInBarackets(String strInBarackets) {
            this.strInBarackets = strInBarackets;
        }

        public String getResolvedSqlFrg() {
            return resolvedSqlFrg;
        }

        public void setResolvedSqlFrg(String resolvedSqlFrg) {
            this.resolvedSqlFrg = resolvedSqlFrg;
        }

        public int getlBarchetIndex() {
            return lBarchetIndex;
        }

        public void setlBarchetIndex(int lBarchetIndex) {
            this.lBarchetIndex = lBarchetIndex;
        }

        public int getrBarchetIndex() {
            return rBarchetIndex;
        }

        public void setrBarchetIndex(int rBarchetIndex) {
            this.rBarchetIndex = rBarchetIndex;
        }

        public int getSeparatorIndex() {
            return separatorIndex;
        }

        public void setSeparatorIndex(int separatorIndex) {
            this.separatorIndex = separatorIndex;
        }

        @Override
        public String toString() {
            return "BinSqlKeyInfo{" +
                    "binKeySqlFragment='" + binKeySqlFragment + '\'' +
                    ", propertyExpression='" + propertyExpression + '\'' +
                    ", mapKeyList=" + mapKeyList +
                    ", strInBarackets='" + strInBarackets + '\'' +
                    ", resolvedSqlFrg='" + resolvedSqlFrg + '\'' +
                    ", lBarchetIndex=" + lBarchetIndex +
                    ", rBarchetIndex=" + rBarchetIndex +
                    ", separatorIndex=" + separatorIndex +
                    '}';
        }
    }
}
