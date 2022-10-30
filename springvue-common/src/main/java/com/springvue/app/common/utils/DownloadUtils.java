package com.springvue.app.common.utils;

import com.alibaba.excel.EasyExcel;
import com.springvue.app.common.excel.ExcelUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 下载工具类
 */
public class DownloadUtils {

    /**
     * 配置下载参数
     * @param response
     * @param fileName
     */
    public static void config(HttpServletResponse response, String fileName) {
        // 设置强制下载不打开
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
    }

    /**
     * 下载Excel
     * @param response
     * @param fileName
     * @param data
     * @param <T>
     * @throws Exception
     */
    public static <T> void excel(HttpServletResponse response, String fileName, List<T> data) throws Exception {
        try (OutputStream os = response.getOutputStream()){
            ExcelUtils.write(os, data);
            DownloadUtils.config(response, fileName);
        }
    }
}
