package bin.common.driver.helper;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;

/**
 * ����ӳ�������
 */
public class ParamterMapHelper {


    /**
     * ȡ�� {@code propertyMapKey} �� {@code metaPropVal} ��Ӧ��ֵ������ӵ� {@code paramMetaObj} �У������Ϊ
     * ������� {@code propertyMapKey} ���� {@code suffix}
     * @param metaPropVal ����Ԫ����
     * @param propertyMapKey ���Զ���ӳ���
     * @param suffix ��׺
     * @param paramMetaObj ����Ԫ����
     * @return ��{@code paramMetaObj}�У�{@code propertyMapKey} �� {@code metaPropVal} ��Ӧ��ֵ���� �ļ���������ƴװ��ʽΪ
     *          {@code propertyMapKey} ���� {@code suffix}
     */
    public static String setSubPropValAndspliceSb(MetaObject metaPropVal, String propertyMapKey, int suffix, MetaObject paramMetaObj){
        String subMapKey=adjustMapKey(propertyMapKey);
        Object propertyValue=null;
        try{
            propertyValue=metaPropVal.getValue(subMapKey);
        }catch (ReflectionException e){
            //may be metaPropVal is base class,e.g.'String','Integer','Double',and so on.
            propertyValue=metaPropVal.getOriginalObject();
        }
        String subMapkeyAndSuffix=subMapKey+suffix;
        paramMetaObj.setValue(subMapkeyAndSuffix,propertyValue);
        return subMapkeyAndSuffix;

    }

    /**
     * ����ӳ�����������һ��'.'ǰ����ַ���ȥ�������ء�eg.'order.user.name' =>'user.name'
     * @param mapKey ӳ�����
     * @return ����һ��'.'ǰ����ַ���ȥ��������
     */
    public static String adjustMapKey(String mapKey){
        int dotIndex=mapKey.indexOf('.');
        if(dotIndex!=-1){
            mapKey=mapKey.substring(dotIndex+1);
        }
        return mapKey;
    }
}
