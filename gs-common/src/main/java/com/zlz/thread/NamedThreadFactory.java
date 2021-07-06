package com.zlz.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 命名线程生成工厂，仅为了给所用线程池所创建的线程提供命名支持
 */
public class NamedThreadFactory implements ThreadFactory, UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(NamedThreadFactory.class);

    private final AtomicInteger threadID = new AtomicInteger();

    /**
     * 是否为后台线程
     */
    private boolean daemon;

    /**
     * 线程名
     */
    private String threadName;

    /**
     * 默认构造函数
     *
     * @param threadName 线程名前缀
     * @param daemon     是否为后台线程
     */
    public NamedThreadFactory(String threadName, boolean daemon) {
        this.threadName = threadName;
        this.daemon = daemon;
    }

    public NamedThreadFactory(String threadName) {
        this(threadName, false);
    }

    /**
     * {@inheritDoc}
     *
     * @see ThreadFactory#newThread(Runnable)
     */
    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, this.threadName + "-" + threadID.incrementAndGet());
        t.setDaemon(this.daemon);
        t.setUncaughtExceptionHandler(this);
        return t;
    }

    /**
     * {@inheritDoc}
     *
     * @see UncaughtExceptionHandler#uncaughtException(Thread,
     * Throwable)
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Uncaught Exception in thread :" + thread.getName(), throwable);
    }

}
