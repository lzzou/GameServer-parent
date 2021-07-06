package com.base.event;

public interface IEventListener {
    /**
     * 事件触发时的回调。
     *
     * @param arg 事件参数
     */
    void onEvent(EventArg arg);
}
