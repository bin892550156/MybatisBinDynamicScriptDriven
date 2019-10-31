package bin.common.driver;

public interface BinKey {

     /**
      * 不为空判断
      */
     String KEY_IS_NOT_EMPTY ="[IS_NOT_EMPTY";
     /**
      * 为空判断
      */
     String KEY_IS_EMPTY ="[IS_EMPTY";
     /**
      * 为null判断
      */
     String KEY_IS_NULL ="[IS_NULL";
     /**
      * 不为null判断
      */
     String KEY_IS_NOT_NULL ="[IS_NOT_NULL";
     /**
      * switch形式的判断
      */
     String KEY_CASE="[CASE";
     /**
      * in SQL 拼装
      */
     String KEY_IN="[IN";
     /**
      * EL表达式判断
      */
     String KEY_IF ="[IF";
     /**
      * 修改全部属性
      */
     String KEY_UPDATE="[UPDATE";
     /**
      * 修改不为null的属性
      */
     String KEY_UPDATE_NOT_NULL="[UPDATE_NOT_NULL";
     /**
      * 修改不为空的属性
      */
     String KEY_UPDATE_NOT_EMPTY="[UPDATE_NOT_EMPTY";
     /**
      * 插入全部属性
      */
     String KEY_INSERT="[INSERT";
     /**
      * 插入不为null属性
      */
     String KEY_INSERT_NOT_NULL="[INSERT_NOT_NULL";
     /**
      * 插入不为空的属性
      */
     String KEY_INSERT_NOT_EMPTY="[INSERT_NOT_EMPTY";
     /**
      * 批量插入属性
      */
     String KEY_INSERT_FOR_EACH="[INSERT_FOR_EACH";

     /**
      * BinSQL表达式前缀
      */
     String PREFIX="[";
     /**
      * BinSQL表达式后缀
      */
     String SUFFIX="]";
//     String SEPARATOR=":";
//     String MAPKEY_PREFIX="{";
//     String MAPKEY_SUFFIX="}";

}
