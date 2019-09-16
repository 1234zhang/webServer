package com.xcc.server.core.session;

import com.xcc.server.core.context.WebApplication;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 9:16.
 */

@Slf4j
public class IdleSessionCleaner implements Runnable {

    private ScheduledExecutorService executor;

    public IdleSessionCleaner(){
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"IdleSessionCleaner");
            }
        };
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void run() {
        log.info("开始扫描过期session");
        WebApplication.getServletContext().cleanIdSession();
        log.info("扫描结束");
    }
}
