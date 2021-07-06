package com.base.net.server;

import com.base.type.CommonNettyConst;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器连接器的处理器
 */
public class ServerConnectorIoHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConnectorIoHandler.class);

    private AbstractServerPacketHandler handler;

    public ServerConnectorIoHandler(AbstractServerPacketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        IServerConnector connector = (IServerConnector) ctx.channel().attr(CommonNettyConst.SERVER_CONNECTOR).get();
        handler.process(connector, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        IServerConnector conn = (IServerConnector) ctx.channel().attr(CommonNettyConst.SERVER_CONNECTOR).get();

        if (conn != null) {
            conn.disconnect();
            LOGGER.info("session [IServerConnector] closed. host -- {}:{} ", conn.getAddress(), conn.getPort());
        }
    }

    /* (non-Javadoc)
     * @see io.netty.channel.ChannelInboundHandlerAdapter#exceptionCaught(io.netty.channel.ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }
}
