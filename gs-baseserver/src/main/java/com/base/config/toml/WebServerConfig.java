package com.base.config.toml;

import java.util.List;

/**
 * web配置
 *
 * @author zlz
 */
public class WebServerConfig {

    public List<ServletConfig> mapping;

    public static class ServletConfig {
        public String name;

        public String classPath;

        public String url;

        public String allow;

        public String loginUsername;

        public String loginPassword;
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
