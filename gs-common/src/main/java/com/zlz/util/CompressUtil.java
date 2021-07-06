package com.zlz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

/**
 * @author Dream
 * @date 2011-5-12
 */
public class CompressUtil {

    private static final Logger log = LoggerFactory.getLogger(CompressUtil.class);

    private static int BUFFER = 1024;

    /**
     * 压缩数据
     *
     * @param str
     * @return
     */
    public static byte[] compress(String str) {
        byte[] input;
        byte[] output = null;
        Deflater compresser = new Deflater();
        try {
            input = str.getBytes("UTF-8");

            compresser.setInput(input);
            compresser.finish();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();

            try {
                bos.close();
            } catch (IOException e) {
                log.error("CompressUtil:compress", e);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("CompressUtil:compress", e);
        } finally {
            compresser.end();
        }

        return output;
    }

    public static byte[] compress(byte[] input) {
        try {
            byte[] output = null;

            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
            byte[] buf = new byte[1024];
            while (!compresser.finished()) {
                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
            output = bos.toByteArray();

            try {
                bos.close();
            } catch (IOException e) {
                log.error("CompressUtil:compress", e);
            }
            compresser.end();
            return output;
        } catch (Exception e) {
            log.error("CompressUtil:compress", e);
            return null;
        }
    }

    /**
     * Gzip数据压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] compressGzip(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            GZIPOutputStream gos = new GZIPOutputStream(baos);

            int count;
            byte temp[] = new byte[BUFFER];
            while ((count = bais.read(temp, 0, BUFFER)) != -1) {
                gos.write(temp, 0, count);
            }

            gos.finish();
            gos.flush();
            gos.close();

            byte[] output = baos.toByteArray();

            baos.flush();
            baos.close();

            bais.close();

            return output;
        } catch (Exception e) {
            log.error("Gzip Exception:", e);
        }

        return data;
    }

    /**
     * Gzip数据解压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompressGzip(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 解压缩
            GZIPInputStream gis = new GZIPInputStream(bais);

            int count;
            byte temp[] = new byte[BUFFER];
            while ((count = gis.read(temp, 0, BUFFER)) != -1) {
                baos.write(temp, 0, count);
            }

            gis.close();
            data = baos.toByteArray();

            baos.flush();
            baos.close();

            bais.close();

            return data;
        } catch (Exception e) {
            log.error("Gzip Exception:", e);
        }

        return data;
    }

    /**
     * 创建压缩XML文件
     *
     * @param path       文件路径
     * @param str        xml字符串
     * @param fileName   文件名称
     * @param isCompress 是否压缩
     * @param message    是否成功消息
     * @return
     */
    public static String createCompressXml(String path, String str,
                                           String fileName, boolean isCompress, String message) {
        String filePath = path + fileName + ".xml";
        try {
            File myFilePath = new File(filePath);

            myFilePath.delete();
            myFilePath.createNewFile();

            FileOutputStream writer = new FileOutputStream(myFilePath);
            writer.write(isCompress ? compress(str) : str.getBytes());
            writer.close();

            return "Build:" + filePath + "------ " + message;
        } catch (IOException e) {
            log.error("CreateCompressXml " + filePath + " is fail!", e);
            return "Build:" + filePath + "------ Fail!";
        }
    }

    /**
     * 向XML文件中追加内容
     *
     * @param path       文件路径
     * @param str        xml字符串
     * @param fileName   文件名称
     * @param isCompress 是否压缩
     * @param message    是否成功消息
     * @return
     */
    public static void setCompressXml(String path, String str, String fileName,
                                      boolean isCompress, String message) {
        String filePath = path + fileName;
        try {
            // FileWriter writer = new FileWriter(filePath, true);
            // writer.write(str);
            // writer.close();
            RandomAccessFile file = new RandomAccessFile(filePath, "rw");
            long length = file.length();
            int index = 0;
            if (length > 0) {
                index = 38;
            }
            file.seek(length);
            file.writeBytes(str.substring(index, str.length()));
            file.close();
        } catch (IOException e) {
            log.error("CreateCompressXml " + filePath + " is fail!", e);
        }
    }

    /**
     * 解压缩数据
     *
     * @param compressed
     * @return
     */
    public static String decompress(byte[] compressed) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);
        Inflater decompressor = new Inflater();

        try {
            decompressor.setInput(compressed);

            final byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }

            byte[] data = bos.toByteArray();

            return new String(data, "UTF-8");
        } catch (Exception e) {
            log.error("CompressUtil:decompress", e);
        } finally {
            decompressor.end();
        }

        return "";
    }

    /**
     * 解压缩数据
     *
     * @param compressed
     * @return
     */
    public static byte[] decompressToBytes(byte[] compressed) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressed.length);
        Inflater decompressor = new Inflater();

        try {
            decompressor.setInput(compressed);

            final byte[] buf = new byte[1024];
            while (!decompressor.finished()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            }

            byte[] data = bos.toByteArray();

            return data;
        } catch (Exception e) {
            log.error("CompressUtil:decompress", e);
        } finally {
            decompressor.end();
        }

        return null;
    }

    public static final class Test implements Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        public int test1;
        public int test2;
        public int test3;
        public int test4;
        public int test5;
        public int test6;

        public String str1;
        public String str2;
        public String str3;
        public String str4;
        public String str5;
        public String str6;

        public Date date1;
        public Date date2;
        public Date date3;
        public Date date4;

        public float f1;
        public float f2;
        public float f3;
        public float f4;
        public float f5;
        public float f6;

    }

    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);

            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] t) {
        try {
            String input = "Hello snappy-java! Snappy-java is a JNI-based wrapper of "
                    + "Snappy, a fast compresser/decompresser.";
            byte[] compressed = CompressUtil.compress(input);
            byte[] ss = CompressUtil.decompressToBytes(compressed);

            Test test = new Test();
            test.date1 = new Date();
            test.date2 = new Date();
            test.date3 = new Date();
            test.date4 = new Date();

            test.f1 = 1354.45f;
            test.f2 = 123.45f;
            test.f3 = 1235.45f;
            test.f4 = 2354.45f;
            test.f5 = 1254.45f;
            test.f6 = 154.45f;

            test.str1 = "sfsdfsfsf你师父搜房 阿法士大夫撒放";
            test.str2 = "sfsdfsfsf你师父搜房 阿法士大夫撒放";
            test.str3 = "sfsdfsfsf你师父搜房 阿法士大夫撒放";
            test.str4 = "sfsdfsfsf你师父搜房 阿法士大夫撒放";

            byte[] data = serialize(test);
            byte[] data1 = CompressUtil.compress(data);

            System.err.println("serialize:" + data.length + ",compress:" + data1.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
