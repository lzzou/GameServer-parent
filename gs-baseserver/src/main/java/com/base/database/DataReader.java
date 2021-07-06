package com.base.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * db查询结果集数据读取接口
 */
public interface DataReader<T> {
    /**
     * 从查询结果集中读取一条记录并返回，本接口为泛型接口，如需读取的数据无明显类型特征（如抽取不规则字段集等），可直接使用Object类型进行读取
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    T readData(ResultSet rs, Object... objects) throws Exception;
}
