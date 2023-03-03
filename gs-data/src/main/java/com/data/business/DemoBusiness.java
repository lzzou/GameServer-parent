package com.data.business;

import com.data.entity.Demo;
import com.data.factory.DemoFactory;

import java.util.List;

/**
 * @author zlz
 */
public class DemoBusiness {
    public static boolean addOrUpdateDemoBatch(List<Demo> list) {
        int[] result = DemoFactory.getDao().addOrUpdateBatch(list);
        return result != null;
    }

    public static Demo getDemo(Long id) {
        return DemoFactory.getDao().getByKey(id);
    }

    public static Long getMaxId() {
        return DemoFactory.getDao().getMaxId();

    }
}
