package com.xcc.client.example.listener;

import com.xcc.server.core.listener.ServletRequestListener;
import com.xcc.server.core.listener.event.ServletRequestEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 21:22.
 */

@Slf4j
public class MyServletRequestListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        log.info("request init....");
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        log.info("request destroy.....");
    }
}
