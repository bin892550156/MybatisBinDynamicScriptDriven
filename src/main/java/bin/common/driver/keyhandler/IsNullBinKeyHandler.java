package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * 对应 {@link bin.common.driver.BinKey#KEY_IS_NULL} 的业务处理器
 * <p>
 *  如果 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 为null，返回
 *  {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,否则返回空字符串
 * </p>
 */
public class IsNullBinKeyHandler extends IsNotNullBinKeyHandler {

    public IsNullBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * 判断 {@code property} 在 {@code metaObject} 对应的属性对象是否为null，
     * @param propertyName 属性名
     * @param metaObject 参数元对象
     * @return {@code property} 在 {@code metaObject} 对应的属性对象，只要为null，都会返回true；否则返回false
     */
    @Override
    protected boolean getCheckResult(String propertyName, MetaObject metaObject) {
        boolean checkResult=super.getCheckResult(propertyName, metaObject);
        return !checkResult;
    }
}
