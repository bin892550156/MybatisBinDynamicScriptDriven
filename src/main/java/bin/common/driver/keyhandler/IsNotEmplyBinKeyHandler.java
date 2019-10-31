package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.iterator.PropertyNameIterator;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_INSERT_NOT_NULL} ��ҵ������
 * <p>
 *     ��� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} Ϊnull�����ǿ��ַ�ʱ������
 *     {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,���򷵻ؿ��ַ���
 * </p>
 */
public class IsNotEmplyBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatisȫ��������Ϣ
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public IsNotEmplyBinKeyHandler(Configuration configuration) {
        this.configuration=configuration;
    }

    /**
     * ��� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} ��ִ�н����Ϊnull�Ҳ��ǿ��ַ�ʱ������
     * {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,���򷵻ؿ��ַ�����
     * <p>
     *     ֧�� '&' �� '|'�ı��ʽ�ж�
     * </p>
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @return  ��� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}��ִ�н�� ��Ϊnull�Ҳ��ǿ��ַ�ʱ������
     *       {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()} ,���򷵻ؿ��ַ�����
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
     * �ж� {@code property} �� {@code metaObject} ��Ӧ�����Զ����Ƿ�Ϊnull��������Զ���Ϊ�ַ�����
     * �����ж��Ƿ��ǿ��ַ�����������Զ���Ϊ {@link Collection} ������ ������󣬻��ж��Ƿ�û��Ԫ�أ�
     * ���Զ������������ͣ���ֻ����ж��Ƿ�Ϊnull��
     * @param propertyName ������
     * @param metaObject ����Ԫ����
     * @return
     *      <ol>
     *          <li> {@code property} �� {@code metaObject} ��Ӧ�����Զ���Ϊ�ַ�����
     *          �����Ϊnull�Ҳ��ǿ��ַ���ʱ����true�����򷵻�false</li>
     *          <li> {@code property} �� {@code metaObject} ��Ӧ�����Զ���Ϊ{@link Collection} ������ �������
     *          �����Ԫ�������淵��true�����򷵻�false</li>
     *          <li>{@code property} �� {@code metaObject} ��Ӧ�����Զ���Ϊ�������ͣ�ֻҪ��Ϊnull�����᷵��true�����򷵻�false</li>
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
