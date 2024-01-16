package com.game.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitUtil {

    private StringSplitUtil() {
    }

    public static int[] splitToInt(String str) {
        return splitToInt(str, ",");
    }

    public static List<String> splitToStr(String str, String spStr) {
        if (str == null || str.trim().length() == 0)
            return new ArrayList<>();

        try {
            String[] temps = str.split(spStr);
            List<String> results = new ArrayList<>(temps.length);
            for (int i = 0; i < temps.length; i++) {
                results.add(temps[i].trim());
            }
            return results;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    public static int[] splitToInt(String str, String spStr) {
        if (str == null || str.trim().length() == 0) {
            return new int[0];
        }

        try {
            String[] temps = str.split(spStr);
            int len = temps.length;
            int[] results = new int[len];
            for (int i = 0; i < len; i++) {
                results[i] = Integer.parseInt(temps[i].trim());
            }
            return results;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public static float[] splitToFloat(String str, String spStr) {
        if (str == null || str.trim().length() == 0) {
            return new float[0];
        }

        try {
            String[] temps = str.split(spStr);
            int len = temps.length;
            float[] results = new float[len];
            for (int i = 0; i < len; i++) {
                results[i] = Float.parseFloat(temps[i].trim());
            }
            return results;
        } catch (Exception e) {
            return new float[0];
        }
    }

    public static String concatToStr(int[] ints) {
        if (ints == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ints.length; i++) {
            sb.append(ints[i]).append(",");
        }
        if (sb.length() == 0) {
            return "";
        }
        return sb.substring(0, sb.length() - 1).toString();
    }

    public static void print(int[] results) {
        for (int i = 0; i < results.length; i++) {
            System.err.print(results[i] + ",");
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 拆分函数结果类
     *
     * @author dream.wang
     */
    public static class SplitData {
        public String method;
        public int[] values;

        public SplitData(int n) {
            method = "none";
            values = new int[n];
        }
    }

    /**
     * |拆分多个分隔符
     *
     * @param str
     * @return
     */
    public static SplitData splitMethod(String str, int n) {
        String[] temp = str.split("\\(|\\,|\\)");

        if (temp.length < 1)
            return null;

        n = temp.length - 1 > n ? temp.length - 1 : n;

        SplitData data = new SplitData(n);

        data.method = temp[0];

        for (int i = 1; i < temp.length; i++) {
            if (temp[i] != "" && !temp[i].isEmpty()) {
                try {
                    data.values[i - 1] = Integer.valueOf(temp[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    public static SplitData splitMethod(String str) {
        return splitMethod(str, 8);
    }

    public static void main(String[] args) {
        StringSplitUtil.splitMethod("test(1,2,0,abc)", 1);
    }

    /**
     * <pre>
     * 两个字符串相加，按照指定分隔符。
     * </pre>
     *
     * @param str
     * @param appendStr
     * @param spStr
     * @return
     */
    public static String strAppend(String str, String appendStr, String spStr) {
        if (spStr == null) {
            spStr = ",";
        }

        if ((str == null || str.trim().length() == 0) && (appendStr == null || appendStr.trim().length() == 0)) {
            return "";
        }

        if ((str == null || str.trim().length() == 0)) {
            return appendStr;
        }

        if (appendStr == null || appendStr.trim().length() == 0) {
            return str;
        }

        StringBuffer sBuffer = new StringBuffer();

        try {
            String tempStr = str + spStr + appendStr;
            String[] temps = tempStr.split(spStr);
            int len = temps.length;

            for (int i = 0; i < len; i++) {
                if (temps[i] != null && temps[i].trim().length() > 0) {
                    sBuffer.append(temps[i].trim()).append(spStr);
                }
            }

            if (sBuffer.length() > 0) {
                return sBuffer.substring(0, sBuffer.length() - 1);
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
