package com.base.web;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * WebComponent相应接口注释
 *
 * @author zlz
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface WebHandleAnnotation {
    String name();

    String description();

    boolean state() default false;
}
