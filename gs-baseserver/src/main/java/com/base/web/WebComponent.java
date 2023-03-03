package com.base.web;

import com.base.component.AbstractComponent;
import com.base.component.Component;
import com.base.component.GlobalConfigComponent;
import com.base.config.WebServerConfig;
import com.base.config.WebServerConfig.ServletConfig;
import com.zlz.util.ClassUtil;
import com.zlz.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 嵌入式http调用使用的连接组件
 */
@Slf4j
@Component
public class WebComponent extends AbstractComponent {

    /**
     * jetty自带的server
     */
    private Server server;

    private WebAppContext webAppContext;

    private ServletContextHandler servletContextHandler;

    /**
     * 添加过滤器
     *
     * @param filterClass 过滤器实现类
     * @param pathSpec    过滤路径
     * @param dispatches  过滤类型
     */
    public void addFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches) {
        if (servletContextHandler != null) {
            servletContextHandler.addFilter(filterClass, pathSpec, dispatches);
        }
    }

    public ServletContextHandler getContext() {
        return servletContextHandler;
    }

    @Override
    public boolean start() {
        super.start();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("WebComponent Start Exception:", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean initialize() {
        WebServerConfig web = GlobalConfigComponent.getConfig().web;
        server = new Server(web.port);

        try {
            webAppContext = new WebAppContext();
            servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

            // 如果是windows系统，不需要进行文件map优化，防止锁定文件
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
            }

            // 从相应的包加载WebComponent的不同接口
            String servletPackage = web.packages;
            List<Class<?>> activityClass = ClassUtil.getClasses(servletPackage);
            if (Objects.nonNull(activityClass)) {
                for (Class<?> clazz : activityClass) {
                    WebHandleAnnotation annotation = clazz.getAnnotation(WebHandleAnnotation.class);

                    if (annotation != null) {
                        try {
                            String path = annotation.name();
                            Servlet servlet = (Servlet) clazz.newInstance();
                            servletContextHandler.addServlet(new ServletHolder(servlet), path);
                        } catch (InstantiationException | IllegalAccessException e) {
                            log.error("WebComponent Exception:", e);
                        }
                    }
                }
            }
            // 第三方或者系统的配置
            if (web.mapping != null && !web.mapping.isEmpty()) {
                for (ServletConfig config : web.mapping) {
                    Servlet servlet = (Servlet) ClassUtil.getClass(config.classPath).newInstance();
                    servletContextHandler.addServlet(new ServletHolder(servlet), config.url);

                    log.info("web服务器添加Servlet:" + config.classPath);
                }
            }

            ResourceHandler resourceHandler = new ResourceHandler();

            resourceHandler.setDirectoriesListed(web.isShowDirectory);
            resourceHandler.setWelcomeFiles(new String[]{web.welcomeFile});

            if (StringUtil.isNullOrEmpty(web.resourcePath)) {
                resourceHandler.setResourceBase(".");
                webAppContext.setResourceBase(".");
            } else {
                resourceHandler.setResourceBase(web.resourcePath);
                webAppContext.setResourceBase(web.resourcePath);
            }
            // resource 和 context 的添加顺序必须如此
            HandlerList handlerList = new HandlerList();
            handlerList.addHandler(resourceHandler);
            handlerList.addHandler(servletContextHandler);
            handlerList.addHandler(webAppContext);
            server.setHandler(handlerList);

            log.info("web服务器启动--端口" + web.port);
        } catch (Exception e) {
            log.error("WebComponent Initialize Exception:", e);
            return false;
        }

        return true;
    }

    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
