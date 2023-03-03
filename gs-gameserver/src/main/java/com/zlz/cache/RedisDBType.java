package com.zlz.cache;

/**
 * redis枚举
 *
 * @author zlz
 */
public enum RedisDBType {
    /**
     * 通用
     */
    COMMON(7),
    ;
    private int value;

    RedisDBType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RedisDBType parse(int value) {
        RedisDBType[] states = RedisDBType.values();

        for (RedisDBType s : states) {
            if (s.getValue() == value) {
                return s;
            }
        }

        return COMMON;
    }
}
