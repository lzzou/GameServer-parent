package com.base.redis;

import com.base.component.AbstractComponent;
import com.base.component.Component;
import com.base.component.GlobalConfigComponent;
import com.base.config.CacheServerConfig;
import com.base.config.CacheServerConfig.RedisConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * redis客户端组件
 */
@Slf4j
@Component
public class RedisClientComponent extends AbstractComponent {


    /**
     * 数据过期时间（三天）
     */
    private static final int EXPIRED_TIME = 72;

    /**
     * <配置的ID,client> redis实例的集合
     */
    private static Map<Integer, RedisClient> clientMap = new HashMap<>();

    @Override
    public boolean initialize() {
        CacheServerConfig cache = GlobalConfigComponent.getConfig().cacheServer;
        List<RedisConfig> list = GlobalConfigComponent.getConfig().cacheServer.redisList;

        if (list == null || list.size() <= 0) {
            return false;
        }

        for (RedisConfig config : list) {
            try {
                RedisClient client = new RedisClient();
                if (cache.expiredTime <= 0) {
                    cache.expiredTime = EXPIRED_TIME;
                }

                if (client.init(cache.ip, cache.port, cache.expiredTime * 3600, cache.password, config.key)) {
                    clientMap.put(config.key, client);
                    log.info("add redis client : " + config.key + ", " + cache.ip + ":" + cache.port + ",expiredTime : " + cache.expiredTime);
                } else {
                    System.exit(0);
                }
            } catch (Exception e) {
                log.error("Redis Client Init Exception:", e);
            }
        }

        return true;
    }

    @Override
    public void stop() {
        for (RedisClient client : clientMap.values()) {
            client.quit();
        }

        clientMap.clear();
    }

    /**
     * 根据类型取得缓存信息
     *
     * @param redisServerID
     * @return
     */
    public static RedisClient getClient(int redisServerID) {
        RedisClient client = clientMap.get(redisServerID);
        if (client != null) {
            return client;
        }

        return clientMap.get(0);
    }

    public static List<RedisClient> getClientList() {
        List<RedisClient> list = new ArrayList<>();
        list.addAll(clientMap.values());

        return list;
    }

}
