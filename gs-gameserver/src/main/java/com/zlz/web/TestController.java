package com.zlz.web;

import com.base.web.BaseHandlerServlet;
import com.base.web.WebHandleAnnotation;

/**
 * @author zlz
 */
@WebHandleAnnotation(name = "/test", description = "测试")
public class TestController extends BaseHandlerServlet {

    @Override
    public String execute(String json) {
        return "Hello Test!!!";
    }

}
