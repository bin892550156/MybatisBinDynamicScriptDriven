package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

/**
 * ��Ӧ {@link bin.common.driver.BinKey#KEY_UPDATE_NOT_NULL} ��ҵ������
 * <p>
 *     ��ƴװUPDATE SQL�Ĺ��ܼ��ϲ���Ϊnull�Ĺ�������
 * </p>
 */
public class UpdateNotNullBinKeyHandler extends UpdateBinKeyHandler{

    /**
     *
     * @param configuration Mybatisȫ��������Ϣ
     */
    public UpdateNotNullBinKeyHandler(Configuration configuration) {
        super(configuration);
    }

    /**
     * ��� {@code propertyName} �� {@code paramObject} ��Ӧ�����Զ���Ϊnull�Ͳ����й��ˡ�
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
            MetaObject paraMetaObj=configuration.newMetaObject(paramObject);
            Object object=paraMetaObj.getValue(propertyName);
            return object==null?Filter.FILTER:Filter.NOT_FILTER;
        }
        return filter;
    }
}
