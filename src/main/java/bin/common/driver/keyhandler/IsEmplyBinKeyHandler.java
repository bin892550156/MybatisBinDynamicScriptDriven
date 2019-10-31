package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.util.Collection;

/**
 * 对应 {@link bin.common.driver.BinKey#KEY_IS_EMPTY} 的业务处理器
 * <p>
 *  如果 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 即为null，返回
 *  {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,否则返回空字符串
 * </p>
 */
public class IsEmplyBinKeyHandler extends IsNotEmplyBinKeyHandler {

    /**
     *
     * @param configuration Mybatsi全局配置信息
     */
    public IsEmplyBinKeyHandler(Configuration configuration) {
        super(configuration);
    }


    /**
     * 判断 {@code property} 在 {@code metaObject} 对应的属性对象是否不为null，如果属性对象为字符串，
     * 还会判断是否是空字符串；如果属性对象为 {@link Collection} 或者是 数组对象，会判断是否没有元素；
     * 属性对象是其他类型，都只会简单判断是否为null，
     * @param propertyName 属性名
     * @param metaObject 参数元对象
     * @return
     *      <ol>
     *          <li> {@code property} 在 {@code metaObject} 对应的属性对象为字符串，
     *          如果不为null且不是空字符串时返回false；否则返回true</li>
     *          <li> {@code property} 在 {@code metaObject} 对应的属性对象为{@link Collection} 或者是 数组对象，
     *          如果有元素在里面返回false，否则返回true</li>
     *          <li>{@code property} 在 {@code metaObject} 对应的属性对象为其他类型，只要不为null，都会返回false；否则返回true</li>
     *      </ol>
     */
    @Override
    protected boolean getCheckResult(String propertyName, MetaObject metaObject) {
        boolean checkResult=super.getCheckResult(propertyName, metaObject);
        return !checkResult;
    }
}
