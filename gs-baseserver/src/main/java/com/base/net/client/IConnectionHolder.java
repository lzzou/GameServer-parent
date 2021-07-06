package com.base.net.client;

/**
 * 连接持有者接口。一般由充当客户端角色的类来实现本接口，如GamePlayer。
 */
public interface IConnectionHolder {
    /**
     * 连接关闭时的回调。
     */
    void onDisconnect();

    /**
     * 获取持有的连接。
     *
     * @return
     */
    IClientConnection getClientConnection();

    /**
     * 设置持有的连接。
     *
     * @param conn
     */
    void setClientConnection(IClientConnection conn);

}
