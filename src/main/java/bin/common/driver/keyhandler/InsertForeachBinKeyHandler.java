package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.exception.BinResloveSqlException;
import bin.common.driver.helper.FieldNameConversionHelper;
import bin.common.driver.helper.ReflectorHelper;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_INSERT_FOR_EACH} ��ҵ������
 * <p>
 *     ��������SQL��Ŀǰֻ��������MYSQL.
 * </p>
 */
public class InsertForeachBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatisȫ��������Ϣ
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public InsertForeachBinKeyHandler(Configuration configuration) {
        this.configuration=configuration;
    }

    /**
     * ���� 'Insert into ���� (����1,����2,����3) values (ֵ1,ֵ2,ֵ3),(ֵ1,ֵ2,ֵ3),(ֵ1,ֵ2,ֵ3)' ��SQL�ű�
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @return 'Insert into ���� (����1,����2,����3) values (ֵ1,ֵ2,ֵ3),(ֵ1,ֵ2,ֵ3),(ֵ1,ֵ2,ֵ3)' ��SQL�ű�
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        MetaObject paramMetaObj=configuration.newMetaObject(paramObject);
        Object rootObject=paramMetaObj.getValue(binBinKeySqlInfo.getPropertyExpression());
        Class rootCls= getClass(binBinKeySqlInfo,rootObject);
        //ȡ�������Ϳ�ӳ�������������
        Reflector reflector=new Reflector(rootCls);
        List<String> ignoreMappingProps = ReflectorHelper.getIgnoreMapping(rootCls);
        String[] properyNames=reflector.getGetablePropertyNames();
        String tableName=ReflectorHelper.getTableName(rootCls);
        List<String> mappingAblePropertyList=new ArrayList<>();
        for(String propertyName:properyNames){
            if(ignoreMappingProps.contains(propertyName)){
                continue;
            }
            mappingAblePropertyList.add(propertyName);
        }
        //����SQL�ű�
        StringBuilder sql=new StringBuilder();
        sql.append(" INSERT INTO ").append(tableName).append(" ( ");
        for(String propertyName:mappingAblePropertyList){
            String column= FieldNameConversionHelper.humpToLine(propertyName);
            sql.append(column).append(",");
        }
        sql.delete(sql.length()-1,sql.length());
        sql.append(" ) VALUES ");
        sql.append(assembleSqlFrag(rootObject,paramMetaObj,mappingAblePropertyList));
        return sql.toString();
    }

    /**
     * ƴװSQL�ű�
     * @param rootObject ������Ҫô���������Ҫô�� {@link Collection}
     * @param paramMetaObj ����Ԫ����
     * @param mappingAblePropertyList ����������������б�
     * @return '(ֵ1,ֵ2,ֵ3),(ֵ1,ֵ2,ֵ3),(ֵ1,ֵ2,ֵ3)'
     */
    private String assembleSqlFrag(Object rootObject,MetaObject paramMetaObj,List<String> mappingAblePropertyList){
        StringBuilder sqlFrag=new StringBuilder();
        int i=0;
        Class rootCls=rootObject.getClass();
        if(rootObject instanceof Collection){
            Iterator iterator = ((Collection) rootObject).iterator();
            while (iterator.hasNext()){
                sqlFrag.append("(");
                Object next = iterator.next();
                MetaObject nextMetaObj=configuration.newMetaObject(next);
                for(String propertyName:mappingAblePropertyList){
                    Object value = nextMetaObj.getValue(propertyName);
                    String mapkey=propertyName+i;
                    paramMetaObj.setValue(mapkey,value);
                    sqlFrag.append("#{").append(mapkey).append("},");
                }
                sqlFrag.delete(sqlFrag.length()-1,sqlFrag.length());
                sqlFrag.append("),");
                i++;
            }
            sqlFrag.delete(sqlFrag.length()-1,sqlFrag.length());
        }else if(rootCls.isArray()){
            int length = Array.getLength(rootObject);
            for (int j = 0; j < length; j++) {
                Object o = Array.get(rootObject, i);
                MetaObject oMetaObj=configuration.newMetaObject(o);
                for(String propertyName:mappingAblePropertyList){
                    Object value = oMetaObj.getValue(propertyName);
                    String mapkey=propertyName+i;
                    paramMetaObj.setValue(mapkey,value);
                    sqlFrag.append("#{").append(mapkey).append("),");
                }
                sqlFrag.delete(sqlFrag.length()-1,sqlFrag.length());
                sqlFrag.append("),");
                i++;
            }
            sqlFrag.delete(sqlFrag.length()-1,sqlFrag.length());
        }
        return sqlFrag.toString();
    }

    /**
     * ��ȡ {@code paramObject} ���࣬��� {@code paramObject} Ϊ {@link Collection} ȡ��һ��Ԫ�أ�
     * ��ȡ��Ԫ�ص��ಢ�����ء���� {@code paramObject} ���������ȡ����Ԫ�ص��ಢ���ء�
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @throws BinResloveSqlException
     *          <ol>
     *              <li>��� {@code rootObject} ������������ֲ��� {@link Collection} ����</li>
     *              <li>��� {@code paramObject} Ϊ {@link Collection},����Ϊ�յ�{@link Collection} ����ʱ</li>
     *          </ol>
     * @return {@code paramObject} ����,��� {@code paramObject} Ϊ {@link Collection} ���� ���� ������󣬾�ȡ��Ԫ����
     */
    private Class getClass(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject){
        Class paramCls=paramObject.getClass();
        if(paramObject instanceof Collection){
            Object next=((Collection) paramObject).iterator().next();
            if(next!=null){
               return next.getClass();
            }else{
                throw new BinResloveSqlException(String.format(" is not element in the %s ,please check the bin SQL : %s ",
                        binBinKeySqlInfo.getPropertyExpression(), binBinKeySqlInfo.getBinKeySqlFragment()));
            }
        }else if (paramCls.isArray()){
           return paramCls.getComponentType();
        }else{
            throw new BinResloveSqlException(String.format(" %s is not Collection or Array ,please check this bin SQL : %s",
                    paramObject, binBinKeySqlInfo.getBinKeySqlFragment()));
        }
    }

}

