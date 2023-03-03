package com.base.database;

import com.zlz.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * dao层数据库执行情况观察对象
 */
@Slf4j
public class DBWatcher {
    private long first = 0;
    private long second = 0;
    private long end = 0;

    public DBWatcher() {
        first = TimeUtil.getSysCurTimeMillis();
    }

    /**
     * 记录获取数据库连接点
     */
    public void watchGetConnector() {
        second = TimeUtil.getSysCurTimeMillis();
    }

    /**
     * 记录数据脚本执行点
     */
    public void watchSqlExec() {
        end = TimeUtil.getSysCurTimeMillis();
    }

    /**
     * 查看执行情况
     *
     * @param procName 此次执行事务的别名
     */
    public void keeyRecords(String procName) {
        long spendTime = end - first;
        if (spendTime > 1000) {
            log.error(String.format(
                    "执行语句%s花耗时间总时间 超过:%sms,获取连接：%sms,执行sql:%sms", procName,
                    spendTime, second - first, end - second));
        }
    }
}
