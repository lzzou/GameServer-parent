package com.base.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 事件源。同一事件类型，支持多个监听者，并且监听者对象可以相同。
 */
public class EventSource implements IEventSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSource.class);

    /**
     * 监听列表。同一事件，可能存在多个相同的监听者。
     */
    private Map<Integer, Collection<IEventListener>> listeners;

    private ReadWriteLock lock;

    /**
     * 构造方法
     */
    public EventSource() {
        this.listeners = new ConcurrentHashMap<Integer, Collection<IEventListener>>();
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * 将监听者加入到指定事件类型的监听队列中。
     *
     * @param eventType 事件类型
     * @param listener  监听者
     */
    @Override
    public void addListener(int eventType, IEventListener listener) {
        if (listener == null) {
            LOGGER.error("listener is null.eventType:" + eventType);
            return;
        }

        this.lock.writeLock().lock();
        try {
            Collection<IEventListener> lstns = this.listeners.get(eventType);
            if (lstns == null) {
                lstns = new LinkedList<IEventListener>();
                lstns.add(listener);
                this.listeners.put(eventType, lstns);
            } else {
                lstns.add(listener);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * 从指定事件类型的监听队列中移除指定的监听者。
     *
     * @param eventType 事件类型
     * @param listener  监听者
     */
    @Override
    public void removeListener(int eventType, IEventListener listener) {
        this.lock.writeLock().lock();
        try {
            Collection<IEventListener> lstns = this.listeners.get(eventType);
            if (lstns != null) {
                lstns.remove(listener);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * 移除指定的监听者。
     *
     * @param listener 监听者
     */
    @Override
    public void removeListener(IEventListener listener) {
        this.lock.writeLock().lock();
        try {
            for (Entry<Integer, Collection<IEventListener>> entry : listeners.entrySet()) {
                if (entry != null && entry.getValue() != null) {
                    if (entry.getValue().remove(listener)) {
                        return;
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void removeAllListener() {
        this.lock.writeLock().lock();
        try {
            listeners.clear();
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    /**
     * 通知监听者发生了事件。事件源由事件参数arg指定。
     *
     * @param arg 事件参数
     */
    @Override
    public void notifyListeners(EventArg arg) {
        List<IEventListener> list = new ArrayList<>();

        // 必须使用写锁
        this.lock.writeLock().lock();
        try {
            Collection<IEventListener> lstns = this.listeners.get(arg.getEventType());
            if (lstns != null) {
                list.addAll(lstns);
            }
        } finally {
            this.lock.writeLock().unlock();
        }

        for (IEventListener item : list) {
            try {
                if (item != null) {
                    item.onEvent(arg);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * 通知监听者发生了事件。事件源为当前this对象。
     *
     * @param eventType 事件类型
     */
    @Override
    public void notifyListeners(int eventType) {
        List<IEventListener> lstns = new ArrayList<>();
        // 必须使用写锁
        this.lock.writeLock().lock();
        try {
            Collection<IEventListener> temp = listeners.get(eventType);
            if (temp != null) {
                lstns.addAll(temp);
            }
        } finally {
            this.lock.writeLock().unlock();
        }

        EventArg eventArg = new EventArg(this, eventType);
        for (IEventListener listener : lstns) {
            try {
                if (listener != null) {
                    listener.onEvent(eventArg);
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }
}
