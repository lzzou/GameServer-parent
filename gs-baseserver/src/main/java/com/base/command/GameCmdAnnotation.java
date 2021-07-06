package com.base.command;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameCmdAnnotation {
    /**
     * 命令类型
     */
    short type();

    /**
     * 命令描述
     */
    String desc() default "";
}
