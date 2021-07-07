package com.zlz.web;

import com.base.web.BaseHandlerServlet;
import com.base.web.WebHandleAnnotation;
import com.data.entity.Demo;
import com.zlz.cache.CacheType;
import com.zlz.cache.RemoteDemo;
import com.zlz.component.RemoteCacheComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zlz
 * @Date: 2020年09月03日 10:51
 * @Description:
 */
@WebHandleAnnotation(name = "/demo", description = "demo")
public class DemoController extends BaseHandlerServlet {

    @Override
    public String execute(String json) {
        Demo demo = gson.fromJson(json, Demo.class);

        RemoteDemo remoteDemo = RemoteCacheComponent.getModule(CacheType.Demo);
        // 校验 {"name":"呼吸","age":18,"remark":"备注"}
        // 入库
        boolean b = remoteDemo.addDemo(demo);
        logger.info("缓存结果:{}", b);

        Demo dbDemo = remoteDemo.getDemo(1L);
        return "dbDemo " + gson.toJson(dbDemo);
    }

    public static void main(String[] args) {
        Demo demo = new Demo();
        demo.setName("呼吸");
        demo.setAge(18);
        demo.setRemark("备注");
        Map<Object, Object> map = new HashMap<>();
        map.put("params", demo);
        System.out.println(gson.toJson(map));
    }
}
