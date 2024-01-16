package com.base.component;

import com.game.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * 组件管理器。使用单例模式。
 *
 * @author zlz
 */
@Slf4j
public class ComponentManager {

    public volatile static ComponentManager INSTANCE;

    /**
     * 类加载器
     */
    private final ClassLoader loader;

    /**
     * 组件集合 (添加的顺序保持不变)
     */
    private static final Map<String, IComponent> COMPONENTS = new LinkedHashMap<>();

    private ComponentManager() {
        loader = Thread.currentThread().getContextClassLoader();
    }

    public static ComponentManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ComponentManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ComponentManager();
                }
            }
        }
        return INSTANCE;
    }

    public List<IComponent> getAllComponent() {
        return new ArrayList<>(COMPONENTS.values());
    }

    public boolean addComponent(String className) {
        try {
            Class<?> cls = loader.loadClass(className);
            IComponent component = (IComponent) cls.newInstance();

            long spendTime = System.currentTimeMillis();

            if (component.initialize()) {
                COMPONENTS.put(className, component);

                if ((spendTime = System.currentTimeMillis() - spendTime) > 1000) {
                    log.warn("Component {} init spend time {} 毫秒!!", className, spendTime);
                } else {
                    log.info("Component {} init spend time {} 毫秒!!", className, spendTime);
                }
                return true;
            } else {
                log.error("{} getName() is null or empty。", className);
                return false;
            }
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    public boolean addComponent(Class<?> clazz) {
        try {
            IComponent component = (IComponent) clazz.newInstance();

            long spendTime = System.currentTimeMillis();

            if (component.initialize()) {
                COMPONENTS.put(clazz.getName(), component);

                if ((spendTime = System.currentTimeMillis() - spendTime) > 1000) {
                    log.warn("Component {} init spend time {} 毫秒!!", clazz.getName(), spendTime);
                } else {
                    log.info("Component {} init spend time {} 毫秒!!", clazz.getName(), spendTime);
                }
                return true;
            } else {
                log.error("{} getName() is null or empty。", clazz.getName());
                return false;
            }
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    public boolean loadComponent(String packageName) {
        try {
            Enumeration<URL> resources =
                    Thread.currentThread().getContextClassLoader().getResources(packageName.replace('.', '/'));
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if (url != null) {
                    // 拿到文件的协议
                    String protocol = url.getProtocol();
                    // 如果是file
                    if ("file".equals(protocol)) {
                        // 取到文件的路径
                        String packagePath = url.getPath();
                        if (!loadClazz(packageName, packagePath)) {
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return true;
    }

    private boolean loadClazz(String packageName, String packagePath) throws ClassNotFoundException {
        File[] files = new File(packagePath)
                .listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
        // 如果files不为空的话，对它进行迭代
        if (Objects.nonNull(files)) {
            for (File file : files) {
                // 取到文件名
                String fileName = file.getName();
                //如果取到的是文件
                if (file.isFile()) {
                    // 取到对应的类名,这里的类名是权限定名
                    String className = fileName.substring(0, fileName.indexOf("."));
                    if (packageName != null && !packageName.isEmpty()) {
                        className = packageName + "." + className;
                    }
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        return addComponent(clazz);
                    }
                } else {
                    String subPackagePath = ClassUtil.getSubPackagePath(packagePath, fileName);
                    String subPackageName = ClassUtil.getSubPackageName(packageName, fileName);
                    loadClazz(subPackageName, subPackagePath);
                }
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends IComponent> T getComponent(Class<T> t) {
        return (T) COMPONENTS.get(t.getName());
    }

    /**
     * 启动组件管理器。
     *
     * @return 启动结果
     */
    public boolean start() {
        for (Entry<String, IComponent> entry : COMPONENTS.entrySet()) {
            IComponent component = entry.getValue();

            if (!component.start()) {
                log.error("****** Component:[{}] has started failed.******************", entry.getKey());
                return false;
            } else {
                log.info("****** Component:[{}] has started successfully.******************", entry.getKey());
            }
        }

        log.info("****** All Component has started successfully.******************");

        return true;
    }

    /**
     * 关闭组件管理器。
     */
    public void stop() {
        List<IComponent> iComponents = new ArrayList<>(COMPONENTS.values());
        Collections.reverse(iComponents);

        iComponents.forEach(p -> {
            log.warn("****** Component:[{}] is ready to stop.******************", p.getClass().getName());
            p.stop();
            log.warn("****** Component:[{}] has stoped.******************", p.getClass().getName());
        });
        COMPONENTS.clear();

        log.error("All component has stoped.");
    }

    /**
     * 重新加载所有组件
     *
     * @param excludes 排除的组件
     * @return 加载结果
     */
    public boolean reload(List<String> excludes) {
        for (Entry<String, IComponent> entry : COMPONENTS.entrySet()) {
            if (excludes != null && excludes.contains(entry.getKey())) {
                continue;
            }
            IComponent module = entry.getValue();
            if (!module.reload()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 加载指定组件
     *
     * @param componentName componentName
     * @return 加载结果
     */
    public boolean reloadSingle(String componentName) {
        IComponent module = COMPONENTS.get(componentName);
        if (module != null) {
            module.reload();
        }

        return true;
    }
}
