package com.base.net.server;

import com.base.net.CommonMessage;
import com.google.protobuf.GeneratedMessage.Builder;

import java.util.concurrent.Executor;

/**
 * 服务器之间的链接，发起方以客户端身份连接到其他服务器的连接器
 */
public interface IServerConnector {
    /**
     * 获取连接地址（IP或域名）
     *
     * @return 连接地址（IP或域名）
     */
    String getAddress();

    /**
     * 获取连接端口
     *
     * @return 连接端口
     */
    int getPort();

    /**
     * 连接
     *
     * @return 连接成功则返回true，否则返回false。
     */
    boolean connect();

    boolean connect(Executor executor);

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 是否已连接。
     *
     * @return 已连接则返回true，否则返回false。
     */
    boolean isConnected();

    /**
     * 发送数据包
     *
     * @param msg 待发送的数据包
     */
    void send(CommonMessage msg);

    /**
     * 发送数据包
     *
     * @param msg      待发送的数据包
     * @param playerID 玩家ID
     */
    void send(CommonMessage msg, int playerID);

    void send(int code, Builder<?> builder);

    void send(int code, Builder<?> builder, int playerID);
}
