package bin.common.driver.helper;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;

/**
 * 参数映射帮助类
 */
public class ParamterMapHelper {


    /**
     * 取出 {@code propertyMapKey} 在 {@code metaPropVal} 对应的值对象，添加到 {@code paramMetaObj} 中，其键名为
     * 调整后的 {@code propertyMapKey} 加上 {@code suffix}
     * @param metaPropVal 属性元对象
     * @param propertyMapKey 属性对象映射键
     * @param suffix 后缀
     * @param paramMetaObj 参数元对象
     * @return 在{@code paramMetaObj}中，{@code propertyMapKey} 在 {@code metaPropVal} 对应的值对象 的键名，键名拼装格式为
     *          {@code propertyMapKey} 加上 {@code suffix}
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
     * 调整映射键名，将第一个'.'前面的字符串去掉并返回。eg.'order.user.name' =>'user.name'
     * @param mapKey 映射键名
     * @return 将第一个'.'前面的字符串去掉并返回
     */
    public static String adjustMapKey(String mapKey){
        int dotIndex=mapKey.indexOf('.');
        if(dotIndex!=-1){
            mapKey=mapKey.substring(dotIndex+1);
        }
        return mapKey;
    }
}
