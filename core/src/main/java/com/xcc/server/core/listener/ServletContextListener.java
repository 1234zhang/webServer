package com.xcc.server.core.listener;

import com.xcc.server.core.listener.event.ServletContextEvent;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 23:08.
 * 应用层上的监听对象
 */

public interface ServletContextListener extends EventListener {
    /**
     * 创建ServletContext监听对象
     * @param contextEvent
     */
    void servletContextInitialized(ServletContextEvent contextEvent);

    /**
     * 销毁对象
     * @param contextEvent
     */
    void servletContextDestroy(ServletContextEvent contextEvent);

}
