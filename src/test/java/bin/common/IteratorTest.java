package bin.common;

import bin.common.driver.iterator.ExpressionSqlFragIterator;
import org.junit.Test;

public class IteratorTest {

    @Test
    public void test_caseSqlFragIterator(){
        ExpressionSqlFragIterator expressionSqlFragIterator =new ExpressionSqlFragIterator("#{user.id},#{user.name},age!=null&&age!=0@#{user.age}");
        while (expressionSqlFragIterator.hasNext()){
            ExpressionSqlFragIterator.ExpressionSqlFrag expressionSqlFrag = expressionSqlFragIterator.next();
            System.out.println(expressionSqlFrag);
        }
    }
}
