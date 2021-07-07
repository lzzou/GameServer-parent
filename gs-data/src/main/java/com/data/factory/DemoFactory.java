package com.data.factory;

import com.data.dao.IDemoDao;
import com.data.dao.impl.DemoDaoImpl;

/**
 * @Author: zlz
 * @Date: 2020年09月03日 15:03
 * @Description:
 */
public class DemoFactory implements IDaoFactory {
    private static final IDemoDao dao = new DemoDaoImpl(dbHelper);

    public static IDemoDao getDao() {
        return dao;
    }
}