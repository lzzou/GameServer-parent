package com.base.component;

import com.base.command.ICode;
import com.base.command.ICommand;
import com.zlz.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 命令管理器组件
 */
public abstract class AbstractCommandComponent extends AbstractComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(AbstractCommandComponent.class);

    /**
     * 缓存命令对象
     **/
    protected final Map<Short, ICommand> cmdCache = new HashMap<>();

    /**
     * 取得所在包名称
     *
     * @return
     */
    public abstract String getCommandPacketName();

    @Override
    public boolean reload() {
        return true;
    }

    @Override
    public boolean initialize() {
        try {
            List<Class<?>> allClasses = ClassUtil.getClasses(getCommandPacketName());

            cmdCache.clear();

            if (Objects.nonNull(allClasses) && !allClasses.isEmpty()) {
                for (Class<?> clazz : allClasses) {
                    try {
                        ICode cmd = clazz.getAnnotation(ICode.class);
                        if (cmd != null) {
                            if (cmdCache.get(cmd.code()) != null) {
                                ICommand command = cmdCache.get(cmd.code());
                                LOGGER.error("cmd code error,code:{},old:{},new:{}.", cmd.code(), command.getClass().getName(),
                                        clazz.getName());
                                return false;
                            }
                            cmdCache.put((short) cmd.code(), (ICommand) clazz.newInstance());
                            // continue;
                        }
                    } catch (Exception e) {
                        LOGGER.error("load command fail, command name : " + clazz.getName(), e);
                        e.printStackTrace();
                    }
                }
            }

            return true;
        } catch (Exception e) {
            LOGGER.error("命令管理器解析错误", e);
            return false;
        }
    }

    /**
     * 加载命令集合
     */
    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void stop() {
        cmdCache.clear();
    }

    /**
     * 缓存中获取命令
     *
     * @param code
     * @return
     */
    public ICommand getCommand(short code) {
        return cmdCache.get(code);
    }

}
