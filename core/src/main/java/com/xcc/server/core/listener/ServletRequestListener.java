package com.xcc.server.core.listener;

import com.xcc.server.core.listener.event.ServletRequestEvent;

import java.util.EventListener;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 22:42.
 * 请求层面上的监听器
 */

public interface ServletRequestListener extends EventListener{
    /**
     * 初始化servletRequestListener 监听器
     * @param sre
     */
    void requestInitialized(ServletRequestEvent sre);
    /**
     * 销毁监听器
     * @param sre
     */
    void requestDestroyed(ServletRequestEvent sre);

}
