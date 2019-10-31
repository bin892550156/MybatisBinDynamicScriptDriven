package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.ExpressionEvaluatorHelper;
import bin.common.driver.helper.ReflectorHelper;
import bin.common.driver.iterator.ExpressionSqlFragIterator;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * д����BinKey���������
 */
public abstract class WriteOperationBinKeyHandler implements BinKeyHandler{

    /**
     * Mybatisȫ��������Ϣ
     */
    protected Configuration configuration;
    /**
     * EL���ʽִ�а�����
     */
    private ExpressionEvaluatorHelper expressionEvaluatorHelper;
    /**
     * ������ �� EL���ʽ ӳ��
     */
    protected Map<String,String> propertyAndExpressionMap;

    /**
     * ���˲������ö��
     */
    protected enum Filter{
        /**
         * ������
         */
        NONE,
        /**
         * ����
         */
        FILTER,
        /**
         * ������
         */
        NOT_FILTER
    }

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public WriteOperationBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
        expressionEvaluatorHelper=ExpressionEvaluatorHelper.getInstance();
    }

    /**
     * ���� {@code paramObject} �����ԣ����˵����������������ԣ�ƴװ��Ӧ��SQL�ű����س�ȥ
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @return SQL�ű�
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        init(binBinKeySqlInfo,paramObject);
        Class paramCls=paramObject.getClass();
        Reflector reflector=new Reflector(paramCls);
        List<String> ignoreMappingProps = ReflectorHelper.getIgnoreMapping(paramCls);
        String[] properyNames=reflector.getGetablePropertyNames();
        String tableName=ReflectorHelper.getTableName(paramCls);
        List<String> mappingAblePropertyList=new ArrayList<>();
        for(String propertyName:properyNames){
            if(ignoreMappingProps.contains(propertyName)) continue;
            if(filter(binBinKeySqlInfo,propertyName,paramObject)==Filter.FILTER) continue;
            mappingAblePropertyList.add(propertyName);
        }

        String sql=generatorSQL(binBinKeySqlInfo,paramObject,tableName,mappingAblePropertyList);
        return sql;
    }

    /**
     * {@link #resolve(BinKeyAssigment.BinKeySqlInfo, Object)} �ʼ���õķ��������ڶ�ҵ�����ݵ�һЩ��ʼ��������
     * Ĭ��ʵ�ֳ�ʼ�� {@link #propertyAndExpressionMap} ,��{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}
     * ��ֵ���н�����װ�� {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag} ���󣬲�������
     * �� {@code sqlFrag} �� {@code expression} ��ӵ� {@link #propertyAndExpressionMap} �С�
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     */
    protected void init(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject){
        String resolvedSqlFrag= binBinKeySqlInfo.getResolvedSqlFrg();
        if(resolvedSqlFrag==null || resolvedSqlFrag.isEmpty()){
            propertyAndExpressionMap =new HashMap<>(0);
            return;
        }
        ExpressionSqlFragIterator expressionSqlFragIterator =new ExpressionSqlFragIterator(resolvedSqlFrag);
        propertyAndExpressionMap =new HashMap<>();
        while (expressionSqlFragIterator.hasNext()){
            ExpressionSqlFragIterator.ExpressionSqlFrag next = expressionSqlFragIterator.next();
            //ExpressionSqlFrag.sqlFrag��Ӧ��paramObject��������
            propertyAndExpressionMap.put(next.getSqlFrag().trim(),next.getExpression());
        }
    }

    /**
     * ����SQL�ű�
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @param tableName ����
     * @param mappingAblePropertyList �����������������б�
     * @return SQL�ű�
     */
    protected abstract String generatorSQL(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject,
                                           String tableName, List<String> mappingAblePropertyList);

    /**
     * �������ԣ�Ĭ�ϻ�ȡ {@code propertyName} ��Ӧ��EL���ʽ����ִ�У��������true���Ͳ����˸���������
     * ������˵������û���ҵ���Ӧ��EL���ʽʱ��Ĭ���ǲ��ø����ԡ�
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param propertyName ������
     * @param paramObject ��������
     * @return ����true��ʾ���˵������ԣ�����false��ʾ���ø����ԡ�
     */
    protected Filter filter(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, String propertyName, Object paramObject) {
        String expression = propertyAndExpressionMap.get(propertyName);
        if(expression!=null&&!expression.isEmpty()){
            boolean evaluateBoolean = expressionEvaluatorHelper.evaluateBoolean(expression, paramObject);
            return evaluateBoolean?Filter.NOT_FILTER:Filter.FILTER;
        }
        return Filter.NONE;
    }
}
