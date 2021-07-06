package com.base.net.server;

import com.base.command.ICommand;
import com.base.component.AbstractCommandComponent;
import com.base.net.CommonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器包处理器，用来处理从目标服务器接收到的包，将网络层接收到的数据包往上层传递。
 */
public abstract class AbstractServerPacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServerPacketHandler.class);

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
            LOGGER.error("AbstractServerPacketHandler not found!");
            return;
        }

        ICommand cmd = component.getCommand(code);

        if (cmd == null) {
            LOGGER.error(" AbstractServerPacketHandler : Can not found command code = " + code + ",drop this packet.");
            return;
        }

        try {
            cmd.execute(client, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public abstract AbstractCommandComponent getComponent();
}
