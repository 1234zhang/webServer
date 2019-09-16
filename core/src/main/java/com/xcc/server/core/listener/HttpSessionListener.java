package com.xcc.server.core.listener;

import com.xcc.server.core.listener.event.HttpSessionEvent;

import java.util.EventListener;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 22:59.
 */

public interface HttpSessionListener extends EventListener {
    /**
     * 创建session监听对象
     * @param httpSessionListener
     */
    void createSession(HttpSessionEvent httpSessionListener);

    /**
     * 销毁session监听对象
     * @param httpSessionEvent
     */
    void destorySessioni(HttpSessionEvent httpSessionEvent);
}
