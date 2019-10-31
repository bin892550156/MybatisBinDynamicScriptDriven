package bin.common.driver.helper;

import bin.common.driver.annotation.BinIgnoreMapping;
import bin.common.driver.annotation.BinMappingTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * ���������
 */
public class ReflectorHelper {

    /**
     * ��ȡ {@code cls} �����б� {@link BinIgnoreMapping} ע�͵�������
     * @param cls ��
     * @return {@code cls} �����б� {@link BinIgnoreMapping} ע�͵�������
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
     * ��ȡ {@code cls} ��Ӧ�� ������
     * <p>
     *     ȡ���� {@code cls} �� {@link BinMappingTable} ע�������{@link BinMappingTable#value()}��Ϊ
     *     {@code cls} �ı��������ء���� {@code cls} û�м��� {@link BinMappingTable},���� {@code cls}��
     *     ������ת���»�����ʽ���Ʒ���
     * </p>
     * @param cls ��
     * @return {@code cls} ��Ӧ�� ������
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
