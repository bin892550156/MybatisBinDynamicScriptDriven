package bin.common.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyBatisBinConfig {

    public static SqlSessionFactory mSqlSessionFactory;

    public static void init(){
        if(mSqlSessionFactory==null){
            String resource = "mybatis-config.xml";
            try {
                InputStream inputStream= Resources.getResourceAsStream(resource);
                Properties properties=new Properties();
                properties.setProperty("driver","com.mysql.jdbc.Driver");
                properties.setProperty("url","jdbc:mysql://localhost:3306/bin_test?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
                properties.setProperty("username","root");
                properties.setProperty("password","root");
                mSqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream,properties);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static SqlSession openSession(){
        init();
        return mSqlSessionFactory.openSession();
    }


}
