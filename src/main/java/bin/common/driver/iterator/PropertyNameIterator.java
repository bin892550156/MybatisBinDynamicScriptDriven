package bin.common.driver.iterator;

import bin.common.driver.BinKey;
import bin.common.driver.exception.BinResloveSqlException;

import java.util.Iterator;

/**
 * ������������
 * <p>
 *     ���ڽ����ʽ��������ȡ������e.g.' name & age ' => ['nane','age']
 * </p>
 */
public class PropertyNameIterator implements Iterator<String> {
    /**
     * '&' �ַ�
     */
    public final static char CHAR_AND='&';
    /**
     * '|' �ַ�
     */
    public final static char CHAR_OR='|';
    /**
     * ��ǰ����λ��
     */
    int currentIndex;
    /**
     * ��һ���������Ľ���λ��
     */
    int lastPropNameEndIndex;
    /**
     * ��һ��������
     */
    String next;
    /**
     * ���Ա��ʽ�ַ�����
     */
    char[] propertyExpressionCharArr;
    /**
     * ���Ա��ʽ
     */
    String propertyExpression;
    /**
     * ��ǰʹ�õ�������ַ�,'&' ������ '|'
     */
    char checkCondition;
    public PropertyNameIterator(String propertyExpression) {
        this.propertyExpression =propertyExpression;
        this.propertyExpressionCharArr =propertyExpression.toCharArray();
        currentIndex=-1;
        lastPropNameEndIndex=-1;
        for (int i = 0; i < propertyExpressionCharArr.length; i++) {
            char c= propertyExpressionCharArr[i];
            if(c=='&'||c=='|'){
                checkCondition=c;
                break;
            }
        }
    }

    /**
     *  �ж��Ƿ�����һ��������
     * <p>
     *     ͨ������{@link #propertyExpressionCharArr} ��ȡ���������������ȡ�ɹ��ͷ���true�����򷵻�false��
     * </p>
     * @return
     */
    @Override
    public boolean hasNext() {
        int length= propertyExpressionCharArr.length;
        boolean hasNext=false;
        for(int i=currentIndex+1;i<length;i++){
            char c= propertyExpressionCharArr[i];
            if(c==CHAR_AND||c==CHAR_OR){
                if(c!=checkCondition){
                    throw new BinResloveSqlException(BinKey.KEY_IS_NOT_EMPTY +" ����ͬʱ����&��|:"+ propertyExpression);
                }
                next= propertyExpression.substring(lastPropNameEndIndex==-1?0:lastPropNameEndIndex,i);
                char c2= propertyExpressionCharArr[i+1];
                if(i+1<length&&(c2=='&'||c2=='|')){
                    i++;
                }
                currentIndex=i;
                lastPropNameEndIndex=currentIndex;
                hasNext=true;
                break;
            }else if(i==length-1){
                next= propertyExpression.substring(lastPropNameEndIndex+1,length);
                currentIndex=i;
                hasNext=true;
            }
        }
        return hasNext;
    }

    /**
     * ������һ��������
     * @return ��һ��������
     */
    @Override
    public String next() {
        return next;
    }

    /**
     * ��ȡ��ǰ�����
     */
    public char getCheckCondition() {
        return checkCondition;
    }
}