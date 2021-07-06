package com.base.spy;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 *
 * @author dansen
 * @date Jun 1, 2017 1:44:33 PM
 * @desc
 */
public class Regex {
    /**
     * 查找某个精确值
     *
     * @param str
     * @param regex
     * @return
     */
    public static String val(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        String val = "";
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            if (matcher.groupCount() > 1) {
                val = matcher.group(1);
            }
        }
        return val;
    }

    /**
     * 查找匹配字符串
     *
     * @param str
     * @param regex
     * @return
     */
    public static String find(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        String val = "";
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            val = matcher.group();
        }
        return val;
    }

    /**
     * 查找所有匹配值
     *
     * @param str
     * @param regex
     * @return
     */
    public static List<String> finds(String str, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);
        List<String> vals = new ArrayList<>();
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            vals.add(matcher.group());
        }

        return vals;
    }
}
