package bin.common.driver.annotation;

import java.lang.annotation.*;

/**
 * 映射的表名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BinMappingTable {

   /**
    * 表名
    */
   String value() default "";
}
