package com.xcc.server.core.listener.event;

import com.xcc.server.core.context.ServletContext;
import com.xcc.server.core.request.Request;

import java.io.Serializable;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 22:46.
 */

public class ServletRequestEvent extends java.util.EventObject{
    private static final long serialVersionUID = 520357854831551279L;

    private final transient Request request;

    public ServletRequestEvent(ServletContext sc, Request request){
        super(sc);
        this.request = request;
    }

    public Request getRequest(){
        return this.request;
    }

    public ServletContext getContext(){
        return (ServletContext) super.getSource();
    }
}
