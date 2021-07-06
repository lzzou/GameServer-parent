package com.base.net.coder;

import com.base.net.CommonMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;
import java.util.List;

public class CommonMessageDecoder extends ByteToMessageDecoder {
    private static Logger LOGGER = LoggerFactory.getLogger(CommonMessageEncoder.class);

    public CommonMessageDecoder() {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable(CommonMessage.HDR_SIZE)) {
            return;
        }

        in.markReaderIndex();
        short headerFlag = in.order(ByteOrder.LITTLE_ENDIAN).readShort();
        if (CommonMessage.HEADER != headerFlag) {
            //当包头错误，从下一个字节开始读取，每次移动一个字节
            in.resetReaderIndex();
            in.readerIndex(in.readerIndex() + 1);
            return;
        }

        // 长度
        int length = in.order(ByteOrder.LITTLE_ENDIAN).readShort();
        if (length <= 0 || length >= Short.MAX_VALUE) {
            // 非法的数据长度
            LOGGER.debug("Message Length Invalid Length = " + length
                    + ", drop this Message.");
            return;
        }

        if (!in.isReadable(length - 4)) {
            // 数据还不够读取,等待下一次读取
            in.resetReaderIndex(); // 复位
            return;
        }

        byte[] pktBytes = new byte[length];
        in.resetReaderIndex();
        in.order(ByteOrder.LITTLE_ENDIAN).readBytes(pktBytes);

        CommonMessage packet = CommonMessage.build(pktBytes);

        if (packet != null) {
            out.add(packet);
        }
    }
}
