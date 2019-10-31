package bin.common.driver;

import bin.common.driver.sqlsource.BinRawSqlSource;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * BinSQL��������
 * <p>
 *     ���ڴ���BinSQL�������MybatisĬ������ԭ�еĹ��ܡ�
 * </p>
 */
public class BinSQLDriver extends XMLLanguageDriver {

    /**
     * ����һ��{@link SqlSource} ��һ���ֽ�����������ڼ�,��ӳ������һ������ȡһ��xml�ļ������á�;
     * @param configuration Mybatisȫ��������
     * @param script ע���е�SQL�ű�
     * @param parameterType ����������ʹ�һ��xml����ӳ����������ָ���ķ��������ԡ�����Ϊ�ա�;
     * @return
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        /**
         * ���script�г���'['�ַ�������Ϊ��Bin SQL
         */
        if(script.indexOf(BinKey.PREFIX)!=-1){
            return new BinRawSqlSource(configuration,script,parameterType);
        }else{
            return super.createSqlSource(configuration, script, parameterType);
        }
    }
}
