package com.base.spy;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监测线程服务
 *
 * @author dansen
 * @date May 26, 2017 10:42:58 AM
 * @desc
 */
@Slf4j
public class SpyService {
    /**
     * 是否在运行中
     */
    private volatile boolean run = true;

    /**
     * 线程监测记录
     */
    private Map<String, ThreadWatcher> watchers = new HashMap<String, ThreadWatcher>();

    /**
     * 新出现的阻塞线程
     */
    public void onNewWait(ThreadInfo info) {
        watchers.put(info.getThreadID(), new ThreadWatcher(info));
    }

    /**
     * 线程已经恢复正常
     *
     * @param info
     */
    public void onFree(ThreadInfo info) {
        watchers.remove(info.getThreadID());
    }

    /**
     * 线程持续处于阻塞状态
     *
     * @param info
     */
    public void onContinue(ThreadInfo info) {
        ThreadWatcher watcher = watchers.get(info.getThreadID());

        if (watcher.getCount() >= Spy.WARNING_COUNT) {
            log.error("@_@ spy find thread {} blocked!!", info.getThreadID());
        }
    }

    /**
     * 更新监视
     *
     * @param info
     */
    public void updateWatch(ThreadInfo info) {
        if (!watchers.containsKey(info.getThreadID()) && info.getStatusType() == ThreadStatusType.Waiting) {
            onNewWait(info);
        } else if (watchers.containsKey(info.getThreadID()) && info.getStatusType() == ThreadStatusType.Runnable) {
            onFree(info);
        } else if (watchers.containsKey(info.getThreadID()) && info.getStatusType() == ThreadStatusType.Waiting) {
            onContinue(info);
        } else if (!watchers.containsKey(info.getThreadID()) && info.getStatusType() == ThreadStatusType.Runnable) {
            // the normal status
        } else {
            // System.out.println(info.getName() + " " + info.getStatus());
        }
    }

    /**
     * 服务函数
     *
     * @throws Exception
     */
    private void service() throws Exception {
        JStackObject stackObject = JavaTool.jstack(Spy.pid);

        List<ThreadInfo> threadInfos = stackObject.getThreads();

        for (ThreadInfo info : threadInfos) {
            updateWatch(info);
        }
    }

    /**
     * 结束服务
     */
    public void stop() {
        run = false;
    }

    /**
     * 是否正在运行
     *
     * @return
     */
    private boolean isRunning() {
        return run;
    }

    /**
     * 服务函数
     * 启动一个低优先级的线程在后台处理
     */
    public static void spyService() {
        SpyService service = new SpyService();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (service.isRunning()) {
                    try {
                        service.service();
                        Thread.sleep(Spy.interval);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.setDaemon(Spy.daemon);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("spy_detector");
        thread.start();
    }
}
