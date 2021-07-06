package com.base.spy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

/**
 * Java锁监测机制，防止死锁、Lock卡死问题
 *
 * @author dansen
 * @date May 26, 2017 10:35:06 AM
 * @desc
 */

public class Spy {
    private static final Logger logger = LoggerFactory.getLogger(Spy.class);

    // 连续出现N次等待则发出警告
    public static final int WARNING_COUNT = 5;
    // 监测线程是否后台运行（Java退出需要所有非daemon线程结束才会正常退出）
    public static volatile boolean daemon = true;
    // 程序的进程ID
    public static volatile int pid;

    //
    public static volatile boolean log = false;

    // 每隔多久检测一次
    public static volatile int interval = 60 * 1000; // 1 minute

    /**
     * 启动监测函数
     */
    public static void start() {
        start(0, true, false);
    }

    /**
     * 检测jstack是否安装
     *
     * @return
     */
    private static boolean isJstackInstalled() {
        try {
            ProcessBuilder builder = new ProcessBuilder("jstack", "-l", String.valueOf(pid));
            builder.start();
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static void start(int interval, boolean daemon, boolean log) {
        if (!isJstackInstalled()) {
            return;
        }

        if (interval > 0) {
            Spy.interval = interval;
        }

        Spy.daemon = daemon;
        Spy.log = log;

        String name = ManagementFactory.getRuntimeMXBean().getName();
        Spy.pid = Integer.parseInt(name.split("@")[0]);
        SpyService.spyService();
    }

    public static void main(String[] args) {
        start();
    }
}
