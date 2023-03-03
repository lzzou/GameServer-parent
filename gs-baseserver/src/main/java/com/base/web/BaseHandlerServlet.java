package com.base.web;

import com.zlz.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 处理参数（params）为json格式字符串的Servlet基类。
 *
 * @author zlz
 */
@Slf4j
public abstract class BaseHandlerServlet extends HttpServlet implements IHandlerServlet {
    private static final long serialVersionUID = -7337936262053988938L;

    protected String keyword;

    protected HttpServletRequest request;

    protected HttpServletResponse response;

    /**
     * 请求客户端的IP
     */
    protected String clientIP = null;

    /**
     * 非法的客户端IP
     */
    protected static final byte INVALID_IP = 5;

    public BaseHandlerServlet() {
        super();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("utf-8");
        this.request = request;
        this.response = response;

        keyword = this.getClass().getAnnotation(WebHandleAnnotation.class).name();
        keyword = keyword.replace("/", "");

        String result = null;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");

        // IP 检查
        clientIP = ServletUtil.getRequestIP(request);
        String host = ServletUtil.getRequestHost(request);

        // if (checkRequestIP(clientIP) == false && checkRequestIP(host) == false)
        // {
        // result = gson.toJson(INVALID_IP);
        // }
        // else
        // {
        String json = request.getParameter("params");
        try {
            result = execute(json);
        } catch (Exception e) {
            String msg = String.format("%s--请求异常:action(%s) params(%s)", clientIP, keyword, json);
            log.error(msg, e);
        }
        // }

        response.setCharacterEncoding("UTF-8");

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }

    @Override
    public String execute(String json, HttpServletRequest request, HttpServletResponse response) {
        return "";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        this.request = request;
        this.response = response;

        keyword = this.getClass().getAnnotation(WebHandleAnnotation.class).name();
        keyword = keyword.replace("/", "");

        String result = null;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");

        // IP 检查
        clientIP = ServletUtil.getRequestIP(request);
        String host = ServletUtil.getRequestHost(request);

        // if (checkRequestIP(clientIP) == false && checkRequestIP(host) == false)
        // {
        // result = gson.toJson(INVALID_IP);
        // }
        // else
        // {
        // String json = request.getParameter("params");
        String param = getParam(request);
        try {
            result = execute(param);
            // result = execute(json);
        } catch (Exception e) {
            String msg = String.format("%s--请求异常:action(%s) params(%s)", clientIP, keyword, param);
            log.error(msg, e);
        }
        // }

        response.setCharacterEncoding("UTF-8");

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print(result);
        out.flush();
        out.close();
    }

    /**
     * 获取post请求中body的参数
     *
     * @param request HttpServletRequest
     * @return 参数字符串
     */
    public String getParam(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            log.error("获取请求参数错误:{}", e.getMessage());
        }
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            log.error("获取请求参数错误:{}", e.getMessage());
        }
        return sb.toString();
    }


    /**
     * 获取请求客户端的IP
     *
     * @return
     */
    protected String getRequstIP() {
        return clientIP;
    }

    /**
     * 转换utf-8编码
     *
     * @param value
     * @return
     */
    public String getUTF8(String value) {
        if (value != null) {
            if (!checkGBK(value)) {
                return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            } else {
                return value;
            }
        }
        return "";
    }

    /**
     * 检查能否用GBK解码
     *
     * @param value
     * @return
     */
    public boolean checkGBK(String value) {
        return java.nio.charset.Charset.forName("GBK").newEncoder().canEncode(value);
    }
}
