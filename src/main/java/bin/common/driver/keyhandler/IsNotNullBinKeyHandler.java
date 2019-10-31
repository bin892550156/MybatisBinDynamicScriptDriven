package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_IS_NOT_NULL} ��ҵ������
 * <p>
 *  ��� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} ��Ϊnull������
 *  {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,���򷵻ؿ��ַ���
 * </p>
 */
public class IsNotNullBinKeyHandler extends IsNotEmplyBinKeyHandler {

    /**
     *
     * @param configuration Mybatsiȫ��������Ϣ
     */
    public IsNotNullBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * �ж� {@code property} �� {@code metaObject} ��Ӧ�����Զ����Ƿ�Ϊnull��
     * @param propertyName ������
     * @param metaObject ����Ԫ����
     * @return {@code property} �� {@code metaObject} ��Ӧ�����Զ���ֻҪ��Ϊnull�����᷵��true�����򷵻�false
     */
    @Override
    protected boolean getCheckResult(String propertyName, MetaObject metaObject) {
        propertyName = metaObject.findProperty(propertyName, true);
        if (propertyName!=null&&propertyName.length() > 0) {
            Object value = metaObject.getValue(propertyName);
            if (value == null) return false;
        }
        return true;
    }
}
