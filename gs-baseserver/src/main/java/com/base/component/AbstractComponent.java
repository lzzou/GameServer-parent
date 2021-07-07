package com.base.component;

/**
 * 抽象组件接口
 *
 * @Author: zlz
 */
public abstract class AbstractComponent implements IComponent {
    @Override
    public boolean reload() {
        return initialize();
    }

    @Override
    public boolean start() {
        return true;
    }
}
