/**
 *
 */
package com.zlz.scheduler;

import com.zlz.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 一个简单、高性能的定时器和调度器
 *
 * @date Apr 28, 2017 11:31:06 AM
 * @author dansen
 * @desc
 */
public class OakScheduler {
    private static Logger logger = LoggerFactory.getLogger(OakScheduler.class);
    /** 线程数量 */
    private static final int DEFAULT_INITIAL_CAPACITY = 5;

    /** 一天间隔 */
    public static final int DAY_INTERVAL = 24 * 3600 * 1000;

    /** 唯一的单例 */
    private static OakScheduler currentScheduler = new OakScheduler();
    /** 一个线程池 */
    private ExecutorService service = null;
    /** 任务队列 */
    private volatile Queue<Job> taskQueue = null;
    /** 退出标识 */
    private volatile boolean isRunning = true;
    /** 当前最近的任务时间 */
    private volatile long leftTime = 0;
    /** 任务ID */
    private volatile AtomicInteger jobID = new AtomicInteger(0);

    private class KernelJob extends Job {
        @Override
        public void run() {
            long time = System.currentTimeMillis();

            while (isRunning && !taskQueue.isEmpty()) {
                Job job = taskQueue.peek();

                // 取出所有到时的任务
                if (time >= job.getNextTime()) {
                    service.submit(job);
                    taskQueue.poll();

                    if (job.getInterval() > 0) {
                        job.setNextTime(job.getNextTime() + job.getInterval());
                        taskQueue.add(job);
                    }
                } else {
                    break;
                }
            }

            if (taskQueue.isEmpty()) {
                leftTime = 0;
            } else {
                leftTime = taskQueue.peek().getNextTime() - time;
            }

            try {
                // 继续下一次
                synchronized (taskQueue) {
                    taskQueue.wait(leftTime);
                }

                if (!service.isShutdown()) {
                    service.submit(this);
                }
            } catch (Exception e) {
                logger.info("", e);
            }
        }
    }

    /**
     * 初始化
     *
     * @return
     */
    public boolean startup() {
        service = Executors.newFixedThreadPool(DEFAULT_INITIAL_CAPACITY, new NamedThreadFactory("oak-scheduler"));

        /**
         * 采用优先级队列，每次取得堆顶元素
         */
        taskQueue = new PriorityBlockingQueue<>(DEFAULT_INITIAL_CAPACITY, new Comparator<Job>() {
            @Override
            public int compare(Job o1, Job o2) {
                return (int) (o1.getNextTime() - o2.getNextTime());
            }

        });

        service.submit(new KernelJob());
        return true;
    }

    /**
     * 添加一个job
     *
     * @param delay
     *            延迟多长时间执行(ms)
     * @param interval
     *            间隔多长时间执行，如果是0则表示不重复执行(ms)
     * @param job
     *            任务对象
     * @return
     */
    private long add(int delay, int interval, Job job) {
        if (job == null) {
            return 0;
        }

        if (service == null) {
            startup();
        }

        if (delay < 0 || interval < 0) {
            return 0;
        }

        job.setInterval(interval);

        job.setJobID(jobID.incrementAndGet());

        job.setDelay(delay);

        job.setNextTime(System.currentTimeMillis() + delay);

        taskQueue.add(job);

        // 通知所有线程
        synchronized (taskQueue) {
            taskQueue.notifyAll();
        }

        return job.getJobID();
    }

    private OakScheduler() {

    }

    /**
     * 对外接口
     *
     * @param delay
     * @param interval
     * @param job
     */
    public static long schedule(int delay, int interval, Job job) {
        return currentScheduler.add(delay, interval, job);
    }

    /**
     * 对外接口
     *
     * @param delay
     * @param interval
     * @param consumer
     * @return jobID 返回一个任务ID，便于删除该任务
     */
    public static long schedule(int delay, int interval, Consumer<Job> consumer) {
        long j = currentScheduler.add(delay, interval, new Job() {
            @Override
            public void run() {
                try {
                    long time = System.currentTimeMillis();
                    consumer.accept(this);
                    long current = System.currentTimeMillis();

                    if (current - time > 200) {
                        logger.info(String.format("schedule (%s) spend too much time %d", consumer,
                                current - time));
                    }
                } catch (Exception e) {
                    logger.info("", e);
                }
            }
        });

        return j;
    }

    private void unscheduleInternal(long jobID) {
        taskQueue.removeIf(p -> {
            return p.getJobID() == jobID;
        });
    }

    /**
     * 停止定时任务
     *
     * @param jobID
     */
    public static void unschedule(long jobID) {
        currentScheduler.unscheduleInternal(jobID);
    }

    /**
     * lambda表达式
     *
     * @param consumer
     */
    public static long async(Consumer<Job> consumer) {
        return schedule(0, 0, consumer);
    }

    /**
     * 结束定时器
     */
    public void stop() {
        if (service != null) {
            isRunning = false;
            service.shutdown();
        }
    }

    public static void status() {
        logger.warn("oak schedule infomations:");
        logger.warn("---- job count:" + currentScheduler.taskQueue.size());
        System.out.println();
    }

    //FOR TEST
    public static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) {

        for (int i = 0; i < 100000; ++i) {
            OakScheduler.schedule(100, 0, p -> {
                System.out.println(p.getJobID() + " " + System.currentTimeMillis() + " " + count.incrementAndGet());
            });
        }

    }
}
