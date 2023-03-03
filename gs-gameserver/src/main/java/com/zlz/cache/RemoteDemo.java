package com.zlz.cache;

import com.base.redis.RedisCommon;
import com.base.redis.RedisSaveHandler;
import com.base.rmi.IRemoteCode;
import com.data.business.DemoBusiness;
import com.data.entity.Demo;
import com.zlz.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author zlz
 */
@IRemoteCode(code = CacheType.DEMO, desc = "Demo")
@Slf4j
public class RemoteDemo extends RedisCommon {

    private static final byte DEMO = 1;

    private static final String KEY_DEMO = "zlz:demo:%s";

    private static final String KEY_DEMO_MAX_ID = "maxid:demo";

    public RemoteDemo() {
        super(RedisDBType.COMMON.getValue());

        RedisSaveHandler<Demo> demoHandler = new RedisSaveHandler<>(
                (list, client) -> DemoBusiness.addOrUpdateDemoBatch(list));

        handlerMap.put(DEMO, demoHandler);
    }

    @Override
    public synchronized void resetTableMaxId() {
        Long maxValue = getMaxId();
        String strValue = client.getWithOutUnserialize(KEY_DEMO_MAX_ID);
        if (!StringUtil.isNullOrEmpty(strValue) && !"null".equals(strValue)) {
            Long value = Long.parseLong(strValue);
            Long max = Math.max(value, maxValue);
            // 如果缓存的比数据库的小，重置为数据库的值
            if (value < max) {
                client.getSet(KEY_DEMO_MAX_ID, String.valueOf(max));
            }
        } else {
            client.getSet(KEY_DEMO_MAX_ID, String.valueOf(maxValue));
        }
    }

    @Override
    public Long getMaxId() {
        Long maxId = DemoBusiness.getMaxId();
        if (maxId == null) {
            return 1L;
        }
        return maxId;
    }

    /**
     * 增
     */
    public boolean addDemo(final Demo demo) {
        long id = client.incr(KEY_DEMO_MAX_ID);
        demo.setId(id);

        String key = String.format(KEY_DEMO, id);

        client.setex(key, demo);
        addKey(DEMO, key);
        return true;
    }

    public Demo getDemo(Long id) {
        String key = String.format(KEY_DEMO, id);
        Demo demo = client.get(key);
        if (Objects.isNull(demo)) {
            demo = DemoBusiness.getDemo(id);
            if (Objects.nonNull(demo)) {
                client.setex(key, demo);
            }
        }
        return demo;
    }
}
