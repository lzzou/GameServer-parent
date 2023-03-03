package com.base.spy;

import java.util.ArrayList;
import java.util.List;

/**
 * Java进程堆栈信息
 *
 * @author zlz
 */

public class JStackObject {
    private String time;
    private String header;
    private List<ThreadInfo> threads = new ArrayList<>();
    private int deadLock;
    private String stackInfo;
    private String globalRefs;

    public List<ThreadInfo> getThreads() {
        return threads;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the stackInfo
     */
    public String getStackInfo() {
        return stackInfo;
    }

    /**
     * @param stackInfo the stackInfo to set
     */
    public void setStackInfo(String stackInfo) {
        this.stackInfo = stackInfo;
    }

    /**
     * @return the globalRefs
     */
    public String getGlobalRefs() {
        return globalRefs;
    }

    /**
     * @param globalRefs the globalRefs to set
     */
    public void setGlobalRefs(String globalRefs) {
        this.globalRefs = globalRefs;
    }

    /**
     * 解析堆栈信息
     *
     * @param stackInfo
     */
    public void parse(String stackInfo) {
        this.stackInfo = stackInfo;
        time = Regex.find(stackInfo, "\\d+-\\d+-\\d+ \\d+:\\d+:\\d+");
        header = Regex.find(stackInfo, "Full thread.+:");
        globalRefs = Regex.find(stackInfo, "JNI global references: \\d+");

        String dead = Regex.find(stackInfo, "Found (\\d+) deadlock.");

        /**死锁检测*/
        if (!dead.isEmpty()) {
            deadLock = Integer.parseInt(dead.split(" ")[1]);
            System.err.println(String.format("@_@ spy find %d dead lock!!", deadLock));
        }

        List<String> threadsStr = Regex.finds(stackInfo, "\".+?\" #\\d+?.+?- None");

        for (String str : threadsStr) {
            threads.add(new ThreadInfo(str));
        }
    }

}
