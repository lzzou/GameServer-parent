package com.base.component;

/**
 * 组件接口。组件的生命周期：initialize->start->stop。
 */
public interface IComponent {
    /**
     * 初始化组件
     */
    boolean initialize();

    /**
     * 启动组件
     */
    boolean start();

    /**
     * 停止组件
     */
    void stop();

    /**
     * 重新加载组件
     */
    boolean reload();
}
