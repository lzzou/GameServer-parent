package com.base.database;

import com.base.component.AbstractComponent;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 引入mybatis组件
 *
 * @Author: zlz
 * @Date: 2021/7/7 14:54
 */
public class MybatisComponent extends AbstractComponent {


    @Override
    public boolean initialize() {
        return false;
    }

    @Override
    public void stop() {

    }
}
