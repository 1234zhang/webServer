package com.xcc.server.core.network.connector.nio;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author Brandon.
 * @date 2019/9/16.
 * @time 21:13.
 */

@Slf4j
public class IdeConnectionCleaner implements Runnable{
    private ScheduledExecutorService executor;
    private List<NioPoller> pollers;

    public IdeConnectionCleaner(List<NioPoller> pollers){
        this.pollers = pollers;
    }

    public void start(){
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"IdeConnectionCleaner");
            }
        };
        executor = Executors.newSingleThreadScheduledExecutor(factory);
        executor.scheduleWithFixedDelay(this, 0, 5, TimeUnit.SECONDS);
    }

    public void shutdown(){
        executor.shutdown();
    }
    @Override
    public void run() {
        pollers.forEach(poller ->{
            log.info("Cleaner 检测 poller ： {} 中的socket是否过期。。。。", poller.getPollerName());
            poller.cleanTimeOutSockets();
        });
        log.info("检查完成。。。。");
    }
}
