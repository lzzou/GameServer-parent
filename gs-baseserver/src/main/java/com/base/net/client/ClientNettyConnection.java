package com.base.net.client;

import com.base.net.nettytd.BetterWrite;
import com.base.type.CommonConst;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

/**
 * 基于Netty的IClientConnection实现。
 */
public class ClientNettyConnection extends AbstractClientConnection {
    private Channel session = null;

    /**
     * 连接的平台ID
     */
    private int platformID;
    /**
     * 连接的区域
     */
    private int areaID;
    /**
     * 连接的频道ID
     */
    private int channelID;

    /**
     * @param packetHandler
     */
    public ClientNettyConnection(AbstractClientPacketHandler packetHandler, Channel session) {
        super(packetHandler);
        this.session = session;
    }

    @Override
    public void send(Object packet) {
        if (session != null && session.isWritable()) {
            BetterWrite.write(session, packet, null);
        }
    }

    @Override
    public void setEncryptionKey(int[] key) {
        AttributeKey<int[]> val = AttributeKey.valueOf(CommonConst.ENCRYPTION_KEY);
        session.attr(val).set(key);
    }

    @Override
    public void setDecryptionKey(int[] key) {
        AttributeKey<int[]> val = AttributeKey.valueOf(CommonConst.ENCRYPTION_KEY);
        session.attr(val).set(key);
    }

    @Override
    public void closeConnection(boolean immediately) {
        if (this.session != null && session.isOpen()) {
            session.close();
        }
    }

    @Override
    public String getClientIP() {
        return ((InetSocketAddress) session.remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public Channel getSession() {
        return session;
    }

    @Override
    public int getPlatformID() {
        return platformID;
    }

    @Override
    public int getAreaID() {
        return areaID;
    }

    @Override
    public void setPlatformID(int id) {
        this.platformID = id;
    }

    @Override
    public void setAreaID(int id) {
        this.areaID = id;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

}
