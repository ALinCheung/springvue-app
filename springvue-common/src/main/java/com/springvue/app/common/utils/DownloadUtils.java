package com.springvue.app.common.utils;

import com.springvue.app.common.excel.ExcelUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 下载工具类
 */
public class DownloadUtils {

    /**
     * 配置字节流
     * @param response
     * @param fileName
     */
    public static void stream(HttpServletResponse response, String fileName) {
        response.setContentType("application/octet-stream");
        attachment(response, fileName);
    }

    /**
     * 设置强制下载不打开
     * @param response
     * @param fileName
     */
    public static void force(HttpServletResponse response, String fileName) {
        response.setContentType("application/force-download");
        attachment(response, fileName);
    }

    /**
     * 配置下载参数
     * @param response
     * @param fileName
     */
    private static void attachment(HttpServletResponse response, String fileName) {
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
            DownloadUtils.force(response, fileName);
        }
    }
}
