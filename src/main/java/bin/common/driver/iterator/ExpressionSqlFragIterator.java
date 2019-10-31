package bin.common.driver.iterator;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.exception.BinResloveSqlException;

import java.util.Iterator;

/**
 * 表达式与SQL碎片解析迭代器，针对：'属性表达式1@SQL碎片1,属性表达式2@SQL碎片2,属性表达式3@SQL碎片3...'解析成
 * {@link ExpressionSqlFrag} 对象
 */
public  class ExpressionSqlFragIterator implements Iterator<ExpressionSqlFragIterator.ExpressionSqlFrag> {

    /**
     * {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}
     */
    private String resolvedSqlFrag;
    /**
     * {@link #resolvedSqlFrag}的字符数组
     */
    private char[] resolvedSqlFragCharArr;
    /**
     * 当前索引位置
     */
    private int currentIndex;
    /**
     * 当前'@'的索引位置
     */
    private int currentAIndex;
    /**
     * 下一个解析出来的{@link ExpressionSqlFrag}对象
     */
    private ExpressionSqlFrag next;

    /**
     *
     * @param resolvedSqlFrag  {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}
     */
    public ExpressionSqlFragIterator(String resolvedSqlFrag){
        this.resolvedSqlFrag=resolvedSqlFrag;
        this.resolvedSqlFragCharArr=resolvedSqlFrag.toCharArray();
        currentIndex=0;
    }

    /**
     * 判断是否还有下一个{@link ExpressionSqlFrag}.
     * <p>
     *     通过遍历{@link #resolvedSqlFragCharArr}找到符合解析封装成{@link ExpressionSqlFrag}的表达式，
     *     即'属性表达式1@SQL碎片1,'。就对其进行解析封装成{@link ExpressionSqlFrag}对象，如果封装完成会
     *     返回true;否则返回false
     * </p>
     * @return 如果成功封装 {@link ExpressionSqlFrag} 对象则返回true；否则返回false
     */
    @Override
    public boolean hasNext() {
        int length=resolvedSqlFragCharArr.length;
        String caseVal=null;
        String sqlFrag=null;
        int commaIndex=currentIndex;
        int aIndex=currentAIndex;
        ExpressionSqlFrag expressionSqlFrag =new ExpressionSqlFrag();
        for(int i=currentIndex+1;i<length;i++){
            char c=resolvedSqlFragCharArr[i];
            if(c=='@'){
                aIndex=i;
                caseVal=resolvedSqlFrag.substring(commaIndex==0?commaIndex:commaIndex+1,aIndex);
                expressionSqlFrag.setExpression(caseVal);
            }
            if(c==','){
                commaIndex=i;
                sqlFrag=resolvedSqlFrag.substring(aIndex+1,commaIndex);
                expressionSqlFrag.setSqlFrag(sqlFrag);
                if(caseVal==null) {
                    aIndex=i;
                    caseVal="";
                    expressionSqlFrag.setExpression(caseVal);
                }
            }else if(i==length-1){
                sqlFrag=resolvedSqlFrag.substring(aIndex+1,length);
                expressionSqlFrag.setSqlFrag(sqlFrag);
                if(caseVal==null) {
                    aIndex=i;
                    caseVal="";
                    expressionSqlFrag.setExpression(caseVal);
                }
            }
            if(expressionSqlFrag.complete()){
                currentIndex=i;
                currentAIndex=aIndex;
                next= expressionSqlFrag;
                return true;
            }
        }

        return false;
    }

    /**
     * 返回下一个已解析封装完的{@link ExpressionSqlFrag} 对象
     * @return 下一个已解析封装完的{@link ExpressionSqlFrag} 对象
     */
    @Override
    public ExpressionSqlFrag next() {
        return next;
    }

    /**
     * 解析'ELSE@SQL碎片1,'或者'else@SQL碎片1'的表达式，返回'SQL碎片1'.
     * 如果没有找到对应表达式，返回空字符串
     * @return 返回'SQL碎片1',如果没有找到对应表达式，返回空字符串
     */
    public String getElseSqlFrag(){
        int elseIndex=resolvedSqlFrag.lastIndexOf("ELSE");
        if(elseIndex==-1){
            elseIndex=resolvedSqlFrag.lastIndexOf("else");
            if(elseIndex==-1){
                return "";
            }
        }
        int length=resolvedSqlFrag.length();
        int startIndex=elseIndex+5; //5='else'.length+'@'.length
        if(startIndex<=length-1){
            throw new BinResloveSqlException("no found sql Fragment of 'ELSE' or 'else' on :"+resolvedSqlFrag);
        }
        String sqlFrag=resolvedSqlFrag.substring(elseIndex+5,length);
        return sqlFrag;
    }

    /**
     * 属性表达式 + SQL碎片 的封装类
     */
    public static class ExpressionSqlFrag {
        /**
         * 属性表达式
         */
        String expression;
        /**
         * SQL碎片
         */
        String sqlFrag;

        public String getExpression() {
            return expression;
        }

        public void setExpression(String expression) {
            this.expression = expression;
        }

        public String getSqlFrag() {
            return sqlFrag;
        }

        public void setSqlFrag(String sqlFrag) {
            this.sqlFrag = sqlFrag;
        }

        /**
         * 当 {@link #sqlFrag} 和{@link #expression} 都不为null时，返回true，否则返回false
         */
        public boolean complete(){
            return sqlFrag!=null&& expression !=null;
        }

        /**
         * {@link #expression} 是否是 'ELSE'或者是'else'
         * @return 是返回ture,否则返回false
         */
        public boolean isElse(){
            return "ELSE".equals(expression)||"else".equals(expression);
        }
        @Override
        public String toString() {
            return "CaseValAndSqlFrag{" +
                    "caseVal='" + expression + '\'' +
                    ", sqlFrag='" + sqlFrag + '\'' +
                    '}';
        }
    }
}

