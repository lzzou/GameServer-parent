package com.zlz.component;

import com.base.component.AbstractComponent;
import com.base.component.GlobalConfigComponent;
import com.base.redis.RedisCommon;
import com.base.rmi.IRemoteCode;
import com.zlz.util.ClassUtil;
import com.zlz.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 远程缓存管理组件
 *
 * @author zlz
 */
@Slf4j
public class RemoteCacheComponent extends AbstractComponent {
    private static volatile boolean isStop = false;


    /**
     * 缓存处理
     */
    private static Map<String, RedisCommon> remotes = new HashMap<>();

    /**
     * 定时保存线程池
     */
    private static ScheduledExecutorService threadSyncMysql = ThreadPoolUtil.singleScheduledExecutor("remote-cache");

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public boolean initialize() {
        try {
            List<Class<?>> allClasses = ClassUtil.getClasses(GlobalConfigComponent.getConfig().cacheServer.packages);
            if (Objects.nonNull(allClasses) && !allClasses.isEmpty()) {
                for (Class<?> clazz : allClasses) {
                    IRemoteCode cmd = clazz.getAnnotation(IRemoteCode.class);
                    if (cmd != null) {
                        remotes.put(cmd.code(), (RedisCommon) clazz.newInstance());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("缓存加载异常", e);
        }

        return false;
    }

    @Override
    public boolean start() {
        try {
            int interval = GlobalConfigComponent.getConfig().cacheServer.syncInterval;
            if (interval <= 0) {
                interval = 180;
            }

            // 初始化自增ID
            resetTableMaxId();

            threadSyncMysql.scheduleWithFixedDelay(RemoteCacheComponent::save, 5, interval, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("RemoteCacheComponent start error:", e);
            return false;
        }

    }

    private void resetTableMaxId() {
        remotes.forEach((k, v) -> v.resetTableMaxId());
    }

    @Override
    public void stop() {
        save();
        isStop = true;
        threadSyncMysql.shutdownNow();
        remotes.clear();
    }

    public static void save() {
        try {
            lock.writeLock().lock();

            long time = System.currentTimeMillis();

            for (RedisCommon common : remotes.values()) {
                if (isStop) {
                    break;
                }
                common.save();
            }

            long dt = System.currentTimeMillis() - time;

            if (dt > 100) {
                log.info("Cache data save to database too much time：" + dt);
            }
        } catch (Exception e) {
            log.error("Cache Synchronize DB Exception:", e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends RedisCommon> T getModule(String type) {
        return (T) remotes.get(type);
    }

    /*    *//**
     * 获得系统配置信息
     *
     * @param className
     * @return
     *//*
    @Deprecated
    private static <T> List<T> getBeanList(Class<T> className) {
        try {
            return getRemoteSystem().getBeanList(className);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
        }

        return new ArrayList<>(0);
    }

    @Deprecated
    private static <T> boolean removeData(T data) {
        try {
            return getRemoteSystem().removeData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    *//**
     * t_p信息保存(系统自动筛选是insert，还是update,主键不能变，如果主键变，需要clone一份新的)
     *
     * @param dataList
     * @return
     *//*
    @Deprecated
    private static boolean saveData(List<?>... dataList) {
        try {
            return getRemoteSystem().saveData(dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    *//**
     * 取得表的自增ID
     *
     * @param tableID
     * @return
     *//*
    public static int getTableMax(int tableID) {
        try {
            return getRemoteSystem().getTableMaxID(tableID);
        } catch (Exception e) {
            log.error("", e);
        }

        return -1;
    }*/
}
