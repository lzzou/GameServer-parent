package com.base.net.client;

import com.base.type.CommonNettyConst;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ClientIOHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientIOHandler.class);

    protected AbstractClientPacketHandler handler = null;

    public ClientIOHandler(AbstractClientPacketHandler h) {
        handler = h;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        IClientConnection connection = new ClientNettyConnection(handler, ctx.channel());
        ctx.channel().attr(CommonNettyConst.CLIENT_CONNECTION).set(connection);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        IClientConnection conn = (IClientConnection) ctx.channel().attr(CommonNettyConst.CLIENT_CONNECTION).get();
        conn.onDisconnect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("收到消息：{}", msg);
        // super.channelRead(ctx, msg);
        // IClientConnection conn = (IClientConnection) ctx.channel().attr(CommonNettyConst.CLIENT_CONNECTION).get();
//        this.handler.process(conn, msg);
        ctx.writeAndFlush("服务端：1");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception:", cause);
    }
}
