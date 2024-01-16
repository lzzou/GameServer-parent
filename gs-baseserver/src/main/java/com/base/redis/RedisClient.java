package com.base.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * redis缓存代理
 */
@Slf4j
public class RedisClient {

    private JedisPool pool = null;

    /**
     * 数据过期时间（三天）
     */
    private int expiredTime = 3600 * 72;

    /**
     * 控制一个pool最多有多少个状态为idle的jedis实例
     */
    private int maxIdle = 5;

    /**
     * 控制一个pool最少有多少个状态为idle的jedis实例
     */
    private int minIdle = 2;

    /**
     * 连接最大的等待时间，如果超时，抛出异常
     */
    private int maxWaitTime = 1000 * 10;

    /**
     * maxActive：控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；如果赋值为-1，则表示不限制
     */
    private int maxTotal = 1000;

    public int getExpireTime() {
        return expiredTime;
    }

    /**
     * 初始化redis连接
     *
     * @param host
     * @param port
     * @param expiredTime 过期时间（小时）
     * @param password    密码
     * @param dbID        子库ID（16个 0-15）
     * @return
     */
    public boolean init(String host, int port, int expiredTime, String password, int dbID) {
        JedisPoolConfig config = new JedisPoolConfig();
        this.expiredTime = expiredTime;

        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitTime);
        config.setMinIdle(minIdle);
        config.setMaxTotal(maxTotal);

        pool = new JedisPool(config, host, port == -1 ? 6379 : port, expiredTime, password, dbID);

        Jedis jedis = null;

        try {
            jedis = pool.getResource();
        } catch (Exception e) {
            log.error("redis client init Exception:" + host + ":" + port, e);
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return true;
    }

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     * 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以秒为单位，返回 key 的剩余生存时间。
     *
     * @param key
     * @return
     */
    public int ttl(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            long val = jedis.ttl(key);
            return (int) val;
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return -1;
    }

    /**
     * 设置失效时间
     * 对一个 key 执行 INCR 命令，对一个列表进行 LPUSH 命令，或者对一个哈希表执行 HSET 命令，这类操作都不会修改 key 本身的生存时间。
     * 另一方面，如果使用 RENAME 对一个 key 进行改名，那么改名后的 key 的生存时间和改名前一样。
     *
     * @param key
     * @param second
     */
    public boolean expire(String key, int second) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            long val = jedis.expire(key, second);
            return val == 1;
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**************************************** set *******************************************/

    /**
     * hash表 设置值
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hset(String key, String field, Object value) {
        if (value == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            // 0 表示更新，1表示新增
            byte[] data = RedisSerializeUtil.serialize(value);
            if (data != null) {
                long r = jedis.hset(key.getBytes(), field.getBytes(), data);
                expire(key, this.expiredTime);
                return r == 0 || r == 1;
            } else {
                log.error("redis set error:" + key + "-" + field + ",Serialize Exception Null.");
            }
        } catch (Exception e) {
            log.error("redis set error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * hash表 设置值
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hset(String key, int field, Object value) {
        return hset(key, String.valueOf(field), value);
    }

    /**
     * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。
     * 若域 field 已经存在，该操作无效。
     * 如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hsetex(String key, String field, Object value) {
        if (value == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            // 0 表示更新，1表示新增
            byte[] data = RedisSerializeUtil.serialize(value);
            if (data != null) {
                long r = jedis.hsetnx(key.getBytes(), field.getBytes(), data);
                return r == 0 || r == 1;
            } else {
                log.error("redis set error:" + key + "-" + field + ",Serialize Exception Null.");
            }
        } catch (Exception e) {
            log.error("redis set error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 缓存的是对象，所以通过命令行查看是数据会有部分类说明的乱码
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        if (value == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String r = jedis.set(key.getBytes(), RedisSerializeUtil.serialize(value));

            return r.equals("OK");
        } catch (Exception e) {
            log.error("redis set error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 设置带单独失效时间的key值
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setex(String key, Object value) {
        return setex(key, value, this.expiredTime);
    }

    /**
     * 设置带单独失效时间的key值
     *
     * @param key
     * @param value
     * @param expiredTime 失效时间（秒）
     * @return
     */
    public boolean setex(String key, Object value, int expiredTime) {
        if (value == null) {
            return false;
        }

        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String r = jedis.setex(key.getBytes(), expiredTime, RedisSerializeUtil.serialize(value));
            return r.equals("OK");
        } catch (Exception e) {
            log.error("redis set error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 将 key 的值设为 value ，当且仅当 key 不存在。
     * 若给定的 key 已经存在，则 SETNX 不做任何动作。
     *
     * @param key
     * @param value
     * @return
     */
    public int setnx(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            long val = jedis.setnx(key, "" + value);
            return (int) val;
        } catch (Exception e) {
            log.error("redis error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return -1;
    }

    /**************************************** get *******************************************/

    /**
     * 取得一个key的值
     *
     * @param key
     * @return
     */
    public <T> T get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            byte[] data = jedis.get(key.getBytes());
            if (data != null && data.length > 0) {
                T object = RedisSerializeUtil.unserialize(data);
                expire(key, this.expiredTime);
                return object;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("redis get error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return null;
    }

    /**
     * 取得一个key的值
     *
     * @param key
     * @return
     */
    public String getWithOutUnserialize(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            log.error("redis get error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return null;
    }


    /**
     * 获取值 hash表
     *
     * @param key
     * @param field
     * @return
     */
    public <T> T hget(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            // 0 表示更新，1表示新增
            byte[] val = jedis.hget(key.getBytes(), field.getBytes());
            T object = RedisSerializeUtil.unserialize(val);

            expire(key, this.expiredTime);

            return object;
        } catch (Exception e) {
            log.error("redis set error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 获取值 hash表
     *
     * @param key
     * @param field
     * @return
     */
    public <T> T hget(String key, int field) {
        return hget(key, String.valueOf(field));
    }

    /**
     * 返回指定key的所有 hash value。
     *
     * @param key
     * @return
     */
    public <T> List<T> hValues(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            List<byte[]> val = jedis.hvals(key.getBytes());
            expire(key, this.expiredTime);

            List<T> all = new ArrayList<>();

            val.forEach(p -> {
                T object = RedisSerializeUtil.unserialize(p);
                if (object != null) {
                    all.add(object);
                }
            });

            return all;
        } catch (Exception e) {
            log.error("redis hvals error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**************************************** del *******************************************/

    /**
     * 删除单个
     *
     * @param key
     * @param field
     */
    public void hdel(String key, String field) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.hdel(key.getBytes(), field.getBytes());
        } catch (Exception e) {
            log.error("redis set error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除单个
     *
     * @param key
     * @param field
     */
    public void hdel(String key, int field) {
        hdel(key, String.valueOf(field));
    }

    /**
     * 删除单个key
     *
     * @param key
     */
    public void delete(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.del(key.getBytes());
        } catch (Exception e) {
            log.error("redis delete error:" + key, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除单个key
     *
     * @param key
     */
    public void delete(int key) {
        delete(key + "");
    }

    /************************************** 列表set *****************************************/

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合
     *
     * @param key
     * @param members
     */
    public void sadd(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.sadd(key, members);
        } catch (Exception e) {
            log.error("redis get keys error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 判断集合 key是否包含member元素成员。
     *
     * @param key
     * @param member
     * @return
     */
    public boolean scontain(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, member);
        } catch (Exception e) {
            log.error("redis get keys error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return false;
    }

    /**
     * 返回集合 key 中的所有成员。不存在的 key 被视为空集合。
     *
     * @param key
     * @return
     */
    public Set<String> smembers(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.smembers(key);
        } catch (Exception e) {
            log.error("redis get keys error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return null;
    }

    /************************************** global *****************************************/

    /**
     * 将 key 中储存的数字值增一。如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。
     * key对应的value值必须是数字类型。本操作的值限制在 64 位(bit)有符号数字表示之内。
     *
     * @param key
     * @return
     */
    public long incr(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.incr(key);
        } catch (Exception e) {
            log.error("redis get keys error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return -1;
    }

    /**
     * 重置原子操作的值
     *
     * @param key
     * @param value
     * @return
     */
    public String getSet(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.getSet(key, value);
        } catch (Exception e) {
            log.error("redis get keys error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return "";
    }

    /**
     * 将 key 所储存的值加上增量 increment 。
     * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。
     *
     * @param key
     * @param val
     * @return
     */
    public long incrBy(String key, int val) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.incrBy(key, val);
        } catch (Exception e) {
            log.error("redis get keys error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return -1;
    }

    /************************************** function *****************************************/

    /**
     * 清除所有数据库的数据
     */
    public void flushAll() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.flushAll();
        } catch (Exception e) {
            log.error("redis flush all error.", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 清除当前数据库的数据
     */
    public void flushDB() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.flushDB();
        } catch (Exception e) {
            log.error("redis flush db error.", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void save() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.save();
        } catch (Exception e) {
            log.error("redis save error.", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void saveAsync() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.bgsave();
        } catch (Exception e) {
            log.error("redis save async error.", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 切换到指定的数据库，数据库索引号 index 用数字值指定，以 0 作为起始索引值。默认使用 0 号数据库。
     *
     * @param id
     */
    public void select(int id) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.select(id);
        } catch (Exception e) {
            log.error("redis select db error:" + id, e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 请求服务器关闭与当前客户端的连接。
     * 一旦所有等待中的回复(如果有的话)顺利写入到客户端，连接就会被关闭。
     */
    public void quit() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.close();
        } catch (Exception e) {
            log.error("redis quit error:", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * @return
     */
    public String getRedisInfo() {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.keys("");
            return jedis.info();
        } catch (Exception e) {
            log.error("redis get info error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 根据字符串取得匹配的key（慎用）
     *
     * @param pattern
     * @return
     */
    public List<String> keys(String pattern) {
        List<String> list = new ArrayList<String>();
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> sets = jedis.keys(pattern);
            list.addAll(sets);
        } catch (Exception e) {
            log.error("redis get keys error", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return list;
    }

    /**
     * 订阅
     *
     * @param sub
     * @param channels
     */
    public void subscribe(JedisPubSub sub, String... channels) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.subscribe(sub, channels);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 发布
     *
     * @param channel
     * @param message
     */
    public void publish(String channel, String message) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.publish(channel, message);
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
