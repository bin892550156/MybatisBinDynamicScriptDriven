package bin.common.driver.iterator;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.exception.BinResloveSqlException;

import java.util.Iterator;

/**
 * ���ʽ��SQL��Ƭ��������������ԣ�'���Ա��ʽ1@SQL��Ƭ1,���Ա��ʽ2@SQL��Ƭ2,���Ա��ʽ3@SQL��Ƭ3...'������
 * {@link ExpressionSqlFrag} ����
 */
public  class ExpressionSqlFragIterator implements Iterator<ExpressionSqlFragIterator.ExpressionSqlFrag> {

    /**
     * {@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}
     */
    private String resolvedSqlFrag;
    /**
     * {@link #resolvedSqlFrag}���ַ�����
     */
    private char[] resolvedSqlFragCharArr;
    /**
     * ��ǰ����λ��
     */
    private int currentIndex;
    /**
     * ��ǰ'@'������λ��
     */
    private int currentAIndex;
    /**
     * ��һ������������{@link ExpressionSqlFrag}����
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
     * �ж��Ƿ�����һ��{@link ExpressionSqlFrag}.
     * <p>
     *     ͨ������{@link #resolvedSqlFragCharArr}�ҵ����Ͻ�����װ��{@link ExpressionSqlFrag}�ı��ʽ��
     *     ��'���Ա��ʽ1@SQL��Ƭ1,'���Ͷ�����н�����װ��{@link ExpressionSqlFrag}���������װ��ɻ�
     *     ����true;���򷵻�false
     * </p>
     * @return ����ɹ���װ {@link ExpressionSqlFrag} �����򷵻�true�����򷵻�false
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
     * ������һ���ѽ�����װ���{@link ExpressionSqlFrag} ����
     * @return ��һ���ѽ�����װ���{@link ExpressionSqlFrag} ����
     */
    @Override
    public ExpressionSqlFrag next() {
        return next;
    }

    /**
     * ����'ELSE@SQL��Ƭ1,'����'else@SQL��Ƭ1'�ı��ʽ������'SQL��Ƭ1'.
     * ���û���ҵ���Ӧ���ʽ�����ؿ��ַ���
     * @return ����'SQL��Ƭ1',���û���ҵ���Ӧ���ʽ�����ؿ��ַ���
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
     * ���Ա��ʽ + SQL��Ƭ �ķ�װ��
     */
    public static class ExpressionSqlFrag {
        /**
         * ���Ա��ʽ
         */
        String expression;
        /**
         * SQL��Ƭ
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
         * �� {@link #sqlFrag} ��{@link #expression} ����Ϊnullʱ������true�����򷵻�false
         */
        public boolean complete(){
            return sqlFrag!=null&& expression !=null;
        }

        /**
         * {@link #expression} �Ƿ��� 'ELSE'������'else'
         * @return �Ƿ���ture,���򷵻�false
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

