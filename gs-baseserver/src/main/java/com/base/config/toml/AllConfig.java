package com.base.config.toml;

import java.util.List;

/**
 * AllConfig
 *
 * @author zlz
 * @date 2023/11/10 14:47
 */
public class AllConfig {
    /**
     * 服务器配置
     */
    public ServerConfig server;

    /**
     * 数据库配置
     */
    public List<DatabaseConfig> database;

    /**
     * 缓存配置
     */
    public CacheConfig cache;

    /**
     * web配置
     */
    public WebServerConfig web;
}
