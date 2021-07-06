package com.zlz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String工具类
 */
public class StringUtil {

    private static final Logger log = LoggerFactory.getLogger(StringUtil.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    public static final String EMPTY = "";

    /**
     * 验证email地址是否合法
     *
     * @param address
     * @return
     */
    public static boolean isValidEmailAddress(String address) {
        if (address == null) {
            return false;
        }
        Matcher m = EMAIL_PATTERN.matcher(address);
        return m.find();
    }

    /**
     * 判断字符串是否为空。
     *
     * @param src
     * @return
     */
    public static boolean isNullOrEmpty(String src) {
        if (src == null || src.trim().isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * g过滤emojo表情
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        if (source != null && source.length() > 0) {
            return source.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
        } else {
            return source;
        }
    }

    /**
     * 判断字符串是否为空或者只包含空格。
     *
     * @param src
     * @return
     */
    public static boolean isEmptyOrWhitespaceOnly(String src) {
        if (isNullOrEmpty(src)) {
            return true;
        }

        if (src.trim().length() == 0) {
            return true;
        }

        return false;
    }

    public static int FirstToken(String params_, char sep, String token) {
        int end = params_.indexOf(sep);

        if (end != -1) {
            token = params_.substring(0, end);
            return end;
        }

        return -1;
    }

    /**
     * 判断字符串是否超过最大长度
     *
     * @param rawStr
     * @param maxLen
     * @return
     */
    public static String verifyMaxLen(String rawStr, int maxLen) {
        if (rawStr == null || rawStr.trim().length() == 0) {
            return rawStr;
        }
        if (rawStr.length() > maxLen) {
            return rawStr.substring(0, maxLen);
        }
        return rawStr;
    }

    public static void geneteCheckImg(int[] checkCodeSet, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Time New Roman", Font.PLAIN, 60));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int x1 = random.nextInt(12);
            int y1 = random.nextInt(12);
            g.drawLine(x, y, x + x1, y + y1);
        }

        for (int i = 0; i < checkCodeSet.length; i++) {
            int j = checkCodeSet[i];
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(j + "", 30 * i + 10, 66);
        }

        // try {
        // ImageIO.write(image, "png", new
        // File("D:\\yishi\\slg\\server\\branches\\Workspace\\test.png"));
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     *
     * @param value 指定的字符串
     * @return 字符串的长度
     */
    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                /* 中文字符长度为2 */
                valueLength += 2;
            } else {
                /* 其他字符长度为1 */
                valueLength += 1;
            }
        }
        return valueLength / 2;
    }

    /**
     * <pre>
     * 是否为数字类型(负数返回false)
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (str.matches("\\d*")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <pre>
     * 通过ascii做非空判断
     * </pre>
     *
     * @param str
     * @return
     */
    public static boolean isContainsSpace(String str) {
        if (str == null) {
            return true;
        }

        char[] b = str.toCharArray();

        for (int i = 0; i < b.length; i++) {
            if ((int) b[i] == 32) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找正则
     *
     * @param content
     * @param regex
     * @return
     */
    public static List<String> find(String content, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);
        Matcher m = pattern.matcher(content);

        List<String> vals = new ArrayList<>();

        while (m.find()) {
            for (int i = 0; i < m.groupCount(); ++i) {
                vals.add(m.group(i));
            }
        }

        return vals;
    }

    /**
     * @param content
     * @param regex
     * @return
     */
    public static String findFirst(String content, String regex) {
        try {
            List<String> vals = find(content, regex);
            return vals.size() < 2 ? null : vals.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * generate a string
     *
     * @param len
     * @return
     */
    public static String randomString(int len) {
        String total = "";

        while (total.length() < len) {
            long time = System.currentTimeMillis() << 4;
            time += (long) (Math.random() * 10000);
            total += MD5Util.md5(String.valueOf(time));
        }

        return total.substring(0, len);
    }

    /**
     * @param "1,2,3"
     * @param ","
     * @return Set[1, 2, 3]
     */
    public static Set<Integer> splitInt(String val, String del) {
        Set<Integer> vals = new HashSet<>();

        try {
            if (val != null && !val.isEmpty()) {
                String[] v = val.split(del);
                for (String i : v) {
                    if (i != "" && !i.trim().isEmpty())
                        vals.add(Integer.parseInt(i.trim()));
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return vals;
    }

    public static List<Integer> splitIntToList(String val, String del) {
        List<Integer> vals = new ArrayList<>();

        try {
            if (val != null && !val.isEmpty()) {
                String[] v = val.split(del);
                for (String i : v) {
                    if (i != "" && !i.trim().isEmpty())
                        vals.add(Integer.parseInt(i.trim()));
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }

        return vals;
    }


    /**
     * 合并int集
     *
     * @param vals
     * @return
     */
    public static String mergeInt(Set<Integer> vals) {
        String val = "";

        for (Integer v : vals) {
            val += v + ",";
        }

        if (val.endsWith(",")) {
            val = val.substring(0, val.length() - 1);
        }

        return val;
    }

    public static String mergeInt(List<Integer> vals) {
        String val = "";

        for (Integer v : vals) {
            val += v + ",";
        }

        if (val.endsWith(",")) {
            val = val.substring(0, val.length() - 1);
        }

        return val;
    }

    /**
     * map转字符串，用于发送http请求转义双引号
     *
     * @param map
     * @return
     */
    public static String object2String(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"{\"");

        map.forEach((k, v) -> {
            builder.append(k).append("\":\"").append(v).append("\",");
        });

        if (map.size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        builder.append("}\"");
        return builder.toString();
    }
}
