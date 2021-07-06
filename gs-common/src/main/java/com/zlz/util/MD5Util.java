package com.zlz.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class MD5Util {
    private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 字节转十六进制
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int t;
        for (int i = 0; i < 16; i++) {
            t = bytes[i];
            if (t < 0) {
                t += 256;
            }
            sb.append(hexDigits[(t >>> 4)]);
            sb.append(hexDigits[(t % 16)]);
        }
        return sb.toString();
    }

    public static String md5(String input) {
        return code(input, 32);
    }

    public static boolean md5Check(String source, String code) {
        String result = md5(source);
        if (result.equalsIgnoreCase(code)) {
            return true;
        } else {
            return false;
        }
    }

    public static String fileMD5(File file) {
        FileInputStream fis = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[2048];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }

            byte[] b = md.digest();

            return bytesToHex(b).substring(8, 24);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String code(String input, int bit) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            if (bit == 16) {
                return bytesToHex(md.digest(input.getBytes("utf-8"))).substring(8, 24);
            }

            return bytesToHex(md.digest(input.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}
