package bin.common.driver.sqlsource;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

import java.util.HashMap;

/**
 * BIN sqlԴ
 */
public class BinRawSqlSource implements SqlSource {

    /**
     * SQL�ű�
     */
    private String sql;
    /**
     * Mybatisȫ��������Ϣ
     */
    private Configuration configuration;
    /**
     * ��������
     */
    private Class<?> parameterType;

    /**
     * BinKey������
     */
    private BinKeyAssigment binKeyAssigment;
    /**
     * {@link SqlSource} ������
     */
    private SqlSourceBuilder sqlSourceParser;

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     * @param script SQL�ű�
     * @param parameterType ��������
     */
    public BinRawSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        this.sql = script;
        this.configuration=configuration;
        this.parameterType= parameterType == null ? Object.class : parameterType;
        binKeyAssigment=new BinKeyAssigment(configuration);
        sqlSourceParser = new SqlSourceBuilder(configuration);
    }


    /**
     * ���� {@code parameterObject} ������̬SQL,Ȼ�󹹽� {@link SqlSource}
     * @param parameterObject ��������
     * @return {@link org.apache.ibatis.builder.StaticSqlSource} ����
     */
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        String reslovedSql = binKeyAssigment.resloveSql(this.sql, parameterObject);
        SqlSource sqlSource = sqlSourceParser.parse(reslovedSql, this.parameterType, new HashMap<>());
        return sqlSource.getBoundSql(parameterObject);
    }


}
