package com.base.type;

/**
 * @author zlz
 */
public enum ErrorCodeType {
    /**
     * 成功
     */
    Success(0),

    /**
     * 错误
     */
    Error(1),

    /**
     * 登录查询用户异常
     */
    User_Not_Exist(2),

    /**
     * 数据库错误
     */
    DB_Error(3),

    /**
     * 服务器停服维护，网络会断开
     */
    Server_Close(4),

    /**
     * 服务器内部错误
     */
    Server_InternalError(10),
    ;

    private int value;

    private ErrorCodeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ErrorCodeType parse(int type) {
        for (ErrorCodeType t : ErrorCodeType.values()) {
            if (type == t.value) {
                return t;
            }
        }

        return Error;
    }

}
