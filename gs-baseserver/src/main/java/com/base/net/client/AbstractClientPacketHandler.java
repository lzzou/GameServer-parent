package com.base.net.client;

import com.base.command.ICommand;
import com.base.component.AbstractCommandComponent;
import com.base.net.CommonMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 客户端封包处理器，将网络层接收到的数据包往上层传递。
 */
public abstract class AbstractClientPacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractClientPacketHandler.class);

    /**
     * 封包处理
     *
     * @param conn   连接
     * @param packet 封包
     */
    public void process(IClientConnection conn, Object packet) {
        CommonMessage msg = (CommonMessage) packet;
        AbstractCommandComponent cm = getComponent();

        if (cm == null) {
            LOGGER.error("*****AbstractClientPacketHandler : Can not found <AbstractCommandComponent>.******");
            return;
        }

        short code = msg.getCode();
        ICommand cmd = cm.getCommand(code);

        if (cmd == null) {
            LOGGER.error("******AbstractClientPacketHandler : Can not found code = " + code + ",drop this packet.");
            return;
        }

        try {
            cmd.execute(conn, msg);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public abstract AbstractCommandComponent getComponent();
}
