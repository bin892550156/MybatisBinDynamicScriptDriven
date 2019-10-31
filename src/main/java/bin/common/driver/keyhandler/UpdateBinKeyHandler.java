package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.FieldNameConversionHelper;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * 对应 {@link bin.common.driver.BinKey#KEY_UPDATE} 的业务处理器。自动生成UPDATE SQL脚本。
 */
public class UpdateBinKeyHandler extends WriteOperationBinKeyHandler{

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public UpdateBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * 生成 UPDATE SQL脚本
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
        sql.append(" UPDATE ").append(tableName).append(" SET  ");
        for(String propertyName:mappingAblePropertyList){
            String column= FieldNameConversionHelper.humpToLine(propertyName);
            sql.append(column).append(" = #{").append(propertyName).append("},");
        }
        sql.delete(sql.length()-1,sql.length());
        String whereSql=generatorWHERESql(binBinKeySqlInfo,paramObject);
        sql.append(" WHERE ").append(whereSql);
        return sql.toString();
    }

    /**
     * 取出 {@link #propertyAndExpressionMap} 的 'WHERE' 或者 'where' 的SQL碎片
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return {@link #propertyAndExpressionMap} 的 'WHERE' 或者 'where' 的SQL碎片
     */
    protected String generatorWHERESql(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        String resolvedSqlFrg = propertyAndExpressionMap.get("WHERE");
        if(resolvedSqlFrg==null||resolvedSqlFrg.isEmpty()){
            resolvedSqlFrg= propertyAndExpressionMap.get("where");
        }
        return resolvedSqlFrg==null?"":resolvedSqlFrg;
    }


}
