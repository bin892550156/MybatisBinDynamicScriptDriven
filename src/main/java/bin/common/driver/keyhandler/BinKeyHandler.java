package bin.common.driver.keyhandler;

import bin.common.driver.assigment.BinKeyAssigment;

/**
 * BIN关键词业务处理器
 */
public interface BinKeyHandler {
    /**
     * 解析{@link bin.common.driver.BinKey}对应业务,并返回处理后的SQL碎片
     * @param binBinKeySqlInfo bin关键词表达式封装信息
     * @param paramObject 参数对象
     * @return 处理后的SQL碎片
     */
    String resolve(BinKeyAssigment.BinKeySqlInfo binBinKeySqlInfo, Object paramObject);
}
