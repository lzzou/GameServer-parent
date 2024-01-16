package com.base.executor;

import com.game.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 线程池
 *
 * @author dream
 */
@Slf4j
public class ExecutorPool {

    /**
     * 延时线程检查间隔
     */
    private static final int CHECK_INTERVAL = 5;

    /**
     * 逻辑处理worker线程池
     */
    private ExecutorService workerService;

    /**
     * 延迟时间处理boss线程池
     */
    private ExecutorService delayService;

    /**
     * 是否在运行
     */
    private volatile boolean isRunning = true;

    /**
     * 延时Action列表
     */
    private List<AbstractDelayTask> delayList;
    private List<AbstractDelayTask> delayListTemp;

    public ExecutorService getWorkerService() {
        return workerService;
    }

    public ExecutorPool(int bossSize, int workerSize, String workerPoolName, String bossPoolName) {
        workerService = ThreadPoolUtil.service(workerSize, workerPoolName);
        if (bossSize > 0) {
            delayService = ThreadPoolUtil.service(bossSize, bossPoolName);
            delayList = new ArrayList<AbstractDelayTask>();
            delayListTemp = new ArrayList<AbstractDelayTask>();
            delayService.submit(() -> {
                while (isRunning) {
                    long begin = System.currentTimeMillis();
                    checkDelayTask(begin);
                    long interval = System.currentTimeMillis() - begin;
                    if (interval < CHECK_INTERVAL) {
                        try {
                            Thread.sleep(CHECK_INTERVAL - interval);
                        } catch (Exception e) {
                            log.error(String.format("delay task check exception. ", e));
                        }
                    } else {
                        log.warn(String.format("delay task check spend too much time. time:%d", interval));
                    }
                }
            });
        }
    }

    public ExecutorPool(int workerSize) {
        this(0, workerSize, "worker-pool", "boss-pool");
    }

    public void submit(AbstractTask task) {
        workerService.submit(task);
    }

    public void submit(Runnable task) {
        workerService.submit(task);
    }

    public void addDelayTask(AbstractDelayTask action) {
        if (delayList != null) {
            synchronized (delayList) {
                delayList.add(action);
            }
        }
    }

    private void checkDelayTask(long time) {
        if (delayList == null) {
            return;
        }

        if (delayList.isEmpty()) {
            return;
        }

        synchronized (delayList) {
            delayListTemp.addAll(delayList);
            delayList.clear();
        }

        List<AbstractDelayTask> list = new ArrayList<AbstractDelayTask>();
        for (AbstractDelayTask t : delayListTemp) {
            if (!t.checkDelayFinishAndExecute(time)) {
                list.add(t);
            }
        }

        synchronized (delayList) {
            delayList.addAll(list);
        }

        list.clear();
        delayListTemp.clear();
    }

    public void shutdown() {
        isRunning = false;

        if (delayList != null) {
            delayList.clear();
        }

        if (delayListTemp != null) {
            delayListTemp.clear();
        }

        if (workerService != null) {
            workerService.shutdownNow();
        }

        if (delayService != null) {
            delayService.shutdownNow();
        }
    }
}
