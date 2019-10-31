package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.iterator.ExpressionSqlFragIterator;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * ��Ӧ{@link bin.common.driver.BinKey#KEY_CASE}��ҵ������
 */
public class CaseBinKeyHandler implements BinKeyHandler {

    /**
     * Mybatisȫ��������Ϣ
     */
    private Configuration configuration;

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public CaseBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * ������ switch�ؼ����÷�
     * <p>
     * �� {@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()} Ϊ����������ȡ��{@code paramObject}
     * ��Ӧ��������������ֵ��ͨ��{@link ExpressionSqlFragIterator}��{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}
     * ���н�����{@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}�����ҵ���Ӧ
     * ����ֵ��{@link ExpressionSqlFragIterator.ExpressionSqlFrag#getExpression()}�����{@code expression}���Ե�
     * {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}����,���ض�Ӧ��
     * {@link ExpressionSqlFragIterator.ExpressionSqlFrag#getSqlFrag()} },���û���ҵ�������'else'��{@code expression}���Ե�
     * {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}���󣬻���û�вŷ��ؿ��ַ���
     * </p>
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @return ���ض�Ӧ��{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}ֵ��Ӧ��{@link BinKeyAssigment.BinKeySqlInfo#getPropertyExpression()}
     *   {@link ExpressionSqlFragIterator.ExpressionSqlFrag#getSqlFrag()} },���û���ҵ�������'else'��{@code expression}���Ե�
     *   {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag}���󣬻���û�вŷ��ؿ��ַ���
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
