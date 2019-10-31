package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.ExpressionEvaluatorHelper;
import org.apache.ibatis.session.Configuration;

/**
 * ��Ӧ{@link bin.common.driver.BinKey#KEY_IF}��ҵ��������ҵ���������� &lt;if&gt; ��ǩ
 */
public class IfBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatis ȫ��������Ϣ
     */
    private Configuration configuration;
    /**
     * EL���ʽ������
     */
    private final ExpressionEvaluatorHelper evaluator;

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public IfBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
        evaluator=ExpressionEvaluatorHelper.getInstance();
    }

    /**
     * ҵ���������� &lt;if&gt; ��ǩ
     * <p>
     * ȡ {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} ��Ϊ���ʽ��ʹ��
     *  {@link ExpressionEvaluatorHelper#evaluateBoolean(String, Object)} �Ա��ʽ���ݱ���ִ�еó�
     *  {@code boolean} ֵ,���Ϊtrue,���� {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()};
     *  ���򣬷��ؿ��ַ���
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @return ʹ�� {@link ExpressionEvaluatorHelper#evaluateBoolean(String, Object)} �Ա��ʽ���ݱ���ִ�еó�
     *       {@code boolean} ֵ,���Ϊtrue,���� {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()};
     *       ���򣬷��ؿ��ַ���
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
