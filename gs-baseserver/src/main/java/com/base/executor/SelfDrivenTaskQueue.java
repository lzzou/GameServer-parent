package com.base.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 自驱动任务队列，队列中同时最多只能一个任务在执行。使用线程池执行任务。
 */
public class SelfDrivenTaskQueue {
    private static final Logger LOGGER = LoggerFactory.getLogger(SelfDrivenTaskQueue.class);

    private static int MAX_OVER_TASK = 30;

    private static int MAX_STOP_TASK = 100;

    /**
     * 执行Task的线程池
     */
    private ExecutorPool threadPool = null;

    /**
     * 任务队列。队头元素是正在执行的任务。
     */
    private Queue<Runnable> taskQueue = null;

    /**
     * 运行锁，用于确保同时最多只能有一个任务在执行。任务队列本身是线程安全的。
     */
    private ReentrantLock runningLock = null;

    public SelfDrivenTaskQueue(ExecutorPool pool) {
        this.threadPool = pool;
        // 使用无锁线程安全队列
        this.taskQueue = new LinkedList<Runnable>();

        this.runningLock = new ReentrantLock();
    }

    /**
     * 往任务队列中添加任务。
     *
     * @param task
     */
    public void add(Runnable task) {
        this.runningLock.lock();
        try {
            if (this.taskQueue.isEmpty()) {
                this.taskQueue.add(task);

                // 没有任务在执行，开始执行新添加的。
                this.threadPool.submit(task);
            } else {
                // 有任务正在执行，将新任务添加到队列中，等待执行。
                this.taskQueue.add(task);

                if (this.taskQueue.size() > MAX_OVER_TASK) {
                    if (this.taskQueue.peek() != null) {
                        if (this.taskQueue.peek() instanceof AbstractTask) {
                            AbstractTask task2 = (AbstractTask) this.taskQueue.peek();

                            LOGGER.error("SelfDrivenTaskQueue Add Count Over 30.size:{} - executing name:{}",
                                    this.taskQueue.size(), task2.getName());

                            if (this.taskQueue.size() > MAX_STOP_TASK) {
                                LOGGER.error("SelfDrivenTaskQueue Add Count Over 100.size return.:{} - executing name:{}",
                                        this.taskQueue.size(), task2.getName());
                                return;
                            }
                        }
                    }
                }
            }
        } finally {
            this.runningLock.unlock();
        }
    }

    /**
     * 完成一个任务。<br>
     * 任务完成的时候，必须调用本方法来驱动后续的任务
     */
    public void complete() {
        this.runningLock.lock();
        try {
            if (!this.taskQueue.isEmpty()) {
                // 移除已经完成的任务。
                this.taskQueue.remove();
                // 完成一个任务后，如果还有任务，则继续执行。
                if (!this.taskQueue.isEmpty()) {
                    this.threadPool.submit(this.taskQueue.peek());
                }
            }
        } finally {
            this.runningLock.unlock();
        }
    }

    public void clear() {
        taskQueue.clear();
    }
}
