package com.base.net.server;

import com.base.command.ICommand;
import com.base.component.AbstractCommandComponent;
import com.base.net.CommonMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器包处理器，用来处理从目标服务器接收到的包，将网络层接收到的数据包往上层传递。
 */
@Slf4j
public abstract class AbstractServerPacketHandler {

    /**
     * 封包处理。
     *
     * @param client 连接器
     * @param packet 封包
     */
    public void process(IServerConnector client, Object packet) {
        CommonMessage msg = (CommonMessage) packet;
        short code = msg.getCode();

        AbstractCommandComponent component = getComponent();

        if (component == null) {
            log.error("AbstractServerPacketHandler not found!");
            return;
        }

        ICommand cmd = component.getCommand(code);

        if (cmd == null) {
            log.error(" AbstractServerPacketHandler : Can not found command code = " + code + ",drop this packet.");
            return;
        }

        try {
            cmd.execute(client, msg);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public abstract AbstractCommandComponent getComponent();
}
