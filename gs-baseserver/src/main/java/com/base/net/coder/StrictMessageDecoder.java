package com.base.net.coder;

import com.base.net.CommonMessage;
import com.base.type.CommonNettyConst;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 动态秘钥-解密处理
 */
public class StrictMessageDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(StrictMessageDecoder.class);

    // 获取密钥上下文
    private int[] getContext(ChannelHandlerContext session) {
        int[] keys = (int[]) session.channel().attr(CommonNettyConst.DECRYPTION_KEY).get();
        if (keys == null) {
            keys = new int[]{0xae, 0xbf, 0x56, 0x78, 0xab, 0xcd, 0xef, 0xf1};
            session.channel().attr(CommonNettyConst.DECRYPTION_KEY).set(keys);
        }
        return keys;
    }

    // 解密整段数据
    private byte[] decrypt(byte[] data, int[] decryptKey) throws Exception {
        if (data.length == 0) {
            return data;
        }

        if (decryptKey.length < 8) {
            throw new Exception("The decryptKey must be 64bits length!");
        }

        int length = data.length;
        int lastCipherByte;
        int plainText;
        int key;

        // 解密首字节
        lastCipherByte = data[0] & 0xff;
        data[0] ^= decryptKey[0];

        for (int index = 1; index < length; index++) {
            // 解密当前字节
            key = ((decryptKey[index & 0x7] + lastCipherByte) ^ index);
            plainText = (((data[index] & 0xff) - lastCipherByte) ^ key) & 0xff;

            // 更新变量值
            lastCipherByte = data[index] & 0xff;
            data[index] = (byte) plainText;
            decryptKey[index & 0x7] = (byte) (key & 0xff);
        }

        return data;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable(CommonMessage.HDR_SIZE)) {
            // 剩余不足，不足以解析数据包头，暂不处理
            return;
        }

        int header = 0;
        int packetLength = 0;
        int[] decryptKey = getContext(ctx);

        int cipherByte1 = 0, cipherByte2 = 0, cipherByte3, cipherByte4;

        // 此处4字节头部的解码使用直接解码形式，规避频繁的对象创建
        int plainByte1, plainByte2, plainByte3, plainByte4;
        int key;

        in.markReaderIndex();
        // 解密两字节header
        cipherByte1 = in.readByte() & 0xff;
        key = decryptKey[0];
        plainByte1 = (cipherByte1 ^ key) & 0xff;

        cipherByte2 = in.readByte() & 0xff;
        key = ((decryptKey[1] + cipherByte1) ^ 1) & 0xff;
        plainByte2 = ((cipherByte2 - cipherByte1) ^ key) & 0xff;
        // 小端处理
        header = ((plainByte2 << 8) + plainByte1);

        if (CommonMessage.HEADER != header) {
            // 当包头错误，从下一个字节开始读取，每次移动一个字节
            in.resetReaderIndex();
            in.readerIndex(in.readerIndex() + 1);
            LOGGER.error(String.format("Package Header Error:%s -- %s", header, ctx.toString()));
            // 因为加密后，协议必须按照顺序来发，一个都不能少，一旦有错无法纠正，所以直接断开
            ctx.channel().close();
            ctx.close();
            return;
        }

        // 解密两字节length
        cipherByte3 = in.readByte() & 0xff;
        key = ((decryptKey[2] + cipherByte2) ^ 2) & 0xff;
        plainByte3 = ((cipherByte3 - cipherByte2) ^ key) & 0xff;

        cipherByte4 = in.readByte() & 0xff;
        key = ((decryptKey[3] + cipherByte3) ^ 3) & 0xff;
        plainByte4 = ((cipherByte4 - cipherByte3) ^ key) & 0xff;
        // 小端处理
        packetLength = (plainByte4 << 8) + plainByte3;

        if (packetLength <= 0 || packetLength >= Short.MAX_VALUE) {
            // 非法的数据长度
            LOGGER.error("Message Length Invalid Length = " + packetLength + ", drop this Message.");
            LOGGER.error(String.format("Disconnect the client:%s", ctx.channel().remoteAddress()));
            ctx.channel().close();
            ctx.close();
            return;
        }

        if (!in.isReadable(packetLength - 4)) {
            // 数据还不够读取,等待下一次读取
            in.resetReaderIndex(); // 复位
            return;
        }

        // 预解密长度信息成功，回溯位置
        in.resetReaderIndex();

        // 读取数据并解密数据
        byte[] data = new byte[packetLength];
        in.readBytes(data, 0, packetLength);
        data = decrypt(data, decryptKey);
        CommonMessage packet = CommonMessage.build(data);
        if (packet != null) {
            out.add(packet);
            // 调试打印IP和包头
            // String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().toString();
            // LOGGER.debug("recv: {}, {}", ip, packet.headerToStr());
        }
    }
}
