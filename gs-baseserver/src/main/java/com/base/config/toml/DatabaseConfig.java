package com.base.config.toml;

/**
 * 数据库连接配置
 *
 * @author zlz
 */
public class DatabaseConfig {

    /**
     * 名称
     */
    public String name;


    public String url;
    public String username;
    public String password;
    public String filters;
    /**
     * 最大并发连接数
     */
    public int maxActive = 20;
    /**
     * 初始化时建立物理连接的个数
     */
    public int initialSize = 10;
    /**
     * 最小连接池数量
     */
    public int minIdle = 5;
    /**
     * 获取连接时最大等待时间，单位毫秒
     */
    public int maxWait = 600000;

}
