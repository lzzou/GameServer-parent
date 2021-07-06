package com.zlz.user.cmd;

import com.base.command.ICommand;
import com.base.net.CommonMessage;
import com.base.net.client.IClientConnection;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象用户命令调度器
 */
public abstract class AbstractUserCmd extends ICommand {
    protected static Gson gson = new Gson();

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /**
     * 建立连接
     */
    public void executeConnect(IClientConnection conn, CommonMessage packet) {

    }

    /**
     * 用户任务的调度分配， Handler调用
     */
    @Override
    public final void execute(IClientConnection conn, CommonMessage packet) {
        long time = System.currentTimeMillis();
        if (conn == null) {
            logger.error("AbstractUserCmd IClientConnection Is Null.");
            return;
        }

/*        GamePlayer player = (GamePlayer) conn.getHolder();

        if (player == null || !player.isConnect()) {
            ConnectionCmdTask task = new ConnectionCmdTask(this, packet, conn);
            GamePlayer.userCmdpool.getWorkerService().submit(task);
        } else {
            UserCmdTask task = new UserCmdTask(this, packet, player);
            player.getDrivenTaskQueue().add(task);
        }

        if (System.currentTimeMillis() - time > 20) {
            logger.error("AbstractUserCmd:" + this.getClass().getName() + " execute overtime. time:" + (System.currentTimeMillis() - time));
        }*/
    }

    //public abstract void execute(GamePlayer player, CommonMessage packet);

}
