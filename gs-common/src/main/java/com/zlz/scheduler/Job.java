/**
 *
 */
package com.zlz.scheduler;

/**
 * @date Apr 28, 2017 11:34:08 AM
 * @author dansen
 * @desc
 */
public abstract class Job implements Runnable {
    private boolean isStop = false;
    private long interval;
    private long delay;
    private long nextTime = 0;
    private long jobID = 0;

    /**
     * @return the jobID
     */
    public long getJobID() {
        return jobID;
    }

    /**
     * @param jobID
     *            the jobID to set
     */
    public void setJobID(long jobID) {
        this.jobID = jobID;
    }

    /**
     * @return the nextTime
     */
    public long getNextTime() {
        return nextTime;
    }

    /**
     * @param nextTime
     *            the nextTime to set
     */
    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    /**
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * @param interval
     *            the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * @return the delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * @param delay
     *            the delay to set
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @return the isStop
     */
    public boolean isStop() {
        return isStop;
    }

    /**
     * @param isStop
     *            the isStop to set
     */
    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

}
