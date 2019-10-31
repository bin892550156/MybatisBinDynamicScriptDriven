package bin.common.driver.helper;

import bin.common.driver.annotation.BinIgnoreMapping;
import bin.common.driver.annotation.BinMappingTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 反射帮助类
 */
public class ReflectorHelper {

    /**
     * 获取 {@code cls} 中所有被 {@link BinIgnoreMapping} 注释的属性名
     * @param cls 类
     * @return {@code cls} 中所有被 {@link BinIgnoreMapping} 注释的属性名
     */
    public static List<String> getIgnoreMapping(Class cls){
        Field[] declaredFields = cls.getDeclaredFields();
        List<String> propertyNameList=new ArrayList<>();
        for(Field field:declaredFields){
            BinIgnoreMapping binIgnoreMapping = field.getAnnotation(BinIgnoreMapping.class);
            if(binIgnoreMapping!=null){
                String propertyName=field.getName();
                propertyNameList.add(propertyName);
            }
        }
        return propertyNameList;
    }

    /**
     * 获取 {@code cls} 对应的 表名。
     * <p>
     *     取出在 {@code cls} 的 {@link BinMappingTable} 注解对象，以{@link BinMappingTable#value()}作为
     *     {@code cls} 的表名并返回。如果 {@code cls} 没有加上 {@link BinMappingTable},就用 {@code cls}的
     *     简单类名转成下划线形式名称返回
     * </p>
     * @param cls 类
     * @return {@code cls} 对应的 表名。
     */
    public static String getTableName(Class cls){
        String defaultTableName=FieldNameConversionHelper.humpToLine(cls.getSimpleName());
        BinMappingTable binMappingTable = (BinMappingTable) cls.getAnnotation(BinMappingTable.class);
        if(binMappingTable!=null){
            String value=binMappingTable.value();
            if(!value.isEmpty()){
                return value;
            }else{
                return defaultTableName;
            }
        }else{
            return defaultTableName;
        }
    }
}
