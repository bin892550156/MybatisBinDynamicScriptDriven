package bin.common.driver.helper;

import org.apache.ibatis.scripting.xmltags.ExpressionEvaluator;

/**
 * EL表达式帮助类
 */
public class ExpressionEvaluatorHelper {

    /**
     * EL表达式执行器
     */
    private final ExpressionEvaluator evaluator=new ExpressionEvaluator();;

    /**
     * 单例对象
     */
    private static final ExpressionEvaluatorHelper instance=new ExpressionEvaluatorHelper();

    private ExpressionEvaluatorHelper(){}

    /**
     * 获取唯一单例对象
     */
    public static ExpressionEvaluatorHelper getInstance() {
        return instance;
    }

    /**
     * 执行EL表达式，返回boolean结果
     * @param expression EL表达式
     * @param parameterObject 参数对象
     * @return EL表达式的结果值
     */
    public boolean evaluateBoolean(String expression, Object parameterObject) {
        expression = correctExpression(expression);
        return evaluator.evaluateBoolean(expression,parameterObject);
    }

    /**
     * 执行EL表达式，返回Iterable
     * @param expression EL表达式
     * @param parameterObject 参数对象
     * @return 执行EL表达式得出的Iterable对象
     */
    public Iterable<?> evaluateIterable(String expression, Object parameterObject) {
        return evaluator.evaluateIterable(expression,parameterObject);
    }

    /**
     * 调整EL表达式，将EL表达式为'IS_NOT_EMPTY'开头的字符串进行转化,即：'IS_NOT_EMPT 属性名' 转化成 '属性名 ！=null && 属性名 != '' '。
     * 目前只支持一个
     * @param expression EL表达式
     * @return 如果EL表达式是'IS_NOT_EMPT 属性名'形式的字符串，会返回'属性名 ！=null && 属性名 != '' '，否则直接返回字符串
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
