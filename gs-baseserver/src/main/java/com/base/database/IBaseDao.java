package com.base.database;

import java.util.List;
import java.util.Map;

public interface IBaseDao<T> {

    /**
     * 添加
     *
     * @param t
     * @return
     */
    boolean add(T t);

    /**
     * 修改
     *
     * @param t
     * @return
     */
    boolean update(T t);

    /**
     * 根据脚本执行更新
     *
     * @param sql          更新脚本
     * @param paramWrapper 参数
     * @return
     */
    boolean update(String sql, DBParamWrapper paramWrapper);

    /**
     * 删除
     *
     * @param t
     * @return
     */
    boolean delete(T t);

    /**
     * 添加或修改缓存
     *
     * @param t
     * @return
     */
    boolean addOrUpdate(T t);

    /**
     * @param ids 对应数据库中主键
     * @return
     * @注意 参数传入的顺序对应脚本中的主键设置顺序
     */
    boolean deleteByKey(Object... ids);

    /**
     * 根据脚本执行查询操作，不带参数
     *
     * @param sql查询的脚本
     * @return
     */
    T query(String sql);

    /**
     * 根据脚本执行查询操作
     *
     * @param sql          查询的脚本
     * @param paramWrapper 参数
     * @return 返回查询结果对象
     */
    T query(String sql, DBParamWrapper paramWrapper);

    /**
     * 根据脚本执行查询操作,不带参数
     *
     * @param sql 查询的脚本
     * @return
     */
    List<T> queryList(String sql);

    /**
     * 根据脚本执行查询操作
     *
     * @param sql          sql 查询的脚本
     * @param paramWrapper 参数
     * @return 返回查询结果对象集合
     */
    List<T> queryList(String sql, DBParamWrapper paramWrapper);

    /**
     * @param ids 对应数据库中主键
     * @return
     * @注意 参数传入的顺序对应脚本中的主键设置顺序
     */
    T getByKey(Object... ids);

    /**
     * 根据脚本查询实体，返回key字段作为hash的Map
     *
     * @param sql    脚本
     * @param params 参数
     * @param key    列名，它的值作为哈希的key值<br>
     *               <strong><i>如果key只有一个的时候，返回的泛型(Key部分)可以是任意类型，但是key传多个进行的时候，
     *               返回的泛型(Key部分)只能是String<i><strong>
     * @return
     */
    <K> Map<K, T> queryMap(String sql, DBParamWrapper params, Object... key);

    /**
     * 查询所有
     */
    List<T> listAll();

    /**
     * 批量添加或修改实体
     *
     * @param t
     * @return
     */
    int[] addOrUpdateBatch(List<T> t);

    /**
     * 批量删除实体
     *
     * @param t
     * @return
     */
    int[] deleteBatch(List<T> t);

    public Long getMaxId();

    /**
     * 查询单列的信息
     *
     * @param sql
     * @param paramWrapper
     * @return
     */
    public <S> List<S> queryOneColumnData(String sql, DBParamWrapper paramWrapper);

    /**
     * 查询单列单条信息
     *
     * @param sql
     * @param paramWrapper
     * @return
     */
    public <S> S queryOneColumnDataOne(String sql, DBParamWrapper paramWrapper);

    /**
     * 执行sql语句
     *
     * @param sql
     * @param params
     * @return
     */
    public boolean execute(String sql, DBParamWrapper params);
}
