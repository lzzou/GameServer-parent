package com.base.net.nettytd;

import com.base.net.nettytd.tasks.framework.LockFreeStackWithSize;
import com.base.net.nettytd.tasks.framework.Tasks;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.List;
import java.util.ListIterator;

/**
 * @author Stepan Koltsov
 */
public class BetterWrite<T> {

    private static final AttributeKey<BetterWrite> KEY = AttributeKey.valueOf(BetterWrite.class.getName());

    private final Channel channel;
    private final LockFreeStackWithSize<QueueItem<T>> queue = new LockFreeStackWithSize<>();
    private final Tasks tasks = new Tasks();

    private static class QueueItem<T> {
        private final T item;
        private final ChannelFutureListener listener;

        private QueueItem(T item, ChannelFutureListener listener) {
            this.item = item;
            this.listener = listener;
        }
    }

    public BetterWrite(Channel channel) {
        this.channel = channel;
    }

    private void flush() {
        while (tasks.fetchTask()) {
            List<QueueItem<T>> items = queue.removeAllReversed();
            ListIterator<QueueItem<T>> iterator = items.listIterator(items.size());
            while (iterator.hasPrevious()) {
                QueueItem<T> item = iterator.previous();
                ChannelFuture future = channel.write(item.item);
                if (item.listener != null) {
                    future.addListener(item.listener);
                }
            }
        }
        channel.flush();
    }

    public static <T> void write(Channel channel, T value, ChannelFutureListener listener) {
        BetterWrite<T> betterWriter = getBetterWrite(channel);

        betterWriter.queue.add(new QueueItem<>(value, listener));
        if (betterWriter.tasks.addTask()) {
            channel.pipeline().lastContext().executor().execute(betterWriter::flush);
        }
    }

    private static <T> BetterWrite<T> getBetterWrite(final Channel channel) {
        final Attribute<BetterWrite> attr = (Attribute<BetterWrite>) channel.attr(KEY);
        BetterWrite<T> betterWriter = attr.get();
        if (betterWriter != null) {
            return betterWriter;
        }
        final BetterWrite<T> value = new BetterWrite<>(channel);
        final BetterWrite<T> old = attr.setIfAbsent(value);
        if (old != null) {
            return old;
        }
        return value;
    }

}
