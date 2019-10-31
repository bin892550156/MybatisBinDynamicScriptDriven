package bin.common.driver;

public interface BinKey {

     /**
      * ��Ϊ���ж�
      */
     String KEY_IS_NOT_EMPTY ="[IS_NOT_EMPTY";
     /**
      * Ϊ���ж�
      */
     String KEY_IS_EMPTY ="[IS_EMPTY";
     /**
      * Ϊnull�ж�
      */
     String KEY_IS_NULL ="[IS_NULL";
     /**
      * ��Ϊnull�ж�
      */
     String KEY_IS_NOT_NULL ="[IS_NOT_NULL";
     /**
      * switch��ʽ���ж�
      */
     String KEY_CASE="[CASE";
     /**
      * in SQL ƴװ
      */
     String KEY_IN="[IN";
     /**
      * EL���ʽ�ж�
      */
     String KEY_IF ="[IF";
     /**
      * �޸�ȫ������
      */
     String KEY_UPDATE="[UPDATE";
     /**
      * �޸Ĳ�Ϊnull������
      */
     String KEY_UPDATE_NOT_NULL="[UPDATE_NOT_NULL";
     /**
      * �޸Ĳ�Ϊ�յ�����
      */
     String KEY_UPDATE_NOT_EMPTY="[UPDATE_NOT_EMPTY";
     /**
      * ����ȫ������
      */
     String KEY_INSERT="[INSERT";
     /**
      * ���벻Ϊnull����
      */
     String KEY_INSERT_NOT_NULL="[INSERT_NOT_NULL";
     /**
      * ���벻Ϊ�յ�����
      */
     String KEY_INSERT_NOT_EMPTY="[INSERT_NOT_EMPTY";
     /**
      * ������������
      */
     String KEY_INSERT_FOR_EACH="[INSERT_FOR_EACH";

     /**
      * BinSQL���ʽǰ׺
      */
     String PREFIX="[";
     /**
      * BinSQL���ʽ��׺
      */
     String SUFFIX="]";
//     String SEPARATOR=":";
//     String MAPKEY_PREFIX="{";
//     String MAPKEY_SUFFIX="}";

}
