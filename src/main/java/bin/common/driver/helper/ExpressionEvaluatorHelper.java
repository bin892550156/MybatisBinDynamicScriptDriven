package bin.common.driver.helper;

import org.apache.ibatis.scripting.xmltags.ExpressionEvaluator;

/**
 * EL���ʽ������
 */
public class ExpressionEvaluatorHelper {

    /**
     * EL���ʽִ����
     */
    private final ExpressionEvaluator evaluator=new ExpressionEvaluator();;

    /**
     * ��������
     */
    private static final ExpressionEvaluatorHelper instance=new ExpressionEvaluatorHelper();

    private ExpressionEvaluatorHelper(){}

    /**
     * ��ȡΨһ��������
     */
    public static ExpressionEvaluatorHelper getInstance() {
        return instance;
    }

    /**
     * ִ��EL���ʽ������boolean���
     * @param expression EL���ʽ
     * @param parameterObject ��������
     * @return EL���ʽ�Ľ��ֵ
     */
    public boolean evaluateBoolean(String expression, Object parameterObject) {
        expression = correctExpression(expression);
        return evaluator.evaluateBoolean(expression,parameterObject);
    }

    /**
     * ִ��EL���ʽ������Iterable
     * @param expression EL���ʽ
     * @param parameterObject ��������
     * @return ִ��EL���ʽ�ó���Iterable����
     */
    public Iterable<?> evaluateIterable(String expression, Object parameterObject) {
        return evaluator.evaluateIterable(expression,parameterObject);
    }

    /**
     * ����EL���ʽ����EL���ʽΪ'IS_NOT_EMPTY'��ͷ���ַ�������ת��,����'IS_NOT_EMPT ������' ת���� '������ ��=null && ������ != '' '��
     * Ŀǰֻ֧��һ��
     * @param expression EL���ʽ
     * @return ���EL���ʽ��'IS_NOT_EMPT ������'��ʽ���ַ������᷵��'������ ��=null && ������ != '' '������ֱ�ӷ����ַ���
     */
    private String correctExpression(String expression) {
        StringBuilder expressionSb=new StringBuilder(expression);
        String key="IS_NOT_EMPTY";
        int index=expression.indexOf(key);
        while (index!=-1){
            int propertyStartIndex=key.length()+1;
            if(propertyStartIndex<expression.length()-1){
                int propertyEndIndex=expression.indexOf(" ",propertyStartIndex);
                if(propertyEndIndex==-1){
                    propertyEndIndex=expression.length()-1;
                }
                String property=expression.substring(propertyStartIndex,propertyEndIndex);
                String newPropExpression=String.format(" %s !=null && %s !='' ",property,property);
                expressionSb.replace(index,propertyEndIndex,newPropExpression);
            }
        }
        return expressionSb.toString();
    }
}
