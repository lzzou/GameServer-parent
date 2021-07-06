package com.zlz;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 *
 * @author zhaoziwei
 * @date 2016年1月27日 上午10:23:42
 */
public class Md5Utils {

    private static Logger log = LoggerFactory.getLogger(Md5Utils.class);

    // 全局数组
    private static final String[] strDigits = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        if (iRet < 0) {
            iRet += 256;
        }
        int iD2 = iRet % 16;
        int iD1 = iRet / 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuilder sBuffer = new StringBuilder();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }

    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 获取字符串的MD5码 32位
     *
     * @param strObj
     * @return
     */
    public static String getMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = strObj;
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            log.error("字符串MD5异常!", ex);
        }
        return resultString;
    }

    /**
     * 获取字符串的MD5码 32位
     *
     * @param strObj
     * @return
     */
    public static String getMD5Code(String strObj, String enc) {
        String resultString = null;
        try {
            resultString = strObj;
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes(enc)));
        } catch (Exception ex) {
            log.error("字符串MD5异常!", ex);
        }
        return resultString;
    }

    /**
     * 获取字符串的MD5码 32位
     *
     * @param strObj
     * @return
     */
    public static String getMD5Code(byte[] strObj) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj));
        } catch (NoSuchAlgorithmException ex) {
            log.error("字符串MD5异常!", ex);
        }
        return resultString;
    }
}