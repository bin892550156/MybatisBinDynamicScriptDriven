package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.FieldNameConversionHelper;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_UPDATE} ��ҵ���������Զ�����UPDATE SQL�ű���
 */
public class UpdateBinKeyHandler extends WriteOperationBinKeyHandler{

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public UpdateBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * ���� UPDATE SQL�ű�
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @param tableName ����
     * @param mappingAblePropertyList �����������������б�
     * @return INSERT SQL�ű�
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
     * ȡ�� {@link #propertyAndExpressionMap} �� 'WHERE' ���� 'where' ��SQL��Ƭ
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param paramObject ��������
     * @return {@link #propertyAndExpressionMap} �� 'WHERE' ���� 'where' ��SQL��Ƭ
     */
    protected String generatorWHERESql(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        String resolvedSqlFrg = propertyAndExpressionMap.get("WHERE");
        if(resolvedSqlFrg==null||resolvedSqlFrg.isEmpty()){
            resolvedSqlFrg= propertyAndExpressionMap.get("where");
        }
        return resolvedSqlFrg==null?"":resolvedSqlFrg;
    }


}
