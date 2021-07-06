package com.base.net.client;

/**
 * IClientConnection抽象基类，所有的IClientConnection实现类必须从此类继承。
 */
public abstract class AbstractClientConnection implements IClientConnection {
    /**
     * 连接持有者
     */
    private IConnectionHolder holder = null;

    /**
     * 客户端封包处理器
     */
    private AbstractClientPacketHandler packetHandler = null;

    /**
     * 计数ID
     */
    private int clientID;

    /**
     * 构造方法
     *
     * @param packetHandler 客户端封包处理器
     */
    public AbstractClientConnection(AbstractClientPacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Override
    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public int getClientID() {
        return this.clientID;
    }

    @Override
    public AbstractClientPacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public IConnectionHolder getHolder() {
        return holder;
    }

    @Override
    public void setHolder(IConnectionHolder holder) {
        this.holder = holder;
        if (this.holder != null) {
            this.holder.setClientConnection(this);
        }
    }

    @Override
    public void onDisconnect() {
        if (holder != null) {
            holder.onDisconnect();
            holder = null;
        }
    }

    @Override
    public int getPlatformID() {
        return 0;
    }

    @Override
    public int getAreaID() {
        return 0;
    }

    @Override
    public void setPlatformID(int id) {

    }

    @Override
    public void setAreaID(int id) {

    }

}
