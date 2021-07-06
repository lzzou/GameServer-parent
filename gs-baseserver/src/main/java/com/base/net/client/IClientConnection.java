package com.base.net.client;

import io.netty.channel.Channel;

/**
 * 客户端连接的代理
 */
public interface IClientConnection {
    /**
     * 获取客户端IP
     *
     * @return
     */
    String getClientIP();

    void setClientID(int clientID);

    int getClientID();

    int getPlatformID();

    int getAreaID();

    void setPlatformID(int id);

    void setAreaID(int id);

    /**
     * 获取数据包处理器。
     *
     * @return
     */
    AbstractClientPacketHandler getPacketHandler();

    /**
     * 获取连接持有者。
     *
     * @return
     */
    IConnectionHolder getHolder();

    /**
     * 设置连接持有者。
     *
     * @param holder
     */
    void setHolder(IConnectionHolder holder);

    /**
     * 发送数据包。
     *
     * @param packet
     */
    void send(Object packet);

    /**
     * 连接关闭时的回调。
     */
    void onDisconnect();

    /**
     * 设置加密密钥
     *
     * @param key 加密密钥
     */
    void setEncryptionKey(int[] key);

    /**
     * 设置解密密钥
     *
     * @param key 解密密钥
     */
    void setDecryptionKey(int[] key);

    /**
     * @param immediately是否马上关闭
     */
    void closeConnection(boolean immediately);

    /**
     * 获取连接的session<br>
     *
     * @return
     * @Test
     */
    public Channel getSession();

}
