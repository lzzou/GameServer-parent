package com.zlz.component;

import com.base.command.ICommand;
import com.base.component.AbstractCommandComponent;
import com.proto.command.UserCmdType;

public class UserCommandComponent extends AbstractCommandComponent {
    @Override
    public String getCommandPacketName() {
        return "com.zlz.user.cmd";
    }

    /**
     * 二级协议特殊处理
     */
    @Override
    public ICommand getCommand(short code) {
        ICommand cmd = cmdCache.get(code);
        if (cmd != null) {
            return cmd;
        }

        if (code > UserCmdType.GameCmdType.TWO_MIN_VALUE && code < UserCmdType.GameCmdType.TWO_MAX_VALUE) {
            return cmdCache.get((short) UserCmdType.GameCmdType.TWO_MAX_VALUE);
        }

        return null;
    }

}
