package com.base.net.nettytd;


import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * @author Stepan Koltsov
 */
public class Unittest {

    public void it() throws Exception {
        Server server = new Server();

        CountDownLatch latch = new CountDownLatch(1);
        int[] r = new int[1];

        Client client = new Client(server.getPort(), new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                r[0] = integer;
                latch.countDown();
            }
        });

        client.send(100);
        latch.await();
    }

}
