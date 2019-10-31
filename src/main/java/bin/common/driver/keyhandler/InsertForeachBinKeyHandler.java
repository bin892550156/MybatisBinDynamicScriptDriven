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
 * 对应 {@link bin.common.driver.BinKey#KEY_INSERT_FOR_EACH} 的业务处理器
 * <p>
 *     批量插入SQL，目前只是适用于MYSQL.
 * </p>
 */
public class InsertForeachBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatis全局配置信息
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public InsertForeachBinKeyHandler(Configuration configuration) {
        this.configuration=configuration;
    }

    /**
     * 生成 'Insert into 表名 (列名1,列名2,列名3) values (值1,值2,值3),(值1,值2,值3),(值1,值2,值3)' 的SQL脚本
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return 'Insert into 表名 (列名1,列名2,列名3) values (值1,值2,值3),(值1,值2,值3),(值1,值2,值3)' 的SQL脚本
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        MetaObject paramMetaObj=configuration.newMetaObject(paramObject);
        Object rootObject=paramMetaObj.getValue(binBinKeySqlInfo.getPropertyExpression());
        Class rootCls= getClass(binBinKeySqlInfo,rootObject);
        //取出表名和可映射的属性名列名
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
        //生成SQL脚本
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
     * 拼装SQL脚本
     * @param rootObject 根对象，要么是数组对象，要么是 {@link Collection}
     * @param paramMetaObj 参数元对象
     * @param mappingAblePropertyList 符合需求的属性名列表
     * @return '(值1,值2,值3),(值1,值2,值3),(值1,值2,值3)'
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
     * 获取 {@code paramObject} 的类，如果 {@code paramObject} 为 {@link Collection} 取出一个元素，
     * 获取其元素的类并并返回。如果 {@code paramObject} 是数组对象，取出其元素的类并返回。
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @throws BinResloveSqlException
     *          <ol>
     *              <li>如果 {@code rootObject} 不是数组对象，又不是 {@link Collection} 对象</li>
     *              <li>如果 {@code paramObject} 为 {@link Collection},但是为空的{@link Collection} 对象时</li>
     *          </ol>
     * @return {@code paramObject} 的类,如果 {@code paramObject} 为 {@link Collection} 对象 或者 数组对象，就取其元素类
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

