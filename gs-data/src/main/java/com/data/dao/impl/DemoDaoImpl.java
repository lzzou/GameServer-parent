package com.data.dao.impl;

import com.base.database.BaseDao;
import com.base.database.DBParamWrapper;
import com.base.database.pool.DBHelper;
import com.data.dao.IDemoDao;
import com.data.entity.Demo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * @Author: zlz
 * @Date: 2020年09月03日 15:05
 * @Description:
 */
public class DemoDaoImpl extends BaseDao<Demo> implements IDemoDao {
    public DemoDaoImpl(DBHelper helper) {
        super(helper);
    }

    @Override
    protected Demo rsToEntity(ResultSet rs) throws SQLException {
        Demo demo = new Demo();
        demo.setId(rs.getLong("id"));
        demo.setName(rs.getString("name"));
        demo.setAge(rs.getInt("age"));
        demo.setRemark(rs.getString("remark"));
        return demo;
    }

    @Override
    public boolean add(Demo demo) {
        return false;
    }

    @Override
    public boolean update(Demo demo) {
        return false;
    }

    @Override
    public boolean delete(Demo demo) {
        return false;
    }

    @Override
    public boolean addOrUpdate(Demo demo) {
        return false;
    }

    @Override
    public Long getMaxId() {
        String sql = "select max(id) from demo;";
        return queryOneColumnDataOne(sql, null);
    }

    @Override
    public boolean deleteByKey(Object... ids) {
        return false;
    }

    @Override
    public Demo getByKey(Object... ids) {
        String sql = "select * from demo where `id`=?;";
        DBParamWrapper params = new DBParamWrapper();
        params.put(Types.BIGINT, ids[0]);
        Demo demo = query(sql, params);
        return demo;
    }

    @Override
    public List<Demo> listAll() {
        return null;
    }

    @Override
    public int[] addOrUpdateBatch(List<Demo> demos) {
        String sql = "insert into demo(`id`, `name`, `age`, `remark`) values(?, ?, ?, ?) on DUPLICATE KEY update `name`=?,`age`=?,`remark`=?;";
        int[] effectedRows = getDBHelper().sqlBatch(sql, demos, (statement, objects) -> {
            @SuppressWarnings("unchecked")
            List<Demo> demos1 = (List<Demo>) objects[0];
            for (Demo demo : demos1) {
                DBParamWrapper params = new DBParamWrapper();
                params.put(Types.BIGINT, demo.getId());
                params.put(Types.VARCHAR, demo.getName());
                params.put(Types.INTEGER, demo.getAge());
                params.put(Types.VARCHAR, demo.getRemark());
                params.put(Types.VARCHAR, demo.getName());
                params.put(Types.INTEGER, demo.getAge());
                params.put(Types.VARCHAR, demo.getRemark());
                statement = getDBHelper().prepareCommand(statement, params.getParams());
                statement.addBatch();
            }
            return statement.executeBatch();
        });
        return effectedRows;
    }

    @Override
    public int[] deleteBatch(List<Demo> t) {
        return new int[0];
    }
}
