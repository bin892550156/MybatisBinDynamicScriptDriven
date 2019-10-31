package bin.common;

import bin.common.bean.User;
import bin.common.driver.helper.FieldNameConversionHelper;
import bin.common.driver.helper.ReflectorHelper;
import org.apache.ibatis.reflection.Reflector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.StringTokenizer;

public class CodeTest {

    @Test
    public void test_insertSql(){
        Class paramCls=User.class;
        Reflector reflector=new Reflector(paramCls);
        List<String> ignoreMappingProps = ReflectorHelper.getIgnoreMapping(paramCls);
        String[] properyNames=reflector.getGetablePropertyNames();
        StringBuilder sql=new StringBuilder();
        String tableName=ReflectorHelper.getTableName(paramCls);
        List<String> mappingAblePropertyList=new ArrayList<>();
        for(String propertyName:properyNames){
            if(ignoreMappingProps.contains(propertyName)){
                continue;
            }
            mappingAblePropertyList.add(propertyName);
        }
        sql.append("INSERT INTO ").append(tableName).append(" ( ");
        for(String propertyName:mappingAblePropertyList){
            String column=FieldNameConversionHelper.humpToLine(propertyName);
            sql.append(column).append(",");
        }
        sql.delete(sql.length()-1,sql.length());
        sql.append(") values ( ");
        for(String propertyName:mappingAblePropertyList){
            sql.append("#{").append(propertyName).append("},");
        }
        sql.delete(sql.length()-1,sql.length());
        sql.append(")");
    }

    @Test
    public void test_updateSql(){
        Class paramCls= User.class;
        Reflector reflector=new Reflector(paramCls);
        List<String> ignoreMappingProps = ReflectorHelper.getIgnoreMapping(paramCls);
        String[] properyNames=reflector.getGetablePropertyNames();
        StringBuilder sql=new StringBuilder();
        String tableName=ReflectorHelper.getTableName(paramCls);
        List<String> mappingAblePropertyList=new ArrayList<>();
        for(String propertyName:properyNames){
            if(ignoreMappingProps.contains(propertyName)){
                continue;
            }
            mappingAblePropertyList.add(propertyName);
        }
        sql.append(" UPDATE ").append(tableName).append(" SET  ");
        for(String propertyName:mappingAblePropertyList){
            String column=FieldNameConversionHelper.humpToLine(propertyName);
            sql.append(column).append(" = #{").append(propertyName).append("},");
        }
        sql.delete(sql.length()-1,sql.length());
        System.out.println(sql);
    }
}
