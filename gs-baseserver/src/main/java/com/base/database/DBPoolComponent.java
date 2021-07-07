package com.base.database;

import com.base.component.AbstractComponent;
import com.base.component.Component;
import com.base.component.GlobalConfigComponent;
import com.base.config.DatabaseConfig;
import com.base.config.DatabaseConfig.DruidPoolConfig;
import com.base.database.pool.DBHelper;
import com.base.database.pool.IDBPool;
import com.base.database.pool.druid.DruidPool;
import com.zlz.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库连接池管理类
 */
@Component
@Slf4j
public final class DBPoolComponent extends AbstractComponent {

    protected static volatile boolean isStop = false;

    /**
     * pools保存所有的连接池的信息 key对应是这个连接池的名字
     */
    private static Map<String, IDBPool> pools = new ConcurrentHashMap<String, IDBPool>();

    private static Map<String, DBHelper> dbHelpers = new ConcurrentHashMap<String, DBHelper>();

    /**
     * 启动连接池
     */
    private boolean startupPool() {
        synchronized (pools) {
            Set<Entry<String, IDBPool>> entries = pools.entrySet();

            for (Entry<String, IDBPool> entry : entries) {
                if (!entry.getValue().startup()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 重新启动连接池
     */
    public void restartup() {
        synchronized (pools) {
            Set<Entry<String, IDBPool>> entries = pools.entrySet();
            for (Entry<String, IDBPool> entry : entries) {
                if (!entry.getValue().validConn()) {
                    entry.getValue().startup();
                }
            }
        }
    }

    /**
     * 添加连接池，并启动连接池
     *
     * @param dbName
     * @param pool
     */
    public void addDBPool(String dbName, IDBPool pool) {
        synchronized (pools) {
            IDBPool temp = pools.get(dbName);
            if (temp != null) {
                throw new RuntimeException("Already exit the dbPool!");
            } else {
                pools.put(dbName, pool);
            }
        }
    }

    /**
     * 添加Hepler
     *
     * @param dbName
     * @param dbHelper
     */
    public void addDBHelper(String dbName, DBHelper dbHelper) {
        synchronized (dbHelpers) {
            DBHelper temp = dbHelpers.get(dbName);
            if (temp != null) {
                throw new RuntimeException("Already exit the dbHelper!");
            } else {
                dbHelpers.put(dbName, dbHelper);
            }
        }
    }

    /**
     * 根据XML解析数据库连接池
     *
     * @param element 传进来xml的database节点
     */
    public boolean initWithDbXML(DatabaseConfig element) {
        try {
            if (element.druidPools != null && !element.druidPools.isEmpty()) {
                for (DruidPoolConfig object : element.druidPools) {
                    DruidPool pool = new DruidPool(object);
                    DBHelper dbHelper = new DBHelper(pool);
                    if (StringUtil.isNullOrEmpty(object.area)) {
                        addDBPool(object.name, pool);
                        addDBHelper(object.name, dbHelper);
                    } else {
                        addDBPool(object.area + "_" + object.name, pool);
                        addDBHelper(object.area + "_" + object.name, dbHelper);
                    }
                    log.info("初始化 druid db pool：" + object.name);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("初始化数据库配置文件出错", e);
        }
        return false;

    }

    /**
     * 区域_名称的组合key
     *
     * @param key
     * @return
     */
    public static DBHelper getDBHelper(String key) {
        return dbHelpers.get(key);
    }

    /**
     * 区域 和名称组合的DBHelper
     *
     * @param area
     * @param dbName
     * @return
     */
    public static DBHelper getDBHelper(String area, String dbName) {
        return dbHelpers.get(area + "_" + dbName);
    }

    public static Map<String, DBHelper> getAllHelper() {
        return dbHelpers;
    }

    @Override
    public boolean initialize() {
        boolean result = initWithDbXML(GlobalConfigComponent.getConfig().database);
        startupPool();
        return result;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void stop() {
        isStop = true;

        synchronized (pools) {
            Set<Entry<String, IDBPool>> entries = pools.entrySet();
            for (Entry<String, IDBPool> entry : entries) {
                entry.getValue().shutdown();
            }
        }
    }

}
