package com.base.net.coder;

import com.base.net.CommonMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 动态秘钥的协议加密方式原理：
 * 客户端和服务器的秘钥错位而已。
 * 客户端的发送 和 服务器的接收 秘钥保持一致
 * 服务器的发送 和 客户端的接收 秘钥保持一致
 *
 * @author dream
 */
public class StrictFactory implements ProtocolFactory {
    @Override
    public ByteToMessageDecoder getDecoder() {
        return new StrictMessageDecoder();
    }

    @Override
    public MessageToByteEncoder<CommonMessage> getEncoder() {
        return new StrictMessageEncoder();
    }

    @Override
    public ChannelHandlerAdapter getHandler() {
        return null;
    }
}
