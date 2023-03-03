package com.data.factory;

import com.data.dao.IDemoDao;
import com.data.dao.impl.DemoDaoImpl;

/**
 * @author zlz
 */
public class DemoFactory implements IDaoFactory {
    private static final IDemoDao dao = new DemoDaoImpl(mainHelper);

    public static IDemoDao getDao() {
        return dao;
    }
}