package com.base.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 保存处理handler
 */
public class RedisSaveHandler<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSaveHandler.class);

    /**
     * 更新key
     */
    private Set<String> keySet = new HashSet<>();

    /**
     * 更新key
     */
    private HashMap<String, Set<String>> mapSet = new HashMap<>();

    public interface Hanlder<E> {
        public void exec(List<E> list, RedisClient client);
    }

    private Hanlder<E> handler;

    public RedisSaveHandler(Hanlder<E> handler) {
        this.handler = handler;
    }

    public void addKey(String key) {
        synchronized (keySet) {
            keySet.add(key);
        }
    }

    public void addMapKey(String key, String subKey) {
        synchronized (mapSet) {
            Set<String> set = mapSet.get(key);
            if (set == null) {
                set = new HashSet<>();
                mapSet.put(key, set);
            }

            set.add(subKey);
        }
    }

    public HashMap<String, Set<String>> getMapKey() {
        HashMap<String, Set<String>> map = new HashMap<>();
        synchronized (mapSet) {
            map.putAll(mapSet);
        }

        return map;
    }

    public List<String> getKetList() {
        List<String> list = new ArrayList<>();
        synchronized (keySet) {
            list.addAll(keySet);
        }
        return list;
    }

    public void save(RedisClient client) {
        update(client);
    }

    private void update(RedisClient client) {
        // 保证数据的保存,在极限情况下，数据也不会丢失。
        if (!keySet.isEmpty()) {
            List<E> list = new ArrayList<>();
            List<String> keys = new ArrayList<>();
            synchronized (keySet) {
                keys.addAll(keySet);
                keySet.clear();
            }

            for (String key : keys) {
                E info = client.get(key);
                if (info != null) {
                    list.add(info);
                } else {
                    LOGGER.error("udpate:redis data is not exist.key:" + key);
                }
            }

            try {
                handler.exec(list, client);
            } catch (Exception e) {
                synchronized (keySet) {
                    keySet.addAll(keys);
                }
                LOGGER.error("RedisSaveHandler Save Exception:", e);
            } finally {
                keys.clear();
            }
        }

        if (!mapSet.isEmpty()) {
            List<E> list = new ArrayList<>();
            HashMap<String, Set<String>> keys = new HashMap<>();
            synchronized (mapSet) {
                keys.putAll(mapSet);
                mapSet.clear();
            }

            for (Entry<String, Set<String>> entry : keys.entrySet()) {
                for (String subKey : entry.getValue()) {
                    E temp = client.hget(entry.getKey(), subKey);
                    if (temp != null) {
                        list.add(temp);
                    } else {
                        // 在更新之前该记录已经被删除
                        LOGGER.error("udpate:redis data is not exist.key:" + entry.getKey() + "," + subKey);
                    }
                }
            }

            try {
                handler.exec(list, client);
            } catch (Exception e) {
                synchronized (mapSet) {
                    for (Entry<String, Set<String>> entry : keys.entrySet()) {
                        if (mapSet.containsKey(entry.getKey())) {
                            mapSet.get(entry.getKey()).addAll(entry.getValue());
                        } else {
                            mapSet.put(entry.getKey(), entry.getValue());
                        }
                    }
                }

                LOGGER.error("Exception:", e);
            } finally {
                keys.clear();
            }
        }
    }

}
