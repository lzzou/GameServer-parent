package com.base.database;

import java.sql.PreparedStatement;

/**
 * db执行statement接口
 */
public interface DataExecutor<T> {
    /**
     * 该方法为泛型方法
     *
     * @param statement 需要执行的statement
     * @param objects   传入的参数集合
     * @return
     * @throws Exception
     */
    T execute(PreparedStatement statement, Object... objects) throws Exception;
}
