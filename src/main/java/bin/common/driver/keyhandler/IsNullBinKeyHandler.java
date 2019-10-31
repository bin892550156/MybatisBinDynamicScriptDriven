package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_IS_NULL} ��ҵ������
 * <p>
 *  ��� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} Ϊnull������
 *  {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,���򷵻ؿ��ַ���
 * </p>
 */
public class IsNullBinKeyHandler extends IsNotNullBinKeyHandler {

    public IsNullBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * �ж� {@code property} �� {@code metaObject} ��Ӧ�����Զ����Ƿ�Ϊnull��
     * @param propertyName ������
     * @param metaObject ����Ԫ����
     * @return {@code property} �� {@code metaObject} ��Ӧ�����Զ���ֻҪΪnull�����᷵��true�����򷵻�false
     */
    @Override
    protected boolean getCheckResult(String propertyName, MetaObject metaObject) {
        boolean checkResult=super.getCheckResult(propertyName, metaObject);
        return !checkResult;
    }
}
