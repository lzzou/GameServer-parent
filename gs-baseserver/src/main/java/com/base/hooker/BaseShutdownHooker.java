package com.base.hooker;

import lombok.extern.slf4j.Slf4j;

/**
 * 钩子基础类，停止服务器的钩子
 *
 * @author zlz
 */
@Slf4j
public final class BaseShutdownHooker extends Thread {

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
