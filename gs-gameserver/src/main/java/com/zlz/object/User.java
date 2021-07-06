package com.zlz.object;

import com.base.event.EventSource;
import com.base.executor.ExecutorPool;
import com.base.executor.SelfDrivenTaskQueue;
import com.base.object.AbstractUser;
import com.base.type.CommonNettyConst;
import com.zlz.module.AbstractUserModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户主类
 */
public class User extends AbstractUser {
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    /**
     * 玩家命令处理线程池
     */
    public static ExecutorPool userCmdpool = new ExecutorPool(0, Runtime.getRuntime().availableProcessors() * 3 + 1,
            "User Worker", "User Boss");
    private EventSource event;

    /**
     * 命令队列
     */
    private SelfDrivenTaskQueue cmdQueue;

    /**
     * 保存时间，每个玩家的不同，每隔一定时间同步一次
     */
    private long saveTime = 0;

    /**
     * 离线保存时间间隔
     */
    private long offlineSaveTime = 0;

    /**
     * 是否为svip玩家
     */
    private boolean isSvipUser;

    private Map<Integer, AbstractUserModule> moduleMap;
    /**
     * 构造人物对象
     */
/*    public GamePlayer(PlayerAllData info) {
        super();
        lastChatTime = new Date();
        playerAllInfo = info;
        cmdQueue = new SelfDrivenTaskQueue(userCmdpool);
        event = new EventSource();
        sender = new PlayerProxySender(this);
        moduleMap = new LinkedHashMap<Integer, AbstractPlayerModule>();
        initModule();
    }*/

    /**
     * 默认构造函数
     */
    public User() {
        super();
        moduleMap = new LinkedHashMap<>();
        event = new EventSource();
    }


    /********************************************************************************************************************/
    /**
     * 初始化模块
     */
    private void initModule() {
        // 第一个发
        // moduleMap.put(GamePlayerModuleType.PING.getValue(), new PlayerPingModule(this));
        // 放在前面，后面计算属性需要用到buffer信息
    }

    /**
     * 加载部分信息（用于客户端查看玩家信息）
     */
    public boolean loadInfo(int userID) {
        try {
            /*AccountInfo allInfo = RemoteCacheComponent.getRemotePlayer().getAccountInfo(userID);
            PlayerInfo playerInfo = RemoteCacheComponent.getRemotePlayer().getPlayerInfo(userID);
            if (allInfo == null || playerInfo == null)
                return false;

            CompeteInfo competeInfo = RemoteCacheComponent.getRemoteCompete().getCompeteInfo(userID);
            if (competeInfo == null)
                competeInfo = new CompeteInfo();

            ResourceInfo resourceInfo = RemoteCacheComponent.getRemotePlayer().getResourceInfo(userID);

            this.playerAllInfo = new PlayerAllData();
            this.playerAllInfo.setAccountInfo(allInfo);
            this.playerAllInfo.setPlayerInfo(playerInfo);
            this.playerAllInfo.setCompeteInfo(competeInfo);
            this.playerAllInfo.setResourceInfo(resourceInfo);

            ObjectBean objectBean = ObjectDataComponent.getObjectBean(playerInfo.getObjectID());
            if (objectBean == null)
                return false;
            this.playerAllInfo.setObjectBean(objectBean);

            moduleMap.put(GamePlayerModuleType.PARTNER.getValue(), new PlayerPartnerModule(this));
            moduleMap.put(GamePlayerModuleType.FUNCTION.getValue(), new PlayerOpenFunctionModule(this));
            moduleMap.put(GamePlayerModuleType.SOUL.getValue(), new PlayerSoulModule(this));
            moduleMap.put(GamePlayerModuleType.BAG.getValue(), new PlayerBagModule(this));
            moduleMap.put(GamePlayerModuleType.VIP.getValue(), new PlayerVIPModule(this));
            moduleMap.put(GamePlayerModuleType.RESOURCE.getValue(), new PlayerResourceModule(this));
            moduleMap.put(GamePlayerModuleType.ANTIQUE.getValue(), new PlayerAntiqueModule(this));
            moduleMap.put(GamePlayerModuleType.TITLE.getValue(), new PlayerTitleModule(this));
            moduleMap.put(GamePlayerModuleType.GLOBALBUFFER.getValue(), new PlayerBufferModule(this));
            moduleMap.put(GamePlayerModuleType.RECHARGE.getValue(), new PlayerRechargeModule(this));
            moduleMap.put(GamePlayerModuleType.LABEL.getValue(), new PlayerLabelModule(this));
            moduleMap.put(GamePlayerModuleType.UNION.getValue(), new PlayerUnionModule(this));
            moduleMap.put(GamePlayerModuleType.GENIUS.getValue(), new PlayerGeniusModule(this));
            moduleMap.put(GamePlayerModuleType.SOCIAL.getValue(), new PlayerSocialModule(this));
            moduleMap.put(GamePlayerModuleType.TASK.getValue(), new PlayerTaskModule(this));
            moduleMap.put(GamePlayerModuleType.ACTIVITY.getValue(), new PlayerActivityModule(this));
            moduleMap.put(GamePlayerModuleType.RENAME.getValue(), new PlayerRenameModule(this));
            moduleMap.put(GamePlayerModuleType.NOTE.getValue(), new PlayerNoteModule(this));
            moduleMap.put(GamePlayerModuleType.COMPETE.getValue(), new PlayerCompeteModule(this));
            // 逻辑计算属性、战斗力放最后面
            moduleMap.put(GamePlayerModuleType.PET.getValue(), new PlayerPetModule(this));
            moduleMap.put(GamePlayerModuleType.SKILL.getValue(), new PlayerSkillModule(this));
            moduleMap.put(GamePlayerModuleType.LOGICDATA.getValue(), new PlayerLogicDataModule(this));
*/
            load();

            return true;
        } catch (Exception e) {
            LOGGER.error("GamePlayer Simple Load Exception:", e);
        }

        return false;
    }

/*    public void addModule(GamePlayerModuleType type, AbstractPlayerModule module) {
        moduleMap.put(type.getValue(), module);
    }*/

    // @SuppressWarnings("unchecked")
/*    public <T extends AbstractPlayerModule> T getModule(int type) {
        return (T) moduleMap.get(type);
    }*/


    /*****************************************************************************************************************/

    public EventSource getEvent() {
        return event;
    }

    public SelfDrivenTaskQueue getDrivenTaskQueue() {
        return cmdQueue;
    }

/*
    public PlayerProxySender getSender() {
        return this.sender;
    }
*/


    public boolean getIsSvipUser() {
        return isSvipUser;
    }

    public void setIsSvipUser(boolean isSvip) {
        this.isSvipUser = isSvip;
    }

    @Override
    public void onDisconnect() {
        disconnect(false);
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    /**
     * 只保存playerinfo
     */
    public void savePlayerInfo() {
        try {
            // RemoteCacheComponent.getRemotePlayer().updatePlayerInfo(playerAllInfo.getPlayerInfo());
        } catch (Exception e) {
            LOGGER.error("保存玩家数据失败", e);
        }
    }

    /**
     * 定时保存，区分在线和离线，而且每个玩家的保存时间不同
     */
    public void jobSave() {
        /*long time = System.currentTimeMillis();
        if (isConnect()) {
            if (time >= saveTime)
                saveTime = time + GamePropertiesComponent.SAVE_PLAYER_TIME;
            else
                return;
        } else {
            if (time >= offlineSaveTime)
                offlineSaveTime = time + GamePropertiesComponent.SAVE_PLAYER_TIME * 5;
            else
                return;
        }*/

        save();
    }

    public boolean save() {
        try {
            /*for (AbstractPlayerModule module : moduleMap.values()) {
                if (!module.save()) {
                    LOGGER.error("GamePlayer:saveModuleData() error. module:" + module.getClass().getName());
                    return false;
                }
            }

            // 保存玩家的信息要放在module后
            RemoteCacheComponent.getRemotePlayer().updateAccountInfo(playerAllInfo.getAccountInfo());
            RemoteCacheComponent.getRemotePlayer().updatePlayerInfo(playerAllInfo.getPlayerInfo());
            RemoteCacheComponent.getRemotePlayer().updatePlayerResource(playerAllInfo.getResourceInfo());*/

        } catch (Exception e) {
            LOGGER.error("保存玩家数据失败", e);
            return false;
        }

        return true;
    }

    /**
     * 午夜刷新重置玩家模块信息
     * 强制刷新
     */
    public void refresh() {
        /*for (AbstractPlayerModule module : moduleMap.values()) {
            try {
                module.refresh(true);
            } catch (Exception e) {
                LOGGER.error("GamePlayer Refresh:", e);
            }
        }

        playerAllInfo.getPlayerInfo().setBlowCount(GamePropertiesComponent.DAILY_BLOW_COUNT);
        // 午夜更新玩家账户登录信息
        playerAllInfo.getAccountInfo().setLastLoginDate(new Date());
        playerAllInfo.getAccountInfo().setPreLoginCount(playerAllInfo.getAccountInfo().getTotalLoginCount());
        playerAllInfo.getAccountInfo().setPreLoginPeriod(playerAllInfo.getAccountInfo().getTotalLoginPeriod());*/

        save();
    }

    /**
     * 是否是强制断线
     *
     * @param isForceKick
     */
    public void disconnect(boolean isForceKick) {
        try {
            if (client != null) {
                client.setHolder(null);
                client.setClientID(0);
                client.closeConnection(true);
                client = null;
            }

            Date curTime = new Date();


            save();

        } catch (Exception e) {
            LOGGER.error("catch error when user quit:", e);
        } finally {
            /*if (isForceKick) {
                // 下线日志
                LoginLog log = new LoginLog();
                log.setUserID(getUserID());
                log.setLogTime(new Date());
                log.setLogType(1);
                LogComponent.addLoginLog(log);
                GamePlayerComponent.removePlayer(getUserID());
            }*/
        }

        // 通知排队系统
        /*LoginWaitComponent loginWaitComponent = ComponentManager.getInstance().getComponent(LoginWaitComponent.class);
        loginWaitComponent.onPlayerExit();*/
    }

    private boolean load() {
        /*for (AbstractPlayerModule module : moduleMap.values()) {
            try {
                if (!module.load()) {
                    LOGGER.error(module.getClass().getName() + " module load data from db error.");
                    return false;
                }
            } catch (Exception e) {
                LOGGER.error(module.getClass().getName() + " module load data from db error:", e);
                return false;
            }
        }*/
        return true;
    }

    public boolean login() {
        if (load()) {
            relogin();
            return true;
        }

        return false;
    }

    public void relogin() {
        /*boolean isDayFirstLogin = false;
        if (!isLoginSameDay()) {
            playerAllInfo.getAccountInfo().setTotalDay(playerAllInfo.getAccountInfo().getTotalDay() + 1);
            isDayFirstLogin = true;
            // 触发邀请码奖励条件更新
            getSocialModule().onInvitedProgressTrigger(InviteCodeConditionType.Login_Amount, 1);
        }

        playerAllInfo.getAccountInfo().setIsOnline(true);
        playerAllInfo.getAccountInfo().setTotalLoginCount(playerAllInfo.getAccountInfo().getTotalLoginCount() + 1);

        sender.sendLoginSuccess(isDayFirstLogin);
        sender.sendWeather();

        for (AbstractPlayerModule module : moduleMap.values()) {
            try {
                if (module.relogin())
                    module.send();
            } catch (Exception e) {
                LOGGER.error("Module Relogin Exception:" + module.getClass().getName(), e);
            }
        }

        playerAllInfo.getAccountInfo().setLastLoginDate(new Date());

        sender.sendLoginSuccessOver();

        // 登录日志
        LoginLog log = new LoginLog();
        log.setUserID(getUserID());
        log.setLogTime(new Date());
        log.setLogType(0);

        PlayerLogInfo logInfo = RemoteCacheComponent.getRemotePlayer().getPlayerLog(getUserID());
        if (logInfo != null) {
            log.setLoginIP(logInfo.getLoginIP());
            log.setServerID(logInfo.getServerID());
            log.setMachineID(logInfo.getMachineID());
        }

        LogComponent.addLoginLog(log);

        // 保存时间初始化
        saveTime = System.currentTimeMillis() + GamePropertiesComponent.SAVE_PLAYER_TIME;
*/
    }


/*    public int getLevel() {
        if (this.getPartnerModule() == null || this.getPartnerModule().getLeader() == null) {
            LOGGER.error("主角不存在，等级返回0.UserID:" + getUserID());
            return 0;
        }
        return this.getPartnerModule().getLeader().getLevel();
    }

    public PlayerInfo getPlayerInfo() {
        return playerAllInfo.getPlayerInfo();
    }

    *//**
     * 判断当前登录是否同一天
     *
     * @return true 同一天；false：不是同一天
     *//*
    public boolean isLoginSameDay() {
        if (TimeUtil.isSameDay(playerAllInfo.getAccountInfo().getLastLoginDate(), new Date()))
            return true;

        return false;
    }*/

    /*    *//**
     * 获取玩家vip等级
     *
     * @return
     *//*
    public int getVIPLevel() {
        return getVIPModule().getUserVIPLevel();
    }

    *//**
     * 获取玩家渠道ID
     *
     * @return
     *//*
    public int getChannelID() {
        return playerAllInfo.getAccountInfo().getChannelID();
    }

    *//**
     * 获取玩家渠道名
     *
     * @return
     *//*
    public String getChannelName() {
        return playerAllInfo.getAccountInfo().getChannelName();
    }*/


    /**
     * 更新玩家的加解密密钥
     *
     * @param newKey 新密钥
     */
    public void updateKey(int[] newKey) {
        if (client != null && newKey != null && newKey.length >= 8) {
            int[] temp;
            temp = new int[newKey.length];
            System.arraycopy(newKey, 0, temp, 0, newKey.length);
            client.getSession().attr(CommonNettyConst.DECRYPTION_KEY).set(temp);

            temp = new int[newKey.length];
            System.arraycopy(newKey, 0, temp, 0, newKey.length);
            client.getSession().attr(CommonNettyConst.ENCRYPTION_KEY).set(temp);
        }
    }


    public boolean isOnline() {
        return this.isConnect();
    }


}
