package com.zlz.web;

import com.base.web.BaseHandlerServlet;
import com.base.web.WebHandleAnnotation;

/**
 * @Author: longzhang_zou
 * @Date: 2020年07月14日 19:10
 * @Description:
 */
@WebHandleAnnotation(name = "/test", description = "测试")
public class TestController extends BaseHandlerServlet {

    @Override
    public String execute(String json) {
        return "Hello Test!!!";
    }

}
