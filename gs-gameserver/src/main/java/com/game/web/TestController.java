package com.game.web;

import com.base.web.BaseHandlerServlet;
import com.base.web.WebHandleAnnotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zlz
 */
@WebHandleAnnotation(name = "/test", description = "测试")
public class TestController extends BaseHandlerServlet {

    @Override
    public String execute(String json) {
        return "Hello Test!!!";
    }

    @Override
    public String execute(String json, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
