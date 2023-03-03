package com.base.net.server;

import com.base.net.CommonMessage;
import com.base.net.coder.CommonFactory;
import com.base.net.coder.ProtocolFactory;
import com.base.net.nettytd.BetterWriteWithAtomic;
import com.base.type.CommonNettyConst;
import com.google.protobuf.GeneratedMessage.Builder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

/**
 * 服务器端的连接器
 */
@Slf4j
public class ServerNettyConnector implements IServerConnector {

    /**
     * 连接会话
     */
    protected Channel session = null;

    /**
     * 连接ID
     */
    protected int connectorID;

    /**
     * 默认的最大重连次数
     */
    public static final int DEFAULT_MAX_RECONNECT_TIMES = 60;

    /**
     * 发生重连的次数
     */
    protected int reconnectedCount;

    /**
     * 连接地址（IP或域名）
     */
    private String address;

    /**
     * 连接端口
     */
    private int port;

    protected ProtocolFactory factory;

    protected AbstractServerPacketHandler handler;

    /**
     * 构造方法，IoHandlerAdapter实例将由ServerConnectorIoHandler创建，
     * ProtocolCodecFactory实例将由StrictCodecFactory创建。
     *
     * @param address
     * @param port
     * @param packetHandler
     */
    public ServerNettyConnector(String address, int port, AbstractServerPacketHandler packetHandler) {
        this(address, port, packetHandler, new CommonFactory());
    }

    public ServerNettyConnector(String address, int port, AbstractServerPacketHandler packetHandler, ProtocolFactory factory) {
        this.address = address;
        this.port = port;
        this.reconnectedCount = 0;
        this.factory = factory;
        this.handler = packetHandler;
    }

    @Override
    public boolean connect() {
        try {
            Bootstrap connector = new Bootstrap();
            connector.group(new NioEventLoopGroup());
            connector.channel(NioSocketChannel.class);

            connector.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast("codec-d", factory.getDecoder());
                    ch.pipeline().addLast("codec-e", factory.getEncoder());
                    ch.pipeline().addLast("handler", new ServerConnectorIoHandler(handler));
                }
            });

            ChannelFuture future = connector.connect(getAddress(), getPort()).sync();
            session = future.channel();
            session.attr(CommonNettyConst.SERVER_CONNECTOR).set(this);
            reconnectedCount = 0;
            return true;
        } catch (Exception e) {
            reconnectedCount++;
            log.error("Cann't connect to address:{}, port:{}. Exception: {}", getAddress(), getPort(), e.toString());
            return false;
        }
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            if (session != null) {
                session.close();
                session = null;
            }
        }
    }

    @Override
    public boolean isConnected() {
        if (session != null && session.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void send(CommonMessage msg) {
        if (isConnected()) {
            BetterWriteWithAtomic.write(session, msg, null);
        }
    }

    @Override
    public void send(CommonMessage msg, int playerID) {
        msg.setParam(playerID);
        send(msg);
    }

    @Override
    public void send(int code, Builder<?> builder) {
        send(code, builder, 0);
    }

    @Override
    public void send(int code, Builder<?> builder, int playerID) {
        CommonMessage packet = new CommonMessage(code);
        packet.setParam(playerID);
        if (builder != null) {
            packet.setBody(builder.build().toByteArray());
        }
        send(packet);
    }

    public Channel getSession() {
        return session;
    }

    public int getConnectorID() {
        return connectorID;
    }


    public void setConnectorID(int connectorID) {
        this.connectorID = connectorID;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean connect(Executor executor) {
        return false;
    }

    public int getReconnectedCount() {
        return reconnectedCount;
    }
}
