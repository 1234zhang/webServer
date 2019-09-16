package com.xcc.server.core.listener.event;

import com.xcc.server.core.context.ServletContext;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 23:05.
 */

public class ServletContextEvent extends java.util.EventObject {

    private static final long serialVersionUID = -3825299946702723859L;

    public ServletContextEvent(ServletContext context){
        super(context);
    }

    public ServletContext getContext(){
        return (ServletContext) super.getSource();
    }
}
