package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;
import bin.common.driver.helper.ExpressionEvaluatorHelper;
import bin.common.driver.helper.ReflectorHelper;
import bin.common.driver.iterator.ExpressionSqlFragIterator;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 写操作BinKey处理抽象类
 */
public abstract class WriteOperationBinKeyHandler implements BinKeyHandler{

    /**
     * Mybatis全局配置信息
     */
    protected Configuration configuration;
    /**
     * EL表达式执行帮助类
     */
    private ExpressionEvaluatorHelper expressionEvaluatorHelper;
    /**
     * 属性名 与 EL表达式 映射
     */
    protected Map<String,String> propertyAndExpressionMap;

    /**
     * 过滤操作结果枚举
     */
    protected enum Filter{
        /**
         * 不操作
         */
        NONE,
        /**
         * 过滤
         */
        FILTER,
        /**
         * 不过滤
         */
        NOT_FILTER
    }

    /**
     *
     * @param configuration Mybatis全局配置信息
     */
    public WriteOperationBinKeyHandler(Configuration configuration) {
        this.configuration = configuration;
        expressionEvaluatorHelper=ExpressionEvaluatorHelper.getInstance();
    }

    /**
     * 反射 {@code paramObject} 的属性，过滤掉不符合条件的属性，拼装对应的SQL脚本返回出去
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return SQL脚本
     */
    @Override
    public String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject) {
        init(binBinKeySqlInfo,paramObject);
        Class paramCls=paramObject.getClass();
        Reflector reflector=new Reflector(paramCls);
        List<String> ignoreMappingProps = ReflectorHelper.getIgnoreMapping(paramCls);
        String[] properyNames=reflector.getGetablePropertyNames();
        String tableName=ReflectorHelper.getTableName(paramCls);
        List<String> mappingAblePropertyList=new ArrayList<>();
        for(String propertyName:properyNames){
            if(ignoreMappingProps.contains(propertyName)) continue;
            if(filter(binBinKeySqlInfo,propertyName,paramObject)==Filter.FILTER) continue;
            mappingAblePropertyList.add(propertyName);
        }

        String sql=generatorSQL(binBinKeySqlInfo,paramObject,tableName,mappingAblePropertyList);
        return sql;
    }

    /**
     * {@link #resolve(BinKeyAssigment.BinKeySqlInfo, Object)} 最开始调用的方法，用于对业务数据的一些初始化操作。
     * 默认实现初始化 {@link #propertyAndExpressionMap} ,对{@link BinKeyAssigment.BinKeySqlInfo#getResolvedSqlFrg()}
     * 的值进行解析封装成 {@link bin.common.driver.iterator.ExpressionSqlFragIterator.ExpressionSqlFrag} 对象，并将对象
     * 的 {@code sqlFrag} 和 {@code expression} 添加到 {@link #propertyAndExpressionMap} 中。
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     */
    protected void init(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject){
        String resolvedSqlFrag= binBinKeySqlInfo.getResolvedSqlFrg();
        if(resolvedSqlFrag==null || resolvedSqlFrag.isEmpty()){
            propertyAndExpressionMap =new HashMap<>(0);
            return;
        }
        ExpressionSqlFragIterator expressionSqlFragIterator =new ExpressionSqlFragIterator(resolvedSqlFrag);
        propertyAndExpressionMap =new HashMap<>();
        while (expressionSqlFragIterator.hasNext()){
            ExpressionSqlFragIterator.ExpressionSqlFrag next = expressionSqlFragIterator.next();
            //ExpressionSqlFrag.sqlFrag对应于paramObject的属性名
            propertyAndExpressionMap.put(next.getSqlFrag().trim(),next.getExpression());
        }
    }

    /**
     * 生成SQL脚本
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @param tableName 表名
     * @param mappingAblePropertyList 符合条件的属性名列表
     * @return SQL脚本
     */
    protected abstract String generatorSQL(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject,
                                           String tableName, List<String> mappingAblePropertyList);

    /**
     * 过滤属性，默认获取 {@code propertyName} 对应的EL表达式进行执行，如果返回true，就不过滤该属性名；
     * 否则过滤掉。如果没有找到对应的EL表达式时，默认是采用该属性。
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param propertyName 属性名
     * @param paramObject 参数对象
     * @return 返回true表示过滤掉该属性，返回false表示采用该属性。
     */
    protected Filter filter(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, String propertyName, Object paramObject) {
        String expression = propertyAndExpressionMap.get(propertyName);
        if(expression!=null&&!expression.isEmpty()){
            boolean evaluateBoolean = expressionEvaluatorHelper.evaluateBoolean(expression, paramObject);
            return evaluateBoolean?Filter.NOT_FILTER:Filter.FILTER;
        }
        return Filter.NONE;
    }
}
