package com.base.net.coder;

import com.base.net.CommonMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommonFactory implements ProtocolFactory {
    @Override
    public ByteToMessageDecoder getDecoder() {
        return new CommonMessageDecoder();
    }

    @Override
    public MessageToByteEncoder<CommonMessage> getEncoder() {
        return new CommonMessageEncoder();
    }

    @Override
    public ChannelHandlerAdapter getHandler() {
        return null;
    }
}
