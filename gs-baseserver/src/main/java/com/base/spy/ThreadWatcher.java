package com.base.spy;

import java.util.ArrayList;
import java.util.List;

/**
 * 线程记录器
 *
 * @author zlz
 */
public class ThreadWatcher {

    private List<ThreadInfo> infos = new ArrayList<>();

    /**
     *
     */
    public ThreadWatcher(ThreadInfo info) {
        infos.add(info);
    }

    public void continueWatch(ThreadInfo info) {
        infos.add(info);
    }

    /**
     * @return
     */
    public int getCount() {
        return infos.size();
    }

}
