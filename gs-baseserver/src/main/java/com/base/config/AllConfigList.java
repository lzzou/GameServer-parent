package com.base.config;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config")
public class AllConfigList {

    /**
     * 服务器配置
     */
    public ServerConfig server;

    /**
     * 数据库配置
     */
    public DatabaseConfig database;

    /**
     * 缓存配置
     */
    public CacheServerConfig cacheServer;

    /**
     * web配置
     */
    public WebServerConfig web;

    //public RmiConfig rmi;
}
