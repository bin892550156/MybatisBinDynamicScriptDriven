package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_INSERT_NOT_EMPTY} ��ҵ������
 * <p>
 *     ��ƴװInsert SQL�Ĺ��ܼ��ϼ���Ϊnull�ֲ�Ϊ���ַ��Ĺ�������
 * </p>
 */
public class InsertNotEmptyBinKeyHandler extends InsertBinKeyHandler{

    public InsertNotEmptyBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * ��� {@code propertyName} �� {@code paramObject} ��Ӧ�����Զ����� {@link String} ���ͣ�����Ϊnull�ֲ��ǿ��ַ���,�Ͳ����й��ˡ�
     * ��� {@code propertyName}  �� {@code paramObject} ��Ӧ�����Զ�����{@link String} ���ͣ�ֻҪ��Ϊnull,�������й���
     * <p>
     *     ����ʹ�ø����ҵ����ˣ�������ҵ����Ϊ {@code propertyName} ����Ҫ����ʱ���Ż���б����ҵ����ˡ�
     * </p>
     * @param binBinKeySqlInfo bin�ؼ��ʱ��ʽ��װ��Ϣ
     * @param propertyName ������
     * @param paramObject ��������
     * @return trueΪ���ˣ�falseΪ������
     */
    @Override
    protected Filter filter(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, String propertyName, Object paramObject) {
        Filter filter=super.filter(binBinKeySqlInfo,propertyName,paramObject);
        if(filter==Filter.NONE){
            MetaObject paramMetaObj=configuration.newMetaObject(paramObject);
            Object value=paramMetaObj.getValue(propertyName);
            if(value==null ) return Filter.FILTER;
            if(value instanceof String){
                return ((String) value).isEmpty()?Filter.FILTER:Filter.NOT_FILTER;
            }
        }
        return filter;
    }
}
