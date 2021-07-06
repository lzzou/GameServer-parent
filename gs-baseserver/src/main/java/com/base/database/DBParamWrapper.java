package com.base.database;

import java.util.HashMap;

/**
 * sql脚本参数的包装类
 */
public class DBParamWrapper {
    private HashMap<Integer, DBParameter> params = null;
    private int p = 0;

    public DBParamWrapper() {
        this.params = new HashMap<Integer, DBParameter>();
    }

    /**
     * 添加一个参数
     *
     * @param type 参数的类型
     * @param o    参数的值
     */
    public void put(int type, Object o) {
        params.put(++p, new DBParameter(type, o));
    }

    public HashMap<Integer, DBParameter> getParams() {
        return params;
    }
}
