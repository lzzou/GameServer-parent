package com.base.command;

import com.base.net.CommonMessage;
import com.base.net.client.IClientConnection;
import com.base.net.server.IServerConnector;

/**
 * 命令接口
 *
 * @author zlz
 */
public class ICommand {
    /**
     * 客户端连接处理
     *
     * @param session session
     * @param packet  CommonMessage
     * @throws Exception 异常
     */
    public void execute(IClientConnection session, CommonMessage packet) throws Exception {

    }

    /**
     * 服务器连服务器处理
     *
     * @param session session
     * @param packet  CommonMessage
     * @throws Exception 异常
     */
    public void execute(IServerConnector session, CommonMessage packet) throws Exception {

    }
}
