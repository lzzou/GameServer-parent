package com.zlz.util;

import com.zlz.thread.NamedThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 线程池统一管理模块，便于排查问题（线程池必须命名）
 *
 * @author zlz
 */
public class ThreadPoolUtil {
    /**
     * 固定线程数量的线程池
     *
     * @param num
     * @param name
     * @return
     */
    public static ExecutorService service(int num, String name) {
        return Executors.newFixedThreadPool(num, new NamedThreadFactory(name));
    }

    /**
     * 单线程线程池
     *
     * @param name
     * @return
     */
    public static ExecutorService singleService(String name) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(name));
    }

    /**
     * 定时器线程池
     *
     * @param num
     * @return
     */
    public static ScheduledExecutorService scheduledExecutor(int num, String name) {
        return Executors.newScheduledThreadPool(num, new NamedThreadFactory(name));
    }

    /**
     * 单线程定时器
     *
     * @param name
     * @return
     */
    public static ScheduledExecutorService singleScheduledExecutor(String name) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name));
    }

}
