package com.zlz.module;

import com.zlz.object.User;

/**
 * 玩家抽象module
 */
public abstract class AbstractUserModule {
    protected User user;

    public AbstractUserModule(User user) {
        this.user = user;
    }

    /**
     * 离线回调的处理，玩家离线或者被踢除后，调用此方法。
     */
    public void disconnect() {

    }

    /**
     * 从数据库加载模块信息，玩家首次登陆或者下线后登陆（不在内存的缓存时间），会调用此方法。
     * 在内存的缓存时间内，不会再调用。
     */
    public abstract boolean load();

    /**
     * 重新登录需要加载或处理的信息,玩家在缓存时间内掉线登陆会调用此方法
     */
    public boolean relogin() {
        return true;
    }

    /**
     * 每天凌晨刷新，或者登录后relogin需要刷新的逻辑，此方法，每日凌晨会固定调用
     *
     * @param isSend 是否下发信息，特别针对更新次数等需要同步的信息
     */
    public void refresh(boolean isSend) {

    }

    /**
     * 发送给客户端的数据更新，模块加载完后，会调用此方法发送需要马上同步的信息
     */
    public abstract void send();

    /**
     * 保存模块信息到数据库，定时保存信息到缓存
     */
    public abstract boolean save();
}
