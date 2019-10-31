package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.FieldNameConversionHelper;
import org.apache.ibatis.session.Configuration;

import java.util.List;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_INSERT} ��ҵ���������Զ�����Insert SQL�ű���
 */
public class InsertBinKeyHandler extends WriteOperationBinKeyHandler{

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public InsertBinKeyHandler(Configuration configuration){
        super(configuration);
    }

    /**
     * ���� INSERT SQL�ű�
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
