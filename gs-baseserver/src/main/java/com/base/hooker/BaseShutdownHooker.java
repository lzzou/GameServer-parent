package com.base.hooker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: longzhang_zou
 * @Date: 2020年05月28日 16:41
 * @Description: 钩子基础类，停止服务器的钩子
 */
public final class BaseShutdownHooker extends Thread {

    private static final Logger log = LoggerFactory.getLogger(BaseShutdownHooker.class);

    private IStopHooker hooker;

    public BaseShutdownHooker(IStopHooker server) {
        this.hooker = server;
    }

    /**
     * 退出回调，停止服务器
     */
    @Override
    public void run() {
        log.error("BaseShutdownHooker is Running.");
        if (hooker != null) {
            hooker.callbackHooker();
        }
    }
}
