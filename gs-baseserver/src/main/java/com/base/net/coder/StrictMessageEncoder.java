package com.base.net.coder;

import com.base.net.CommonMessage;
import com.base.type.CommonNettyConst;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 动态加密-加密处理
 *
 * @author zlz
 */
@Slf4j
public class StrictMessageEncoder extends MessageToByteEncoder<CommonMessage> {

    public StrictMessageEncoder() {

    }

    /**
     * 获取当前加密密钥
     *
     * @param session
     * @return
     */
    private int[] getContext(ChannelHandlerContext session) {
        int[] keys = (int[]) session.channel().attr(CommonNettyConst.ENCRYPTION_KEY).get();
        if (keys == null) {
            keys = new int[]{0xae, 0xbf, 0x56, 0x78, 0xab, 0xcd, 0xef, 0xf1};
            session.channel().attr(CommonNettyConst.ENCRYPTION_KEY).set(keys);
        }

        return keys;
    }

    @Override
    protected void encode(ChannelHandlerContext session, CommonMessage message, ByteBuf out) throws Exception {
        try {
            // 若存在不同线程给同一玩家发送数据的情况，因此加密过程需要同步处理
            CommonMessage msg = (CommonMessage) message;

            int lastCipherByte = 0;
            int[] encryptKey = getContext(session);

            byte[] plainText = msg.toByteBuffer().array();

            int length = plainText.length;
            ByteBuffer cipherBuffer = ByteBuffer.allocate(length);

            // 加密首字节
            lastCipherByte = (byte) ((plainText[0] ^ encryptKey[0]) & 0xff);
            cipherBuffer.put((byte) lastCipherByte);

            // 循环加密
            int keyIndex = 0;
            for (int i = 1; i < length; i++) {
                keyIndex = i & 0x7;
                encryptKey[keyIndex] = ((encryptKey[keyIndex] + lastCipherByte) ^ i) & 0xff;
                lastCipherByte = (((plainText[i] ^ encryptKey[keyIndex]) & 0xff) + lastCipherByte) & 0xff;
                cipherBuffer.put((byte) lastCipherByte);
            }

            cipherBuffer.flip();
            out.writeBytes(cipherBuffer.array());
            session.flush();

            // 调试打印IP和包头
            // String ip = ((InetSocketAddress) session.channel().remoteAddress()).getAddress().toString();
            // LOGGER.error("send: {}, {}", ip, msg.headerToStr());
        } catch (Exception ex) {
            log.error("catch error for encoding packet:", ex);
            throw ex;
        }
    }
}
