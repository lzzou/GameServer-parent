package com.game.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FileUtil {
    /**
     * 读取数据
     *
     * @param filename
     * @return byte[]
     */
    public static byte[] readToArray(String filename) throws IOException {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    /**
     * 读取文本文件内容
     *
     * @param filePathAndName 带有完整绝对路径的文件名
     * @param encoding        文本文件打开的编码方式
     * @return 返回文本文件的内容
     */
    public static String readTxt(String filePathAndName, String encoding) throws IOException {
        encoding = encoding.trim();
        StringBuffer str = new StringBuffer("");
        FileInputStream fs = new FileInputStream(filePathAndName);
        InputStreamReader isr;
        if (encoding.equals("")) {
            isr = new InputStreamReader(fs);
        } else {
            isr = new InputStreamReader(fs, encoding);
        }
        BufferedReader br = new BufferedReader(isr);
        String data = "";
        while ((data = br.readLine()) != null) {
            str.append(data + " ");
        }
        br.close();
        return str.toString();
    }

    /**
     * 一行一行的读取文本文件内容
     *
     * @param filePathAndName 带有完整绝对路径的文件名
     * @param encoding        文本文件打开的编码方式
     * @return
     * @throws IOException 返回文本文件的内容
     */
    public static List<String> readLines(String filePathAndName, String encoding) {
        List<String> lines = new ArrayList<String>();
        encoding = encoding.trim();
        int length = 80960;
        FileInputStream fs;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(filePathAndName);
            InputStreamReader isr;
            if (encoding.equals("")) {
                isr = new InputStreamReader(fs);
            } else {
                isr = new InputStreamReader(fs, encoding);
            }
            br = new BufferedReader(isr, length);
            String line = "";
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return lines;
    }

    /**
     * 新建目录
     *
     * @param folderPath 目录
     * @return 返回目录创建后的路径
     */
    public static String createFolder(String folderPath) throws IOException {
        String txt = folderPath;
        File myFilePath = new File(txt);
        txt = folderPath;
        if (!myFilePath.exists()) {
            myFilePath.mkdir();
        }
        return txt;
    }

    /**
     * 多级目录创建
     *
     * @param folderPath 准备要在本级目录下创建新目录的目录路径 例如 c:myf
     * @param paths      无限级目录参数，各级目录以单数线区分 例如 a|b|c
     * @return 返回创建文件后的路径 例如 c:myfac
     */
    public static String createFolders(String folderPath, String paths)
            throws IOException {
        String txts = folderPath;
        String txt;
        txts = folderPath;
        StringTokenizer st = new StringTokenizer(paths, "|");
        for (; st.hasMoreTokens(); ) {
            txt = st.nextToken().trim();
            if (txts.lastIndexOf("/") != -1) {
                txts = createFolder(txts + txt);
            } else {
                txts = createFolder(txts + txt + "/");
            }
        }
        return txts;
    }

    /**
     * 新建文件
     *
     * @param filePathAndName 文本文件完整绝对路径及文件名
     * @param fileContent     文本文件内容
     * @return
     */
    public static void createFile(String filePathAndName, String fileContent)
            throws IOException {
        String filePath = filePathAndName;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);

        // 判断目标文件所在的目录是否存在
        if (!myFilePath.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            if (!myFilePath.getParentFile().mkdirs()) {
                return;
            }
        }

        if (!myFilePath.exists()) {
            myFilePath.createNewFile();
        }
        FileWriter resultFile = new FileWriter(myFilePath);
        PrintWriter myFile = new PrintWriter(resultFile);
        String strContent = fileContent;
        myFile.println(strContent);
        myFile.close();
        resultFile.close();
    }

    /**
     * 有编码方式的文件创建
     *
     * @param filePathAndName 文本文件完整绝对路径及文件名
     * @param fileContent     文本文件内容
     * @param encoding        编码方式 例如 GBK 或者 UTF-8
     * @return
     */
    public static void createFile(String filePathAndName, String fileContent,
                                  String encoding) throws IOException {
        String filePath = filePathAndName;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);

        // 判断目标文件所在的目录是否存在
        if (!myFilePath.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            if (!myFilePath.getParentFile().mkdirs()) {
                return;
            }
        }

        if (!myFilePath.exists()) {
            myFilePath.createNewFile();
        }
        PrintWriter myFile = new PrintWriter(myFilePath, encoding);
        String strContent = fileContent;
        myFile.println(strContent);
        myFile.close();
    }

    /**
     * 删除文件
     *
     * @param filePathAndName 文本文件完整绝对路径及文件名
     * @return Boolean 成功删除返回true遭遇异常返回false
     */
    public static boolean delFile(String filePathAndName) {
        boolean bea = false;
        String filePath = filePathAndName;
        File myDelFile = new File(filePath);
        if (myDelFile.exists()) {
            myDelFile.delete();
            bea = true;
        } else {
            bea = false;
        }
        return bea;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     * @return
     */
    public static void delFolder(String folderPath) {
        delAllFile(folderPath); // 删除完里面所有内容
        String filePath = folderPath;
        filePath = filePath.toString();
        File myFilePath = new File(filePath);
        myFilePath.delete(); // 删除空文件夹
    }

    /**
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean bea = false;
        File file = new File(path);
        if (!file.exists()) {
            return bea;
        }
        if (!file.isDirectory()) {
            return bea;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                bea = true;
            }
        }
        return bea;
    }

    /**
     * 复制单个文件
     *
     * @param oldPathFile 准备复制的文件源
     * @param newPathFile 拷贝到新绝对路径带文件名
     * @return
     * @throws IOException
     */
    public static void copyFile(String oldPathFile, String newPathFile)
            throws IOException {
        int byteread = 0;
        File oldfile = new File(oldPathFile);
        if (oldfile.exists()) { // 文件存在时
            InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
            @SuppressWarnings("resource")
            FileOutputStream fs = new FileOutputStream(newPathFile);
            byte[] buffer = new byte[1444];
            while ((inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
        }
    }

    /**
     * 复制整个文件夹的内容
     *
     * @param oldPath 准备拷贝的目录
     * @param newPath 指定绝对路径的新目录
     * @return
     * @throws IOException
     */
    public static void copyFolder(String oldPath, String newPath)
            throws IOException {
        new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件夹
        File a = new File(oldPath);
        String[] file = a.list();
        File temp = null;
        for (int i = 0; i < file.length; i++) {
            if (oldPath.endsWith(File.separator)) {
                temp = new File(oldPath + file[i]);
            } else {
                temp = new File(oldPath + File.separator + file[i]);
            }
            if (temp.isFile()) {
                FileInputStream input = new FileInputStream(temp);
                FileOutputStream output = new FileOutputStream(newPath + "/"
                        + (temp.getName()).toString());
                byte[] b = new byte[1024 * 5];
                int len;
                while ((len = input.read(b)) != -1) {
                    output.write(b, 0, len);
                }
                output.flush();
                output.close();
                input.close();
            }
            if (temp.isDirectory()) {// 如果是子文件夹
                copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
            }
        }
    }

    /**
     * 移动文件
     *
     * @param oldPath
     * @param newPath
     * @return
     * @throws IOException
     */
    public static void moveFile(String oldPath, String newPath)
            throws IOException {
        copyFile(oldPath, newPath);
        delFile(oldPath);
    }

    /**
     * 移动目录
     *
     * @param oldPath
     * @param newPath
     * @return
     * @throws IOException
     */
    public static void moveFolder(String oldPath, String newPath)
            throws IOException {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    /**
     * 判断是否是相对路径 还是绝对路径
     *
     * @param path
     * @return
     */
    public static boolean isAbsolutePath(String path) {
        if (path.startsWith("/") || path.startsWith("\\") || path.indexOf(":") < 0) {
            return true;
        }
        return false;
    }

    public static String getExt(File file) {
        return file.getName().substring(file.getName().lastIndexOf("."));
    }

    public static String getName(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isFileExist(String path) {
        return new File(path).exists();
    }

}
