package com.base.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IHandlerServlet {
    /**
     * 处理json String
     *
     * @param json 要处理的String
     * @return 返回的String
     */
    String execute(String json);

    /**
     * 处理json String
     *
     * @param json 要处理的String
     * @return 返回的String
     */
    String execute(String json, HttpServletRequest request, HttpServletResponse response);
}
