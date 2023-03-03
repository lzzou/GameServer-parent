package com.zlz.component;

import com.base.component.AbstractComponent;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 脚本组件
 */
@Slf4j
public class ScriptComponent extends AbstractComponent {

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
            log.error("", e);
            return e.getMessage();
        }
    }

    @Override
    public boolean reload() {
        return initialize();
    }

    @Override
    public void stop() {
        log.error("ScriptComponent stop...");
        shell.resetLoadedClasses();
        shell = null;
    }
}
