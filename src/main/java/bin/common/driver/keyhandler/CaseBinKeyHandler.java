package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.iterator.ExpressionSqlFragIterator;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * 对应{@link bin.common.driver.BinKey#KEY_CASE}的业务处理器
 */
public class CaseBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatis全局配置信息
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public CaseBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 类似于 switch关键字用法
     * <p>
     * 以 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 为属性名，获取在{@code paramObject}
     * 对应的属性名的属性值，通过{@link ExpressionSqlFragIterator}对{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}
     * 进行解析成{@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}对象，找到对应
     * 属性值的{@link ExpressionSqlFragIterator.ExpressionSqlFrag#getExpression()}对象的{@code expression}属性的
     * {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}对象,返回对应的
     * {@link ExpressionSqlFragIterator.ExpressionSqlFrag#getSqlFrag()} },如果没有找到，返回'else'的{@code expression}属性的
     * {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}对象，还是没有才返回空字符串
     * </p>
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return 返回对应与{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}值对应的{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}
     *   {@link ExpressionSqlFragIterator.ExpressionSqlFrag#getSqlFrag()} },如果没有找到，返回'else'的{@code expression}属性的
     *   {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}对象，还是没有才返回空字符串
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        String propertyExpression= binBinKeySqlInfo.getPropertyExpression();
        MetaObject metaObject=configuration.newMetaObject(paramObject);
        ExpressionSqlFragIterator expressionSqlFragIterator =new ExpressionSqlFragIterator(binBinKeySqlInfo.getResolvedSqlFrg());
        String propertyName = metaObject.findProperty(propertyExpression, true);
        Object propVale = metaObject.getValue(propertyName);
        if(propVale==null) return expressionSqlFragIterator.getElseSqlFrag();
        while (expressionSqlFragIterator.hasNext()){
            ExpressionSqlFragIterator.ExpressionSqlFrag expressionSqlFrag = expressionSqlFragIterator.next();
            if(propertyName!=null && propertyName.length()>0){
                String caseVal= expressionSqlFrag.getExpression();
                String propValeStr=String.valueOf(propVale);
                if(caseVal.equals(propValeStr)|| expressionSqlFrag.isElse()){
                    return expressionSqlFrag.getSqlFrag();
                }
            }
        }
        return "";
    }

}
