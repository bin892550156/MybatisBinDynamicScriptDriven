package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.util.Collection;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_IS_EMPTY} ��ҵ������
 * <p>
 *  ��� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} ��Ϊnull������
 *  {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,���򷵻ؿ��ַ���
 * </p>
 */
public class IsEmplyBinKeyHandler extends IsNotEmplyBinKeyHandler {

    /**
     *
     * @param configuration Mybatsiȫ��������Ϣ
     */
    public IsEmplyBinKeyHandler(Configuration configuration) {
        super(configuration);
    }


    /**
     * �ж� {@code property} �� {@code metaObject} ��Ӧ�����Զ����Ƿ�Ϊnull��������Զ���Ϊ�ַ�����
     * �����ж��Ƿ��ǿ��ַ�����������Զ���Ϊ {@link Collection} ������ ������󣬻��ж��Ƿ�û��Ԫ�أ�
     * ���Զ������������ͣ���ֻ����ж��Ƿ�Ϊnull��
     * @param propertyName ������
     * @param metaObject ����Ԫ����
     * @return
     *      <ol>
     *          <li> {@code property} �� {@code metaObject} ��Ӧ�����Զ���Ϊ�ַ�����
     *          �����Ϊnull�Ҳ��ǿ��ַ���ʱ����false�����򷵻�true</li>
     *          <li> {@code property} �� {@code metaObject} ��Ӧ�����Զ���Ϊ{@link Collection} ������ �������
     *          �����Ԫ�������淵��false�����򷵻�true</li>
     *          <li>{@code property} �� {@code metaObject} ��Ӧ�����Զ���Ϊ�������ͣ�ֻҪ��Ϊnull�����᷵��false�����򷵻�true</li>
     *      </ol>
     */
    @Override
    protected boolean getCheckResult(String propertyName, MetaObject metaObject) {
        boolean checkResult=super.getCheckResult(propertyName, metaObject);
        return !checkResult;
    }
}
