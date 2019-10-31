package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.iterator.PropertyNameIterator;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * 对应 {@link bin.common.driver.BinKey#KEY_INSERT_NOT_NULL} 的业务处理器
 * <p>
 *     如果 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 为null或者是空字符时，返回
 *     {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,否则返回空字符串
 * </p>
 */
public class IsNotEmplyBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatis全局配置信息
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public IsNotEmplyBinKeyHandler(Configuration configuration) {
        this.configuration=configuration;
    }

    /**
     * 如果 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 的执行结果即为null且不是空字符时，返回
     * {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,否则返回空字符串。
     * <p>
     *     支持 '&' 和 '|'的表达式判断
     * </p>
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return  如果 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}的执行结果 即为null且不是空字符时，返回
     *       {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,否则返回空字符串。
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        String resolvedSql= binBinKeySqlInfo.getResolvedSqlFrg();
        MetaObject metaObject = configuration.newMetaObject(paramObject);
        String propertyExpression= binBinKeySqlInfo.getPropertyExpression();
        PropertyNameIterator propertyNameIterator=new PropertyNameIterator(propertyExpression);
        char checkCondition=propertyNameIterator.getCheckCondition();
        boolean flag=false;
        while (propertyNameIterator.hasNext()){
            String propertyName=propertyNameIterator.next();
            boolean result= getCheckResult(propertyName,metaObject);
            if(checkCondition== PropertyNameIterator.CHAR_AND){
                if(result) {
                    flag=true;
                }else{
                    flag=false;
                }
            }else if(checkCondition== PropertyNameIterator.CHAR_OR){
                if(result) {
                    flag=true;
                    break;
                }
            }else{//not '&' and '|'
                flag=result;
            }
        }
        return flag?resolvedSql:"";
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
     *          如果不为null且不是空字符串时返回true；否则返回false</li>
     *          <li> {@code property} 在 {@code metaObject} 对应的属性对象为{@link Collection} 或者是 数组对象，
     *          如果有元素在里面返回true，否则返回false</li>
     *          <li>{@code property} 在 {@code metaObject} 对应的属性对象为其他类型，只要不为null，都会返回true；否则返回false</li>
     *      </ol>
     */
    protected boolean getCheckResult(String propertyName, MetaObject metaObject) {
        propertyName = metaObject.findProperty(propertyName, true);
        if (propertyName!=null&&propertyName.length() > 0) {
            Object value = metaObject.getValue(propertyName);
            if (value == null) return false;
            if (value instanceof String) {
                String str = (String) value;
                if (str.isEmpty())
                    return false;
            } else if (value instanceof Collection) {
                if (((Collection) value).isEmpty())
                    return false;
            } else if (value.getClass().isArray()) {
                if (Array.getLength(value) == 0)
                    return false;
            }
        }
        return true;
    }

}
