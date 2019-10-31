package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.exception.BinResloveSqlException;
import bin.common.driver.helper.ParamterMapHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 对应{@link bin.common.driver.BinKey#KEY_IN}的业务处理器，业务功能等同于：
 * <p>
 *     列名 in <br/>
 *     &lt;FOREACH item='属性名' index='i' open='(' colse=')' separtor=',' collection='属性列表'&gt; <br/>
 *             &nbsp;&nbsp;&nbsp; #{属性名.属性名} <br/>
 *     &lt;/FOREACH&gt; <br/>
 * </p>
 */
public class InBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatis 全局配置信息
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public InBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 以{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}作为属性名，从{@code paramObject}中获取对应
     * 对象, 通过遍历对象和{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}以及
     * {@link BinKeyAssigment.BinKeySqlInfo#getMapKeyList()}的元素，拼装成' 列名 in (#{mayKey1},#{mapKey}...) ',
     * mayKey为{@link BinKeyAssigment.BinKeySqlInfo#getMapKeyList()}的元素.并将mayKey对应的属性值添加到{@code paramObject}中
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return 如果找到 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 在 {@code paramObject} 对应的属性，
     *  就返回SQL碎片：' 列名 in (#{mayKey1},#{mapKey}...) ',否则返回空字符串
     * @throws BinResloveSqlException
     *         <ol>
     *             <li>{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}中缺失括号时抛出 </li>
     *             <li>{@link BinKeyAssigment.BinKeySqlInfo#getMapKeyList()}出现多个时</li>
     *             <li>{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 在 {@code paramObject} 对应的属性对象不是
     *                  {@link Collection} 或者 数组类型 时
     *             </li>
     *         </ol>
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        String propertyName = binBinKeySqlInfo.getPropertyExpression();
        String resolvedSqlFrag= binBinKeySqlInfo.getResolvedSqlFrg();
        int lBracketIndex= binBinKeySqlInfo.getlBarchetIndex();
        int rBracketIndex= binBinKeySqlInfo.getrBarchetIndex();
        if(rBracketIndex==-1||lBracketIndex==-1){
            throw new BinResloveSqlException(" lose this brackets :"+resolvedSqlFrag);
        }
        StringBuilder sb=new StringBuilder(resolvedSqlFrag.substring(0,resolvedSqlFrag.indexOf("(")+1));
        String strInBarackets= binBinKeySqlInfo.getStrInBarackets();
        List<String> mapKeyList= binBinKeySqlInfo.getMapKeyList();
        if(mapKeyList.size()!=1)
            throw new BinResloveSqlException(String.format(" just can one mapKey in bin SQL when use the '[IN' : %s ",
                    binBinKeySqlInfo.getBinKeySqlFragment()));
        String mapKey=mapKeyList.get(0);
        MetaObject paramMetaObj=configuration.newMetaObject(paramObject);
        propertyName=paramMetaObj.findProperty(propertyName,true);
        if(propertyName.length()>0){
            Object value=paramMetaObj.getValue(propertyName);
            if(value==null) return "";
            if(value instanceof Collection){
                Collection collection= (Collection) value;
                Iterator iterator = collection.iterator();
                int i=0;
                while (iterator.hasNext()){
                    Object rootPropertyValue=iterator.next();
                    MetaObject rootPropValMetaObj=configuration.newMetaObject(rootPropertyValue);
                    String subMapkeyAndSuffix=ParamterMapHelper.setSubPropValAndspliceSb(rootPropValMetaObj,mapKey,i,paramMetaObj);
                    sb.append(strInBarackets.replace(mapKey,subMapkeyAndSuffix));
                    i++;
                }
            }else if(value.getClass().isArray()){
                for (int i = 0; i < Array.getLength(value); i++) {
                    Object rootPropertyValue=Array.get(value,i);
                    MetaObject rootPropValMetaObj=configuration.newMetaObject(rootPropertyValue);
                    String subMapkeyAndSuffix=ParamterMapHelper.setSubPropValAndspliceSb(rootPropValMetaObj,mapKey,i,paramMetaObj);
                    sb.append(strInBarackets.replace(mapKey,subMapkeyAndSuffix));
                }
            }else{
                throw new BinResloveSqlException(String.format(" %s is not Collection or Array ,please check this bin SQL : %s",
                        paramObject, binBinKeySqlInfo.getBinKeySqlFragment()));
            }
            sb.delete(sb.length()-1,sb.length());
            sb.append(")");
        }
        return sb.toString();
    }


}
