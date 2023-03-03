package com.base.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * 缓存服务器代理配置
 *
 * @author zlz
 */
public class CacheServerConfig {

    @XmlElementWrapper(name = "redis")
    @XmlElement(name = "detail")
    public List<RedisConfig> redisList;

    public static class RedisConfig {
        @XmlAttribute
        public int key;

        @XmlAttribute
        public String desc;
    }

    public String ip;

    public int port;

    public String password;

    public int expiredTime;

    public int syncInterval;

    public boolean isCompress = false;

    public String packages;

}
