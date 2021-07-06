package com.zlz.component;

import com.base.component.AbstractComponent;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 脚本组件
 */
public class ScriptComponent extends AbstractComponent {
    private static final Logger logger = LoggerFactory.getLogger(ScriptComponent.class);

    private static GroovyShell shell;

    @Override
    public boolean initialize() {
        shell = new GroovyShell();
        return true;
    }

    public static String evaluate(String str) {
        try {
            if (Objects.isNull(shell)) {
                return "ScriptComponent is disable.";
            } else {
                Object obj = shell.evaluate(str);
                if (obj != null) {
                    return String.valueOf(obj);
                } else {
                    return "void method return.";
                }
            }
        } catch (Exception e) {
            logger.error("", e);
            return e.getMessage();
        }
    }

    @Override
    public boolean reload() {
        return initialize();
    }

    @Override
    public void stop() {
        logger.error("ScriptComponent stop...");
        shell.resetLoadedClasses();
        shell = null;
    }
}
