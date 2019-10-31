package bin.common.driver;

import bin.common.driver.sqlsource.BinRawSqlSource;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * BinSQL的驱动类
 * <p>
 *     用于处理BinSQL，并兼顾Mybatis默认驱动原有的功能。
 * </p>
 */
public class BinSQLDriver extends XMLLanguageDriver {

    /**
     * 创建一个{@link SqlSource} 从一个字解里，它在启动期间,当映射语句从一个类或读取一个xml文件被调用。;
     * @param configuration Mybatis全局配置新
     * @param script 注解中的SQL脚本
     * @param parameterType 输入参数类型从一个xml类型映射器参数中指定的方法或属性。可以为空。;
     * @return
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        /**
         * 如果script中出现'['字符，就认为是Bin SQL
         */
        if(script.indexOf(BinKey.PREFIX)!=-1){
            return new BinRawSqlSource(configuration,script,parameterType);
        }else{
            return super.createSqlSource(configuration, script, parameterType);
        }
    }
}
