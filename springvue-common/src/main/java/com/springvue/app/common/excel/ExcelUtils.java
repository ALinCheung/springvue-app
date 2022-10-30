package com.springvue.app.common.excel;

import cn.hutool.core.map.MapUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Excel工具类
 */
@Slf4j
public class ExcelUtils {

    /**
     * 写入Excel(直接写入)
     */
    public static <T> void write(String fileName, List<T> data) {
        ExcelUtils.write(fileName, "sheet1", data);
    }

    public static <T> void write(OutputStream os, List<T> data) {
        ExcelUtils.write(os, "sheet1", data);
    }

    /**
     * 写入Excel, 需要传入sheet名称(直接写入)
     */
    public static <T> void write(String fileName, String sheetName, List<T> data) {
        if (data != null && !data.isEmpty()) {
            // 获取获取列表泛型类型
            ParameterizedType parameterizedType = (ParameterizedType) data.getClass().getGenericSuperclass();
            Class<T> clz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
            // 写入Excel
            ExcelUtils.write(fileName, MapUtil.<String, Class<T>>builder().put(sheetName, clz).build(), ((excelWriter, writeSheet) -> {
                excelWriter.write(data, writeSheet);
            }));
        }
    }

    public static <T> void write(OutputStream os, String sheetName, List<T> data) {
        if (data != null && !data.isEmpty()) {
            // 获取获取列表泛型类型
            ParameterizedType parameterizedType = (ParameterizedType) data.getClass().getGenericSuperclass();
            Class<T> clz = (Class<T>) parameterizedType.getActualTypeArguments()[0];
            // 写入Excel
            ExcelUtils.write(os, MapUtil.<String, Class<T>>builder().put(sheetName, clz).build(), ((excelWriter, writeSheet) -> {
                excelWriter.write(data, writeSheet);
            }));
        }
    }

    /**
     * 自定义写入Excel, 需要传入sheet名称, 表头类型
     */
    public static <T> void write(String fileName, String sheetName, Class<T> clz, BiConsumer<ExcelWriter, WriteSheet> consumer) {
        try (ExcelWriter excelWriter = EasyExcel.write(fileName).build()) {
            ExcelUtils.write(excelWriter, sheetName, clz, consumer);
        }
    }

    public static <T> void write(OutputStream os, String sheetName, Class<T> clz, BiConsumer<ExcelWriter, WriteSheet> consumer) {
        try (ExcelWriter excelWriter = EasyExcel.write(os).build()) {
            ExcelUtils.write(excelWriter, sheetName, clz, consumer);
        }
    }

    private static <T> void write(ExcelWriter excelWriter, String sheetName, Class<T> clz, BiConsumer<ExcelWriter, WriteSheet> consumer) {
        ExcelUtils.write(excelWriter, MapUtil.<String, Class<T>>builder().put(sheetName, clz).build(), consumer);
    }

    /**
     * 自定义写入Excel, 需要传入sheet名称, 表头类型的键值对
     */
    public static <T> void write(String fileName, Map<String, Class<T>> configs, BiConsumer<ExcelWriter, WriteSheet> consumer) {
        try (ExcelWriter excelWriter = EasyExcel.write(fileName).build()) {
            ExcelUtils.write(excelWriter, configs, consumer);
        }
    }

    public static <T> void write(OutputStream os, Map<String, Class<T>> configs, BiConsumer<ExcelWriter, WriteSheet> consumer) {
        try (ExcelWriter excelWriter = EasyExcel.write(os).build()) {
            ExcelUtils.write(excelWriter, configs, consumer);
        }
    }

    private static <T> void write(ExcelWriter excelWriter, Map<String, Class<T>> configs, BiConsumer<ExcelWriter, WriteSheet> consumer) {
        // 这里注意 如果同一个sheet只要创建一次
        if (configs != null && !configs.keySet().isEmpty() && consumer != null) {
            for (String sheetName : configs.keySet()) {
                if (StringUtils.isNotBlank(sheetName) && configs.get(sheetName) != null) {
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).head(configs.get(sheetName)).build();
                    // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
                    // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                    // consumer需要执行excelWriter.write(data, writeSheet);
                    consumer.accept(excelWriter, writeSheet);
                }
            }
        }
    }

    /**
     * excel日期转java日期
     *
     * @param use1904windowing
     * @param value
     * @return
     */
    public static Date getPOIDate(boolean use1904windowing, double value) {
        int wholeDays = (int) Math.floor(value);
        int millisecondsInDay = (int) ((value - (double) wholeDays) * 8.64E7D + 0.5D);
        Calendar calendar = new GregorianCalendar();
        short startYear = 1900;
        byte dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        } else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, millisecondsInDay);
        if (calendar.get(Calendar.MILLISECOND) == 0) {
            calendar.clear(Calendar.MILLISECOND);
        }
        return calendar.getTime();
    }
}
