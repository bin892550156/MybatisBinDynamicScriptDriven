package bin.common;

import bin.common.driver.helper.SqlFragmentHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SqlFragmentHelperTest {

    SqlFragmentHelper sqlFragmentHelper;

    @Before
    public void init(){
        sqlFragmentHelper=new SqlFragmentHelper();
    }

    @Test
    public void test_isWhereAsTail(){
        boolean isWhereAsTailFlag=sqlFragmentHelper.isWhereAsTail("SEELCT * FROM USER WHERE            ",35);
        Assert.assertEquals(true,isWhereAsTailFlag);
        isWhereAsTailFlag=sqlFragmentHelper.isWhereAsTail("SELECT * FROM USER          ",27);
        Assert.assertEquals(false,isWhereAsTailFlag);
        isWhereAsTailFlag=sqlFragmentHelper.isWhereAsTail("SELECT * FROM USER WHERE  ",18);
        Assert.assertEquals(false,isWhereAsTailFlag);
    }

    @Test
    public void test_correctSqlFragPrefix(){
        String sqlFrag=sqlFragmentHelper.correctSqlFragPrefix("and name = '11' ");
        Assert.assertEquals(" name = '11' ",sqlFrag);
        sqlFrag=sqlFragmentHelper.correctSqlFragPrefix(" or name ='11' ");
        Assert.assertEquals(" name ='11' ",sqlFrag);
    }

    @Test
    public void test_correctSqlFragSuffix(){
        String sqlFrag=sqlFragmentHelper.correctSqlFragSuffix(" select * from user where   ");
        Assert.assertEquals(" select * from user ",sqlFrag);
        sqlFrag=sqlFragmentHelper.correctSqlFragSuffix(" select * from user where   and  ");
        Assert.assertEquals(" select * from user where   ",sqlFrag);
        sqlFrag=sqlFragmentHelper.correctSqlFragSuffix(" select * from user where   or  ");
        Assert.assertEquals(" select * from user where   ",sqlFrag);
    }
}
