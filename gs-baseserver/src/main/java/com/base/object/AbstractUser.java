package com.base.object;

import com.base.net.CommonMessage;
import com.base.net.client.IClientConnection;
import com.base.net.client.IConnectionHolder;
import com.base.type.ErrorCodeType;
import com.google.protobuf.GeneratedMessage.Builder;
import com.proto.command.UserCmdType;
import com.proto.common.gen.CommonOutMsg;

/**
 * 游戏玩家接口
 */
public abstract class AbstractUser implements IConnectionHolder {

    protected IClientConnection client;

    public AbstractUser() {

    }

    @Override
    public IClientConnection getClientConnection() {
        return client;
    }

    @Override
    public void setClientConnection(IClientConnection conn) {
        this.client = conn;
    }

    public void sendMessage(CommonMessage packet) {
        if (isConnect()) {
            //packet.setParam(getUserID());
            client.send(packet);
            // if (packet.getCode() >= GameCmdType.TWO_MIN_VALUE && packet.getCode() <= GameCmdType.TWO_MAX_VALUE)
            // {
            // if (packet.getCode() != GameCmdType.TEST_MOVE_VALUE)
            // TestPrint.dreamError(100, "Game Out:" + packet.getCode());
            // }
        }
    }

    public boolean isConnect() {
        return true;
    }

    /**
     * 发送消息
     *
     * @param code
     * @param builder
     */
    public void sendMessage(int code, Builder<?> builder) {
        if (isConnect()) {
            CommonMessage packet = new CommonMessage(code);

            if (builder != null) {
                packet.setBody(builder.build().toByteArray());
            }

            sendMessage(packet);
        }
    }

    /**
     * 发送消息
     *
     * @param code
     */
    public void sendMessage(int code) {
        sendMessage(code, null);
    }

    /**
     * 发送错误代码
     *
     * @param code
     */
    public ErrorCodeType sendErrorCode(ErrorCodeType code) {
        CommonOutMsg.ErrorCodeProto.Builder builder = CommonOutMsg.ErrorCodeProto.newBuilder();
        builder.setErrorCode(code.getValue());
        sendMessage(UserCmdType.UserCmdOutType.ERROR_CODE_VALUE, builder);
        return code;
    }


/*    public int getUserID() {
        return playerAllInfo.getPlayerInfo().getUserID();
    }

    public String getNickName() {
        return playerAllInfo.getPlayerInfo().getNickName();
    }*/


    @Override
    public abstract void onDisconnect();


}
