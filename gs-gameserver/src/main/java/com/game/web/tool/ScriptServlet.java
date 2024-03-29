package com.game.web.tool;

import com.base.web.BaseHandlerServlet;
import com.base.web.WebHandleAnnotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 脚本
 */
@WebHandleAnnotation(name = "/script", description = "脚本")
public class ScriptServlet extends BaseHandlerServlet {
    private static final long serialVersionUID = 1;

    @Override
    public String execute(String json) {
        try {
            // {"script":"System.out.println(\"hello !!!!!!!!!!!!!!!!!!!!!!!!!!\");"}
            /**
             {"script":"import com.base.component.ComponentManager;
             import com.zlz.component.ScriptComponent;

             ScriptComponent component = (ScriptComponent) ComponentManager.getInstance().getComponent(ScriptComponent.class);
             component.stop();"}
             */
            /**
             import com.base.component.ComponentManager;
             import com.zlz.component.ScriptComponent;

             ScriptComponent component = (ScriptComponent) ComponentManager.getInstance().getComponent(ScriptComponent.class);
             component.stop();
             */
            //JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            //String script = jsonObject.get("script").getAsString();

            String result = "";
            //if (!StringUtil.isNullOrEmpty(script)) {
            //    result = ScriptComponent.evaluate(script);
            //}
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String execute(String json, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
