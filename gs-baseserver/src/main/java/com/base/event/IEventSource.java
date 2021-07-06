package com.base.event;

/**
 * 事件源。同一事件类型，支持多个监听者，并且监听者对象可以相同。
 */
public interface IEventSource {
    /**
     * 将监听者加入到指定事件类型的监听队列中。
     *
     * @param eventType 事件类型
     * @param listener  监听者
     */
    void addListener(int eventType, IEventListener listener);

    /**
     * 从指定事件类型的监听队列中移除指定的监听者。
     *
     * @param eventType 事件类型
     * @param listener  监听者
     */
    void removeListener(int eventType, IEventListener listener);

    /**
     * 移除指定的listener
     *
     * @param listener
     */
    void removeListener(IEventListener listener);

    /**
     * 通知监听者发生了事件。事件源由事件参数arg指定。
     *
     * @param arg 事件参数
     */
    void notifyListeners(EventArg arg);

    /**
     * 通知监听者发生了事件。事件源为当前this对象。
     *
     * @param eventType 事件类型
     */
    void notifyListeners(int eventType);
}
