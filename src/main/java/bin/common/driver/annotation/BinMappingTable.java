package bin.common.driver.annotation;

import java.lang.annotation.*;

/**
 * ӳ��ı���
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BinMappingTable {

   /**
    * ����
    */
   String value() default "";
}
