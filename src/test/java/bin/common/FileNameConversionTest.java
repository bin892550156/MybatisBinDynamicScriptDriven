package bin.common;

import bin.common.driver.helper.FieldNameConversionHelper;
import org.junit.Test;

public class FileNameConversionTest {

    @Test
    public void test_humpToLine(){
        String tbUser = FieldNameConversionHelper.humpToLine("TBUser");
        System.out.println(tbUser);
    }
}
