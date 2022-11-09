package com.springvue.app.common.utils;

import cn.hutool.core.thread.ThreadUtil;

import java.util.concurrent.ExecutorService;

/**
 * 线程工具
 */
public class ExecutorUtil {

    /**
     * 开启线程池
     */
    public static ExecutorService executor = ThreadUtil.newExecutor(1, 10);
}
