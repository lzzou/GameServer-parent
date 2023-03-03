package com.data.factory;

import com.base.database.DBPoolComponent;
import com.base.database.pool.DBHelper;

public interface IDaoFactory {
    /**
     * 主库
     */
    DBHelper mainHelper = DBPoolComponent.getDBHelper(DatabaseType.DB_MAIN);

}
