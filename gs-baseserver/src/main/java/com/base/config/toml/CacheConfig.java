package com.base.config.toml;

import java.util.List;

/**
 * 缓存服务器代理配置
 *
 * @author zlz
 */
public class CacheConfig {

    public List<RedisConfig> detail;

    public static class RedisConfig {
        public int key;
    }

    public String hostname;

    public int port;

    public String password;

    public int expiredTime;

    public int syncInterval;

    public boolean isCompress = false;

    public String packages;

}
