package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * 对应 {@link bin.common.driver.BinKey#KEY_UPDATE_NOT_NULL} 的业务处理器
 * <p>
 *     对拼装UPDATE SQL的功能加上不能为null的过滤条件
 * </p>
 */
public class UpdateNotNullBinKeyHandler extends UpdateBinKeyHandler{

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public UpdateNotNullBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * 如果 {@code propertyName} 在 {@code paramObject} 对应的属性对象不为null就不进行过滤。
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
            MetaObject paraMetaObj=configuration.newMetaObject(paramObject);
            Object object=paraMetaObj.getValue(propertyName);
            return object==null?Filter.FILTER:Filter.NOT_FILTER;
        }
        return filter;
    }
}
