package com.base.executor;

/**
 * 延时任务
 *
 * @author zlz
 */
public abstract class AbstractDelayTask extends AbstractTask {
    private long exeTime;

    public AbstractDelayTask(int delay) {
        exeTime = System.currentTimeMillis() + delay;
    }

    public boolean checkDelayFinishAndExecute(long time) {
        if (time >= exeTime) {
            queue.add(this);
            return true;
        }

        return false;
    }
}
