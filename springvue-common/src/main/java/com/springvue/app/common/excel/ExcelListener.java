package com.springvue.app.common.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.RowTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.util.ListUtils;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * EasyExcel工具监听器
 *
 * 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 * doReadSync同步的返回，不推荐使用，如果数据量大会把数据放到内存里面!!!!!!!!!
 *
 * @param <T>
 */
@Slf4j
@Accessors(chain = true)
public class ExcelListener<T> extends AnalysisEventListener<T> {

    /**
     * 读取总条数
     */
    private int count = 0;

    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    private List<T> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 消费者
     */
    public Consumer<List<T>> consumer = null;

    public ExcelListener(Consumer<List<T>> consumer) {
        this.consumer = consumer;
    }

    public ExcelListener(int BATCH_COUNT, Consumer<List<T>> consumer) {
        this.BATCH_COUNT = BATCH_COUNT;
        this.consumer = consumer;
        cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        //设置忽略空行
        context.readWorkbookHolder().setIgnoreEmptyRow(false);

        log.info("解析到一条数据:{}", data.toString());
        count++;
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去处理一次，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            callback();
            // 处理完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要处理数据，确保最后遗留的数据也处理了
        callback();
        log.info("所有数据解析完成！总条数:{}", count);
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", headMap.toString());
    }

    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("读取到了一条额外信息:{}", extra.toString());
        switch (extra.getType()) {
            case COMMENT:
                log.info("额外信息是批注,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(), extra.getColumnIndex(),
                        extra.getText());
                break;
            case HYPERLINK:
                if ("Sheet1!A1".equals(extra.getText())) {
                    log.info("额外信息是超链接,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(),
                            extra.getColumnIndex(), extra.getText());
                } else if ("Sheet2!A1".equals(extra.getText())) {
                    log.info(
                            "额外信息是超链接,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{},"
                                    + "内容是:{}",
                            extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                            extra.getLastColumnIndex(), extra.getText());
                } else {
                    log.error("Unknown hyperlink!");
                }
                break;
            case MERGE:
                log.info(
                        "额外信息是超链接,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{}",
                        extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                        extra.getLastColumnIndex());
                break;
            default:
        }
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        //设置忽略空行
        if (RowTypeEnum.EMPTY.equals(context.readRowHolder().getRowType())) {
            doAfterAllAnalysed(context);
            return false;
        }
        return super.hasNext(context);
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
        }
    }

    /**
     * 回调函数
     */
    private void callback() {
        if (consumer != null) {
            log.info("{}条数据，开始处理数据！", cachedDataList.size());
            consumer.accept(cachedDataList);
            log.info("{}条数据，处理数据成功！", cachedDataList.size());
        } else {
            log.error("没有回调函数！");
        }
    }
}
