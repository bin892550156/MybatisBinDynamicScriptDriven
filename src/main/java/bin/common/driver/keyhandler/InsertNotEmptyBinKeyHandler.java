package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * 对应 {@link bin.common.driver.BinKey#KEY_INSERT_NOT_EMPTY} 的业务处理器
 * <p>
 *     对拼装Insert SQL的功能加上即不为null又不为空字符的过滤条件
 * </p>
 */
public class InsertNotEmptyBinKeyHandler extends InsertBinKeyHandler{

    public InsertNotEmptyBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * 如果 {@code propertyName} 在 {@code paramObject} 对应的属性对象是 {@link String} 类型，即不为null又不是空字符串,就不进行过滤。
     * 如果 {@code propertyName}  在 {@code paramObject} 对应的属性对象不是{@link String} 类型，只要不为null,都不进行过滤
     * <p>
     *     优先使用父类的业务过滤，但父类业务认为 {@code propertyName} 不需要过滤时，才会进行本类的业务过滤。
     * </p>
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param propertyName 属性名
     * @param paramObject 参数对象
     * @return true为过滤，false为不过滤
     */
    @Override
    protected Filter filter(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, String propertyName, Object paramObject) {
        Filter filter=super.filter(binBinKeySqlInfo,propertyName,paramObject);
        if(filter==Filter.NONE){
            MetaObject paramMetaObj=configuration.newMetaObject(paramObject);
            Object value=paramMetaObj.getValue(propertyName);
            if(value==null ) return Filter.FILTER;
            if(value instanceof String){
                return ((String) value).isEmpty()?Filter.FILTER:Filter.NOT_FILTER;
            }
        }
        return filter;
    }
}
