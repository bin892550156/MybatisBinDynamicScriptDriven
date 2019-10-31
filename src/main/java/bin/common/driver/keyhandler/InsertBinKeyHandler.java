package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.FieldNameConversionHelper;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * 对应 {@link bin.common.driver.BinKey#KEY_INSERT} 的业务处理器。自动生成Insert SQL脚本。
 */
public class InsertBinKeyHandler extends WriteOperationBinKeyHandler{

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public InsertBinKeyHandler(Configuration configuration){
        super(configuration);
    }

    /**
     * 生成 INSERT SQL脚本
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @param tableName 表名
     * @param mappingAblePropertyList 符合条件的属性名列表
     * @return INSERT SQL脚本
     */
    @Override
    protected String generatorSQL(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject,
                                  String tableName, List<String> mappingAblePropertyList) {
        StringBuilder sql=new StringBuilder();
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
        return sql.toString();
    }

}
