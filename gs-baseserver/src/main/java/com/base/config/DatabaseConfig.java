package com.base.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * 数据库连接配置
 *
 * @author zlz
 */
public class DatabaseConfig {
    @XmlElementWrapper(name = "druid")
    @XmlElement(name = "db")
    public List<DruidPoolConfig> druidPools;

    /**
     * Druid连接池配置
     */
    public static class DruidPoolConfig {
        /**
         * 名称
         */
        @XmlAttribute(name = "name")
        public String name;

        /**
         * 区域
         */
        @XmlAttribute(name = "area")
        public String area;

        public String url;
        public String username;
        public String password;

        /**
         * 最大并发连接数
         */
        public int maxActive = 4;
        /**
         * 初始化时建立物理连接的个数
         */
        public int initialSize = 2;
        /**
         * 最小连接池数量
         */
        public int minIdle = 2;
        /**
         * Druid内置提供一个StatFilter，用于统计监控信息
         */
        public String filters = "";
        /**
         * 获取连接时最大等待时间，单位毫秒
         */
        public int maxWait = 600000;
    }

}
