package com.base.net.nettytd.tasks;

import com.base.net.nettytd.common.Computation;
import com.base.net.nettytd.tasks.framework.SimpleSingleQueueActor;

import java.math.BigInteger;
import java.util.concurrent.Executor;

/**
 * Simple version of actor that processes single queue with simple callback.
 * <p>
 * For bull-blown multi-queue implementation see {@link CounterActor}.
 *
 * @author Stepan Koltsov
 */
public class CounterSimpleActor extends SimpleSingleQueueActor<BigInteger> {

    final Computation computation = new Computation();

    /**
     * @param executor to execute this actor
     */
    public CounterSimpleActor(Executor executor) {
        super(executor);
    }

    @Override
    protected void processMessage(BigInteger message) {
        computation.update(message);
    }

}
