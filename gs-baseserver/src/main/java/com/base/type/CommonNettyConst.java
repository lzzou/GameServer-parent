package com.base.type;

import com.base.net.client.IClientConnection;
import com.base.net.server.IServerConnector;
import io.netty.util.AttributeKey;

/**
 * 公共常量定义
 */
public interface CommonNettyConst {
    /**
     * 加密密钥
     */
    AttributeKey<int[]> ENCRYPTION_KEY = AttributeKey.valueOf("EncryptionKey");

    /**
     * 解密密钥
     */
    AttributeKey<int[]> DECRYPTION_KEY = AttributeKey.valueOf("DecryptionKey");

    /**
     * 客户端连接
     */
    AttributeKey<IClientConnection> CLIENT_CONNECTION = AttributeKey.valueOf("ClientConnection");

    /**
     * 服务器连接器
     */
    AttributeKey<IServerConnector> SERVER_CONNECTOR = AttributeKey.valueOf("ServerConnector");

    /**
     * 用户名
     */
    AttributeKey<String> USER_NAME = AttributeKey.valueOf("UserName");
}
