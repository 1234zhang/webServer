package com.xcc.client.example.listener;

import com.xcc.server.core.listener.HttpSessionListener;
import com.xcc.server.core.listener.ServletContextListener;
import com.xcc.server.core.listener.event.HttpSessionEvent;
import com.xcc.server.core.listener.event.ServletContextEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 21:21.
 */


@Slf4j
public class ServletContextAndSessionListener implements ServletContextListener, HttpSessionListener {
    @Override
    public void createSession(HttpSessionEvent httpSessionListener) {
        log.info("session create....");
    }

    @Override
    public void destorySessioni(HttpSessionEvent httpSessionEvent) {
        log.info("session destroy....");
    }

    @Override
    public void servletContextInitialized(ServletContextEvent contextEvent) {
    log.info("servlet context init......");
    }

    @Override
    public void servletContextDestroy(ServletContextEvent contextEvent) {
        log.info("servlet context destroy .....");
    }
}
