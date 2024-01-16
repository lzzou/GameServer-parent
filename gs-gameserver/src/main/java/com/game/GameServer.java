package com.game;

import com.base.component.ComponentManager;
import com.base.component.GlobalConfigComponent;
import com.base.database.DBPoolComponent;
import com.base.redis.RedisClientComponent;
import com.base.server.BaseServer;
import com.base.spy.Spy;
import com.base.web.WebComponent;
import com.game.component.MyServerNettyComponent;
import com.game.component.RemoteCacheComponent;
import com.game.component.ScriptComponent;
import com.game.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 游戏主服务
 *
 * @author zlz
 */
@Slf4j
public class GameServer extends BaseServer {

    /**
     * 服务单例
     */
    private volatile static GameServer INSTANCE;

    private GameServer() {
    }

    /**
     * double check 单例模式
     *
     * @return 实例
     * @link 关于单例模式：<a href="https://www.runoob.com/design-pattern/singleton-pattern.html"/>单例模式</a>
     */
    public static GameServer getInstance() {
        if (INSTANCE == null) {
            synchronized (GameServer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GameServer();
                }
            }
        }
        return INSTANCE;
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        log.info("GameServer is starting...");
        if (!GameServer.getInstance().start()) {
            log.error("GameServer has started failed.");
            System.exit(1);
        }
        log.info("GameServer has started successfully, taken {} millis.", System.currentTimeMillis() - start);
        //printNoBug();
    }

    @Override
    protected boolean loadComponent() {
        try {
            startMonitorThread();
            // 方式一、自动加载@Component注解的组件，这里发现有个问题，加载顺序
            /*if (ComponentManager.getInstance().loadComponent(StringUtil.EMPTY)) {
                // 开启jstack监控
                if (GlobalConfigComponent.getConfig().server.isDebug > 0) {
                    Spy.start();
                }
                return ComponentManager.getInstance().start();
            }*/

            // 方式二、手动加载组件，顺序可自定义
            // 全局配置组件
            if (!ComponentManager.getInstance().addComponent(GlobalConfigComponent.class.getName())) {
                return false;
            }

            // 数据库组件
            if (!ComponentManager.getInstance().addComponent(DBPoolComponent.class.getName())) {
                return false;
            }

            // redis组件
            if (!ComponentManager.getInstance().addComponent(RedisClientComponent.class.getName())) {
                return false;
            }

            // 缓存组件
            if (!ComponentManager.getInstance().addComponent(RemoteCacheComponent.class.getName())) {
                return false;
            }

            // 脚本组件
            if (!ComponentManager.getInstance().addComponent(ScriptComponent.class.getName())) {
                return false;
            }

            // web服务组件
            if (!ComponentManager.getInstance().addComponent(WebComponent.class.getName())) {
                return false;
            }

            // netty服务组件（tcp）
            if (!ComponentManager.getInstance().addComponent(MyServerNettyComponent.class.getName())) {
                return false;
            }

            if (GlobalConfigComponent.getConfig().server.isDebug) {
                Spy.start();
            }
            return ComponentManager.getInstance().start();
        } catch (Exception e) {
            log.error("loadComponent Exception: ", e);
        }
        return true;
    }


    public static void startMonitorThread() {
        NamedThreadFactory namedThreadFactory = new NamedThreadFactory("monitor-thread");
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), namedThreadFactory);
        executor.execute(() -> {
            try {
                while (true) {
                    byte[] reads = new byte[1024];
                    System.in.read(reads, 0, 1024);
                    String val = new String(reads).trim();
                    if ("q".equalsIgnoreCase(val)) {
                        byte[] reads1 = new byte[1024];
                        System.err.println("Y or N");
                        System.in.read(reads1, 0, 1024);
                        val = new String(reads1).trim();
                        if ("Y".equalsIgnoreCase(val)) {
                            GameServer.getInstance().stop();
                            break;
                        }
                    }

                    /*if (val.equalsIgnoreCase("reload")) {

                    }

                    if (val.equalsIgnoreCase("monitor")) {

                    }*/
                }
            } catch (Exception e) {
                log.error("shell输入异常：", e);
            }
        });
    }

    @Override
    public void stop() {
        try {
            log.info("MyServer Is Stopping.");
            ComponentManager.getInstance().stop();
        } catch (Exception e) {
            log.error("MyServer Stop Exception.", e);
        }
        Runtime.getRuntime().halt(0);
        // System.exit(0);
        log.info("MyServer Has Been Stopped.");
    }

    public static void printNoBug() {
        log.info(" ......................我佛慈悲......................");
        log.info("                       _oo0oo_                      ");
        log.info("                      o8888888o                     ");
        log.info("                      88\" . \"88                     ");
        log.info("                      (| -_- |)                     ");
        log.info("                      0\\  =  /0                     ");
        log.info("                    ___/‘---’\\___                   ");
        log.info("                  .' \\|       |/ '.                 ");
        log.info("                 / \\\\|||  :  |||// \\                ");
        log.info("                / _||||| -卍-|||||_ \\               ");
        log.info("               |   | \\\\\\  -  /// |   |              ");
        log.info("               | \\_|  ''\\---/''  |_/ |              ");
        log.info("               \\  .-\\__  '-'  ___/-. /              ");
        log.info("             ___'. .'  /--.--\\  '. .'___            ");
        log.info("          .\"\" ‘<  ‘.___\\_<|>_/___.’ >’ \"\".          ");
        log.info("         | | :  ‘- \\‘.;‘\\ _ /’;.’/ - ’ : | |        ");
        log.info("         \\  \\ ‘_.   \\_ __\\ /__ _/   .-’ /  /        ");
        log.info("     =====‘-.____‘.___ \\_____/___.-’___.-’=====     ");
        log.info("                       ‘=---=’                      ");
        log.info("                                                    ");
        log.info("....................佛祖开光 ,永无BUG...................");
    }

}
