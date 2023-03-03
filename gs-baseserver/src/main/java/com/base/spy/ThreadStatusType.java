package com.base.spy;

/**
 * 线程状态
 *
 * @author zlz
 */
public enum ThreadStatusType {
    /**
     *
     */
    Unknow(0),

    /**
     * 正常运行
     */
    Runnable(1),

    /**
     * 死锁
     */
    Deadlock(2),

    /**
     * 线程在无限等待唤醒
     */
    Waiting(3),
    /**
     *
     */
    Waiting_Monitor_Entry(4),
    /**
     *
     */
    Suspended(5),

    /**
     * 线程在等待monitor锁(synchronized关键字)
     */
    Blocked(6),

    /**
     * 空闲状态-被使用过的线程再次放入线程池时的状态
     */
    Parked(7),

    /**
     * 线程等待唤醒，但设置了时限
     */
    Timed_Waiting(8),

    ;

    /**
     *
     */
    private int value;

    private ThreadStatusType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ThreadStatusType parse(int value) {
        ThreadStatusType[] states = ThreadStatusType.values();

        for (ThreadStatusType s : states) {
            if (s.getValue() == value) {
                return s;
            }
        }

        return Unknow;
    }
}
