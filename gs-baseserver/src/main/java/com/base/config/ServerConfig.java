package com.base.config;

public class ServerConfig {
    /**
     * 服务器ID
     */
    public int id;

    /**
     * 服务器类型
     */
    public String type;

    /**
     * 服务器IP
     */
    public String address;

    /**
     * 服务器TCP监听端口
     * 支持多端口
     */
    public String ports;

    /**
     * 调试辅助
     */
    public boolean isDebug;
    /**
     * 是否具有作弊权限
     */
    public boolean hasCheatPrivilege;
}
