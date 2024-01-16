package com.game.component;

import com.base.component.AbstractCommandComponent;
import com.base.component.Component;
import com.base.component.ComponentManager;
import com.base.component.GlobalConfigComponent;
import com.base.net.AbstractNettyComponent;
import com.base.net.client.AbstractClientPacketHandler;
import com.base.net.client.ClientIOHandler;
import com.game.util.ThreadPoolUtil;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 服务netty网络组件，提供TCP连接
 */
@Component
public class MyServerNettyComponent extends AbstractNettyComponent {
    public static class MyServerClientHandler extends AbstractClientPacketHandler {
        @Override
        public AbstractCommandComponent getComponent() {
            UserCommandComponent cm = ComponentManager.getInstance().getComponent(UserCommandComponent.class);
            return cm;
        }
    }

    @Override
    public void initGroup() {
        int count = Runtime.getRuntime().availableProcessors();

        parentCount = count;
        parentExecutor = ThreadPoolUtil.service(parentCount, "netty-game-boss-pool");

        childCount = count * 3 + 1;
        childExecutor = ThreadPoolUtil.service(childCount, "netty-game-worker-pool");
    }

    @Override
    protected void acceptorInit(ChannelPipeline pipeline) {
        //pipeline.addLast("codec-d", new StrictMessageDecoder());
        //pipeline.addLast("codec-e", new StrictMessageEncoder());
        pipeline.addLast("codec-d", new StringDecoder());
        pipeline.addLast("codec-e", new StringEncoder());
        pipeline.addLast("handler", new ClientIOHandler(new MyServerClientHandler()));
    }

    @Override
    protected int getPort() {
        return Integer.parseInt(GlobalConfigComponent.getConfig().server.ports[0]);
    }
}
