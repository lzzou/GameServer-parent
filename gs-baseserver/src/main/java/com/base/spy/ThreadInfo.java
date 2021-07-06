package com.base.spy;

/**
 * 线程信息
 *
 * @author dansen
 * @date May 26, 2017 3:53:23 PM
 * @desc
 */
public class ThreadInfo {
    private String threadID;
    private String content;
    private ThreadStatusType statusType;
    private String status;
    private String condition;
    private String name;

    public ThreadInfo(String content) {
        this.content = content;
        parse();
    }

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 解析线程信息字符串
     */
    public void parse() {
        threadID = Regex.val(content, "tid=((\\w+))");
        status = Regex.val(content, "java.lang.Thread.State: ((\\w+))");
        name = Regex.val(content, "\"((.+?))\"");

        if (status.equals("TIMED_WAITING")) {
            statusType = ThreadStatusType.Timed_Waiting;
        } else if (status.equals("RUNNABLE")) {
            statusType = ThreadStatusType.Runnable;
        } else if (status.equals("BLOCKED")) {
            statusType = ThreadStatusType.Blocked;
        } else if (status.equals("WAITING")) {
            statusType = ThreadStatusType.Waiting;
            condition = Regex.val(content, "\\[((\\w+))\\]");
        } else {
            System.err.println(name + " unknow status ----> " + status);
        }
    }

    /**
     * @return the threadID
     */
    public String getThreadID() {
        return threadID;
    }

    /**
     * @param threadID the threadID to set
     */
    public void setThreadID(String threadID) {
        this.threadID = threadID;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the statusType
     */
    public ThreadStatusType getStatusType() {
        return statusType;
    }

    /**
     * @param statusType the statusType to set
     */
    public void setStatusType(ThreadStatusType statusType) {
        this.statusType = statusType;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
