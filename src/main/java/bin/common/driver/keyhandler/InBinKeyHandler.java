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
 * ��Ӧ{@link bin.common.driver.BinKey#KEY_IN}��ҵ��������ҵ���ܵ�ͬ�ڣ�
 * <p>
 *     ���� in <br/>
 *     &lt;FOREACH item='������' index='i' open='(' colse=')' separtor=',' collection='�����б�'&gt; <br/>
 *             &nbsp;&nbsp;&nbsp; #{������.������} <br/>
 *     &lt;/FOREACH&gt; <br/>
 * </p>
 */
public class InBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatis ȫ��������Ϣ
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public InBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * ��{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}��Ϊ����������{@code paramObject}�л�ȡ��Ӧ
     * ����, ͨ�����������{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}�Լ�
     * {@link BinKeyAssigment.BinKeySqlInfo#getMapKeyList()}��Ԫ�أ�ƴװ��' ���� in (#{mayKey1},#{mapKey}...) ',
     * mayKeyΪ{@link BinKeyAssigment.BinKeySqlInfo#getMapKeyList()}��Ԫ��.����mayKey��Ӧ������ֵ��ӵ�{@code paramObject}��
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @return ����ҵ� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} �� {@code paramObject} ��Ӧ�����ԣ�
     *  �ͷ���SQL��Ƭ��' ���� in (#{mayKey1},#{mapKey}...) ',���򷵻ؿ��ַ���
     * @throws BinResloveSqlException
     *         <ol>
     *             <li>{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}��ȱʧ����ʱ�׳� </li>
     *             <li>{@link BinKeyAssigment.BinKeySqlInfo#getMapKeyList()}���ֶ��ʱ</li>
     *             <li>{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} �� {@code paramObject} ��Ӧ�����Զ�����
     *                  {@link Collection} ���� �������� ʱ
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
