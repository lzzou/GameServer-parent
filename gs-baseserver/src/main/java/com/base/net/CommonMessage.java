package com.base.net;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 游戏协议包类，描述具体游戏数据包结构。<br>
 * <br>
 * 封包规则：一个包分为包头和包体两部分，结构如下：<br>
 * 【分隔符|包长|校验和|code】【包体】。<br>
 * 其中，包头各部分长度为2字节。检验和计算范围从code开始，直到整个包结束。
 **/
@Slf4j
public class CommonMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 包头长度
     */
    public static final short HDR_SIZE = 12;
    /**
     * 包分隔符
     */
    public static final short HEADER = 0x71ab;
    /**
     * 校验和
     */
    private short checksum;
    /**
     * 协议号
     */
    private short code;
    /**
     * 玩家ID
     **/
    private int param;

    /**
     * 包体数据
     */
    private byte[] bodyData = null;

    /**
     * build实例用，防止类外部创建空消息。
     */
    private CommonMessage() {
    }

    /**
     * 构造方法
     *
     * @param code 协议号
     */
    public CommonMessage(int code) {
        this.code = (short) code;
    }

    /**
     * 构建实例。<br>
     * 注意：所构建的实例的校验和是从输入参数msgData中读取的，并非通过消息数据计算所得。
     *
     * @param msgData 消息数据，至少包括包头，允许不带包体数据。
     * @return
     */
    public static CommonMessage build(byte[] msgData) {
        CommonMessage builder = new CommonMessage();

        if (msgData == null || msgData.length < HDR_SIZE) {
            return null;
        }

        ByteBuffer buff = ByteBuffer.wrap(msgData);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        // 跳过分隔符和包长，包长由输入参数长度确定。
        buff.position(4);
        builder.checksum = buff.getShort();
        builder.code = buff.getShort();
        builder.param = buff.getInt();
        int bodyLen = msgData.length - HDR_SIZE;

        if (bodyLen > 0) {
            if (bodyLen > Short.MAX_VALUE) {
                log.error("数据包超过最大长度。max:{},len:{}.", Short.MAX_VALUE, bodyLen);
                return null;
            }

            builder.bodyData = new byte[bodyLen];
            buff.get(builder.bodyData, 0, bodyLen);

            // 检查校验和是否正确
            short getCS = builder.calcChecksum(msgData);
            if (builder.checksum != getCS) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < msgData.length; i++) {
                    stringBuilder.append(msgData[i]).append(",");
                }

                log.warn("数据包校验失败，数据包将被丢弃。code: 0x{}。校验和应为{}，实际接收校验和为{}", builder.getCode(), getCS,
                        builder.checksum);
                return null;
            }
        } else {
            builder.bodyData = null;
        }

        return builder;
    }

    /**
     * 数据包转换为ByteBuffer，包括包头和包体。
     *
     * @return
     */
    public ByteBuffer toByteBuffer() {
        short len = getLen();

        if (len < 0) {
            log.error("数据包超过short类型大小！！");
        }

        ByteBuffer buff = ByteBuffer.allocate(len);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        buff.putShort(HEADER);
        buff.putShort(len);
        buff.position(6);
        buff.putShort(code);
        buff.putInt(param);
        if (bodyData != null) {
            buff.put(bodyData);
        }
        int pos = buff.position();

        // 插入校验和
        buff.position(4);
        short check = calcChecksum(buff.array());
        this.checksum = check;
        buff.putShort(check);
        buff.position(pos);

        buff.flip();
        return buff;
    }

    /**
     * 获取数据包的长度，包括包头和包体。
     *
     * @return
     */
    public short getLen() {
        if (bodyData != null) {
            if (bodyData.length > 32750) {
                log.error("数据包超过short类型最大值！！- " + bodyData.length);
            }

            return (short) (HDR_SIZE + bodyData.length);
        } else {
            return HDR_SIZE;
        }
    }

    /**
     * 获取校验和。<br>
     * 注意：获取到的校验和可能与实时计算的不相等，这取决于您的操作顺序。
     *
     * @return
     */
    public short getChecksum() {
        return checksum;
    }

    /**
     * 获取协议号
     *
     * @return
     */
    public short getCode() {
        return code;
    }

    /**
     * 设置协议号
     *
     * @param code
     */
    public void setCode(short code) {
        this.code = code;
    }

    /**
     * 获取包体，包体允许为null。
     *
     * @return
     */
    public byte[] getBody() {
        return bodyData;
    }

    /**
     * 设置包体，包体允许为null。
     *
     * @param bytes
     */
    public void setBody(byte[] bytes) {
        this.bodyData = bytes;
    }

    /**
     * 计算校验和
     *
     * @param data 完整的消息数据，包括包头和包体，计算将从第7个字节开始。
     * @return
     */
    public short calcChecksum(byte[] data) {
        int val = 0x77;
        int i = 6;
        int size = data.length;

        byte value = 0;
        while (i < size) {
            value = data[i++];
            val += (value & 0xFF);
        }
        return (short) (val & 0x7F7F);
    }

    /**
     * 包头的字符串表示形式。
     *
     * @return
     */
    public String headerToStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("len: ").append(getLen());
        sb.append(", checksum: ").append(checksum);
        sb.append(", code: 0x").append(Integer.toHexString(code));
        sb.append(", param: ").append(param);
        return sb.toString();
    }

    /**
     * 数据包的字符串表示形式。
     *
     * @return
     */
    public String detailToStr() {
        String str = "";
        if (bodyData != null) {
            try {
                str = new String(bodyData, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                str = "(UnsupportedEncodingException)";
            }
        }
        return String.format("%s. content:%s.", headerToStr(), str);
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }
}
