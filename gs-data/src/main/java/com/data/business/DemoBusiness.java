package com.data.business;

import com.data.entity.Demo;
import com.data.factory.DemoFactory;

import java.util.List;

/**
 * @Author: longzhang_zou
 * @Date: 2020年09月03日 15:01
 * @Description:
 */
public class DemoBusiness {
    public static boolean addOrUpdateDemoBatch(List<Demo> list) {
        int[] result = DemoFactory.getDao().addOrUpdateBatch(list);
        if (result != null) {
            return true;
        }
        return false;
    }

    public static Demo getDemo(Long id) {
        return DemoFactory.getDao().getByKey(id);
    }

    public static Long getMaxId() {
        return DemoFactory.getDao().getMaxId();

    }
}
