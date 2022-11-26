package com.springvue.app.common.utils;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * 线程工具
 */
@Slf4j
public class ExecutorUtils {

    /**
     * 默认尝试锁秒数
     */
    public static final String DEFAULT_THREAD_NAME = "ExecutorUtil-Pool";

    /**
     * 默认尝试锁秒数
     */
    public static final Integer DEFAULT_TRYLOCK_SECOND = 5;

    /**
     * 开启线程池
     */
    public static ExecutorService executor = ThreadUtil.newExecutor(1, 10);

    /**
     * 线程锁
     */
    public static void lock(Consumer<Void> consumer) {
        ExecutorUtils.lock(consumer, DEFAULT_THREAD_NAME, null);
    }

    public static void lock(Consumer<Void> consumer, Integer second) {
        ExecutorUtils.lock(consumer, DEFAULT_THREAD_NAME, second);
    }

    public static void lock(Consumer<Void> consumer, String threadName) {
        ExecutorUtils.lock(consumer, threadName, null);
    }

    public static void lock(Consumer<Void> consumer, String threadName, Integer second) {
        executor.execute(ThreadUtil.newThread(() -> {
            Lock lock = new ReentrantLock(true);
            boolean tryLock = false;
            try {
                tryLock = lock.tryLock();
                if (second != null) {
                    tryLock = lock.tryLock(second, TimeUnit.SECONDS);
                }
                if (tryLock) {
                    consumer.accept(null);
                } else {
                    log.info("线程[{}]获取锁失败", threadName);
                }
            } catch (Exception e) {
                log.info("线程[{}]执行异常, 原因: {}", threadName, e.getMessage(), e);
            } finally {
                if (tryLock) {
                    lock.unlock();
                }
            }
        }, threadName));
    }

}
