package com.base.config;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * web配置
 */
public class WebServerConfig {
    @XmlElementWrapper(name = "mapping")
    @XmlElement(name = "servlet")
    public List<ServletConfig> mapping;

    public static class ServletConfig {
        @XmlAttribute(name = "name")
        public String name;

        @XmlAttribute(name = "classPath")
        public String classPath;

        @XmlAttribute(name = "url")
        public String url;
    }

    /**
     * servlet的端口
     */
    public int port;

    /**
     * handler所在包名
     */
    public String packages;

    /**
     * web资源路径目录路径
     */
    public String resourcePath;

    /**
     * 是否显示文件夹信息
     */
    public boolean isShowDirectory;

    /**
     * 默认显示主页
     */
    public String welcomeFile;

}
