package com.base.database;

import com.base.component.AbstractComponent;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * 引入mybatis组件
 *
 * @author zlz
 */
public class MyBatisComponent extends AbstractComponent {


    @Override
    public boolean initialize() {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);



        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public void stop() {

    }
}
