package com.base.component;

import com.base.command.ICode;
import com.base.command.ICommand;
import com.game.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 命令管理器组件
 *
 * @author zlz
 */
@Slf4j
public abstract class AbstractCommandComponent extends AbstractComponent {

    /**
     * 缓存命令对象
     **/
    protected final Map<Short, ICommand> cmdCache = new HashMap<>();

    /**
     * 取得所在包名称
     *
     * @return 包名
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
                                log.error("cmd code error,code:{},old:{},new:{}.", cmd.code(), command.getClass().getName(),
                                        clazz.getName());
                                return false;
                            }
                            cmdCache.put(cmd.code(), (ICommand) clazz.newInstance());
                            // continue;
                        }
                    } catch (Exception e) {
                        log.error("load command fail, command name : " + clazz.getName(), e);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            log.error("命令管理器解析错误", e);
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
     * @param code 命令code
     * @return 命令实体
     */
    public ICommand getCommand(short code) {
        return cmdCache.get(code);
    }

}
