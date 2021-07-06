package com.base.server;

import com.base.hooker.BaseShutdownHooker;
import com.base.hooker.IStopHooker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: longzhang_zou
 * @Date: 2020年05月28日 16:41
 * @Description: 服务器基础类，包含3个基础模板方法（start，loadComponent，stop）
 */
public abstract class BaseServer implements IStopHooker {

    private static final Logger log = LoggerFactory.getLogger(BaseServer.class);

    /**
     * 基础模板方法-start，启动同时，给服务器加上shutdownHooker钩子
     */
    public boolean start() {
        if (loadComponent()) {
            Runtime.getRuntime().addShutdownHook(new BaseShutdownHooker(this));
            return true;
        }

        return false;
    }

    /**
     * 基础模板方法-loadComponent，加载组件
     */
    protected abstract boolean loadComponent();

    /**
     * 基础模板方法-stop，停止服务
     */
    public abstract void stop();

    @Override
    public void callbackHooker() {
        log.error("BaseServer callbackHooker is Running.");
        stop();
    }
}
