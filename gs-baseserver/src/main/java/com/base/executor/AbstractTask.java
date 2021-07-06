package com.base.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象任务
 *
 * @author dream
 */
public abstract class AbstractTask implements Runnable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractTask.class);

    protected static final int MAX_INTERVAL = 100;

    protected SelfDrivenTaskQueue queue;

    public AbstractTask() {
    }

    public String getName() {
        return getClass().getName();
    }

    public AbstractTask(SelfDrivenTaskQueue queue) {
        this.queue = queue;
    }

    public void setActionQueue(SelfDrivenTaskQueue actionQueue) {
        this.queue = actionQueue;
    }

    @Override
    public void run() {
        try {
            long time = System.currentTimeMillis();
            execute();
            int interval = (int) (System.currentTimeMillis() - time);
            if (interval > 200) {
                LOGGER.debug(String.format("AbstractTask:%s speed too much time:%d.", getName(), interval));
            }
        } catch (Exception e) {
            LOGGER.error("任务出错：", e);
        } finally {
            this.queue.complete();
        }
    }

    public abstract void execute();

    public int getCode() {
        return 0;
    }
}
