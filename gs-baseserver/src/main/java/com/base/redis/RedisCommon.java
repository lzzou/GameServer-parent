package com.base.redis;

import com.base.data.cache.ObjectClone;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * 缓存处理公共接口
 */
public abstract class RedisCommon {
    protected RedisClient client;

    /**
     * 修改的key保存记录(删除的数据直接在数据库删除)
     */
    protected HashMap<Byte, RedisSaveHandler<?>> handlerMap = new HashMap<>();

    protected RedisCommon() {
        this(0);
    }

    protected RedisCommon(int redisDBID) {
        client = RedisClientComponent.getClient(redisDBID);
    }

    /**
     * 添加修改的key
     *
     * @param type
     * @param key
     */
    protected void addKey(byte type, String key) {
        RedisSaveHandler<?> handler = handlerMap.get(type);
        if (handler != null) {
            handler.addKey(key);
        }
    }

    /**
     * 添加Key
     *
     * @param type
     * @param key
     * @param subKey
     */
    protected void addKey(byte type, String key, String subKey) {
        RedisSaveHandler<?> handler = handlerMap.get(type);
        if (handler != null) {
            handler.addMapKey(key, subKey);
        }
    }

    /**
     * 添加key
     *
     * @param type
     * @param key
     * @param subKey
     */
    protected void addKey(byte type, String key, int subKey) {
        addKey(type, key, String.valueOf(subKey));
    }

    /**
     * 定时保存数据
     */
    public void save() {
        Collection<RedisSaveHandler<?>> vals = handlerMap.values();

        for (RedisSaveHandler<?> handler : vals) {
            handler.save(client);
        }
    }

    /**
     * 重置玩家信息的标志位位false
     *
     * @param list
     */
    protected void resetChanged(List<? extends ObjectClone> list) {
        if (list != null) {
            for (ObjectClone c : list) {
                c.setChanged(false);
            }
        }
    }

    /**
     * 重置玩家信息的标志位位false
     */
    protected void resetChanged(ObjectClone obj) {
        if (obj != null) {
            obj.setChanged(false);
        }
    }

    public abstract void resetTableMaxId();

    public abstract Long getMaxId();
}
