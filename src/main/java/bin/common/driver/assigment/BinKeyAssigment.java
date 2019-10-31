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
 * ����BIN SQL�����Ŀ�����.
 * <p>
 *     ���ڽ���BinSQL,��ȡ��BinSQL�е�����BinKey���ʽ��������Ӧ��{@link BinKeyHandler}���н�������
 *     Ȼ�󷵻������Ŀ�ִ�е�SQL�ű�
 * </p>
 */
public class BinKeyAssigment {

    /**
     * ��־
     */
    Log log= LogFactory.getLog(BinKeyAssigment.class);

    /**
     * Bin SQL���ʽǰ׺
     */
    String prefix= BinKey.PREFIX;
    /**
     * Bin SQL���ʽ��׺
     */
    String suffix= BinKey.SUFFIX;

    /**
     * ���ڴ�� {@link BinKey} ��KEY���� �� ��֮��Ӧ{@link BinKeyHandler} ��ӳ��
     */
    private Map<String, BinKeyHandler> keyHandlerMap;

    /**
     * Mybatisȫ��������Ϣ
     */
    private Configuration configuration;

    /**
     * SQL��Ƭ������
     */
    private SqlFragmentHelper sqlFragmentHelper;

    /**
     *
     * @param configuration Mybatsiȫ��������Ϣ
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
     * ���� Bin SQL��ȡ������ {@code sql} ������'['��ͷ��']'��β�ı��ʽ�����ݱ��ʽ�ҵ���Ӧ�� {@link BinKeyHandler}
     * ���ɶ�Ӧ�� {@link BinKeyHandler} ���н������õ�SQL��ƬȻ�󸲸�{@code sql}�ı��ʽ
     * @param sql binSQL
     * @param paramObject ��������
     * @return ��ִ�е�SQL�ű�
     * @throws BinResloveSqlException ĿǰBin���ʽ��ʱ��֧��Ƕ�ף�����Ƕ�ױ��ʽ���׳��쳣��
     *                  ���û���ҵ�BIN���ʽ��Ӧ�� {@link BinKeyHandler} Ҳ���׳��쳣��
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
     * ��ȡ��ƥ���Bin SQL ���ʽ������
     * @param sqlFrag SQL��Ƭ
     * @return ��ƥ���Bin SQL ���ʽ������
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
     * �ж� {@code str} �Ƿ񼴲�Ϊnull�ֲ�Ϊ���ַ���
     * @param str �ַ���
     * @return {@code str} �Ƿ񼴲�Ϊnull�ֲ�Ϊ���ַ���ʱ����true�����򷵻�false
     */
    private boolean isNotEmpty(String str){
        return str!=null && !str.isEmpty();
    }

    /**
     * ���� {@code binSQLExpression} �����װ�� {@link BinKeySqlInfo}
     * @param binSQLExpression BinSQL���ʽ
     * @param binKey Bin�ؼ���
     * @return ��Ӧ {@code binSQLExpression} �� {@link BinKeySqlInfo}
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
     * BinSQL���ʽ��װ��
     * <p>
     *     e.g.'[IS_NOT_EMPTY_name&&age: name=#{name} and age=#{age}]',��ֽ�ɣ�
     *     <ol>
     *         <li>{@link BinKeySqlInfo#binKeySqlFragment} ��ʾ Bin SQL ���ʽ.�� '[IS_NOT_EMPTY_name&&age: name=#{name} and age=#{age}]'</li>
     *         <li>{@link BinKeySqlInfo#propertyExpression} ��ʾ BinKey��'_'��':'���ַ������� 'name&&age' </li>
     *         <li>{@link BinKeySqlInfo#mapKeyList} ��ʾ ����λ��'{'��'}'֮����ַ���.�� '[name,age]' </li>
     *         <li>{@link BinKeySqlInfo#strInBarackets} ��ʾ'('��')'֮��ı��ʽ������ '[id in (#{id},)]' => '#{id},'</li>
     *         <li>{@link BinKeySqlInfo#resolvedSqlFrg} ��ʾ Bin SQL ���ʽ�� ':' ������ַ�������'name=#{name} and age=#{age}'</li>
     *         <li>{@link BinKeySqlInfo#lBarchetIndex}��ʾ'('�ַ�������λ��</li>
     *         <li>{@link BinKeySqlInfo#rBarchetIndex}��ʾ')'�ַ�������λ��</li>
     *         <li>{@link BinKeySqlInfo#separatorIndex}��ʾ':'�ַ�������λ��</li>
     *     </ol>
     * </p>
     */
    public class BinKeySqlInfo {
        /**
         * Bin SQL ���ʽ
         */
        private String binKeySqlFragment;
        /**
         * BinKey��'_'��':'���ַ���
         */
        private String propertyExpression;
        /**
         * ����λ��'{'��'}'֮����ַ���
         */
        private List<String> mapKeyList;
        /**
         * '('��')'֮��ı��ʽ
         */
        private String strInBarackets;
        /**
         * Bin SQL ���ʽ�� ':' ������ַ���
         */
        private String resolvedSqlFrg;
        /**
         * '('�ַ�������λ��
         */
        private int lBarchetIndex;
        /**
         * ')'�ַ�������λ��
         */
        private int rBarchetIndex;
        /**
         * ':'�ַ�������λ��
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
