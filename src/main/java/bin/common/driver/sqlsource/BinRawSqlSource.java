package bin.common.driver.sqlsource;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;

/**
 * BIN sql源
 */
public class BinRawSqlSource implements SqlSource {

    /**
     * SQL脚本
     */
    private String sql;
    /**
     * Mybatis全局配置信息
     */
    private Configuration configuration;
    /**
     * 参数类型
     */
    private Class<?> parameterType;

    /**
     * BinKey分配器
     */
    private BinKeyAssigment binKeyAssigment;
    /**
     * {@link SqlSource} 构建器
     */
    private SqlSourceBuilder sqlSourceParser;

    /**
     *
     * @param configuration Mybatis全局配置信息
     * @param script SQL脚本
     * @param parameterType 参数类型
     */
    public BinRawSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        this.sql = script;
        this.configuration=configuration;
        this.parameterType= parameterType == null ? Object.class : parameterType;
        binKeyAssigment=new BinKeyAssigment(configuration);
        sqlSourceParser = new SqlSourceBuilder(configuration);
    }


    /**
     * 根据 {@code parameterObject} 构建动态SQL,然后构建 {@link SqlSource}
     * @param parameterObject 参数对象
     * @return {@link org.apache.ibatis.builder.StaticSqlSource} 对象
     */
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        String reslovedSql = binKeyAssigment.resloveSql(this.sql, parameterObject);
        SqlSource sqlSource = sqlSourceParser.parse(reslovedSql, this.parameterType, new HashMap<>());
        return sqlSource.getBoundSql(parameterObject);
    }


}
