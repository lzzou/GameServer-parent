package com.game.user.cmd;

import com.base.net.CommonMessage;
import com.base.net.client.IClientConnection;

public class ConnectionCmdTask implements Runnable {
    private IClientConnection connect;
    protected AbstractUserCmd cmd;
    protected CommonMessage message;

    public ConnectionCmdTask(AbstractUserCmd cmd, CommonMessage message, IClientConnection connect) {
        this.connect = connect;
        this.cmd = cmd;
        this.message = message;
    }

    protected void execute() {
        this.cmd.executeConnect(this.connect, this.message);
    }

    @Override
    public void run() {
        this.execute();
    }
}
