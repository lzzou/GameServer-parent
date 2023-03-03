package com.base.spy;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Java工具接口
 *
 * @author zlz
 */
public class JavaTool {
    /**
     * 调用jstack命令
     *
     * @param pid 进程ID
     * @return stack对象信息
     */
    public static JStackObject jstack(int pid) {
        JStackObject object = new JStackObject();

        try {
            ProcessBuilder builder = new ProcessBuilder("jstack", "-l", String.valueOf(pid));
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.getProperty("line.separator"));
            }

            object.parse(sb.toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

//    /**
//     * jps命令（显示当前机器所有java进程）
//     *
//     * @return
//     */
//    public static JPSObject jps() {
//        try {
//            ProcessBuilder builder = new ProcessBuilder("jps", "");
//            Process process = builder.start();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//                sb.append(System.getProperty("line.separator"));
//            }
//
//            System.out.println(sb.toString().trim());
//
//            JPSObject object = new JPSObject();
//            return object;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

}
