package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.ExpressionEvaluatorHelper;
import org.apache.ibatis.session.Configuration;

/**
 * 对应{@link bin.common.driver.BinKey#KEY_IF}的业务处理器，业务功能类似于 &lt;if&gt; 标签
 */
public class IfBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatis 全局配置信息
     */
    private Configuration configuration;
    /**
     * EL表达式帮助类
     */
    private final ExpressionEvaluatorHelper evaluator;

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public IfBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
        evaluator=ExpressionEvaluatorHelper.getInstance();
    }

    /**
     * 业务功能类似于 &lt;if&gt; 标签
     * <p>
     * 取 {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} 作为表达式，使用
     *  {@link ExpressionEvaluatorHelper#evaluateBoolean(String, Object)} 对表达式内容编译执行得出
     *  {@code boolean} 值,如果为true,返回 {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()};
     *  否则，返回空字符串
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return 使用 {@link ExpressionEvaluatorHelper#evaluateBoolean(String, Object)} 对表达式内容编译执行得出
     *       {@code boolean} 值,如果为true,返回 {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()};
     *       否则，返回空字符串
     * </p>
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        String expression= binBinKeySqlInfo.getPropertyExpression();
        if(evaluator.evaluateBoolean(expression,paramObject)){
            return binBinKeySqlInfo.getResolvedSqlFrg();
        }
        return "";
    }
}
