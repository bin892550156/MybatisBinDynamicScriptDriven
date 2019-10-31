package bin.common.driver.annotation;

import java.lang.annotation.*;

/**
 * 用于标记该属性要被忽略，不参加读写操作的 Bin SQL的的构建
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BinIgnoreMapping {
}
