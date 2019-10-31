package bin.common.driver.iterator;

import bin.common.driver.BinKey;
import bin.common.driver.exception.BinResloveSqlException;

import java.util.Iterator;

/**
 * 属性名迭代器
 * <p>
 *     用于将表达式的属性提取出来。e.g.' name & age ' => ['nane','age']
 * </p>
 */
public class PropertyNameIterator implements Iterator<String> {
    /**
     * '&' 字符
     */
    public final static char CHAR_AND='&';
    /**
     * '|' 字符
     */
    public final static char CHAR_OR='|';
    /**
     * 当前索引位置
     */
    int currentIndex;
    /**
     * 上一个属性名的结束位置
     */
    int lastPropNameEndIndex;
    /**
     * 下一个属性名
     */
    String next;
    /**
     * 属性表达式字符数组
     */
    char[] propertyExpressionCharArr;
    /**
     * 属性表达式
     */
    String propertyExpression;
    /**
     * 当前使用的运算符字符,'&' 或者是 '|'
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
     *  判断是否还有下一个属性名
     * <p>
     *     通过遍历{@link #propertyExpressionCharArr} 提取出属性名。如果提取成功就返回true，否则返回false。
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
                    throw new BinResloveSqlException(BinKey.KEY_IS_NOT_EMPTY +" 不用同时出现&和|:"+ propertyExpression);
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
     * 返回下一个属性名
     * @return 下一个属性名
     */
    @Override
    public String next() {
        return next;
    }

    /**
     * 获取当前运算符
     */
    public char getCheckCondition() {
        return checkCondition;
    }
}