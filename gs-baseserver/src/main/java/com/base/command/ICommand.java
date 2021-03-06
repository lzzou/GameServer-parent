package com.base.command;

import com.base.net.CommonMessage;
import com.base.net.client.IClientConnection;
import com.base.net.server.IServerConnector;

/**
 * 命令接口
 *
 * @author dream
 */
public class ICommand {
    /**
     * 客户端连接处理
     *
     * @param session
     * @param packet
     * @throws Exception
     */
    public void execute(IClientConnection session, CommonMessage packet) throws Exception {

    }

    /**
     * 服务器连服务器处理
     *
     * @param session
     * @param packet
     * @throws Exception
     */
    public void execute(IServerConnector session, CommonMessage packet) throws Exception {

    }
}
