package com.xcc.server.core.listener.event;

import com.xcc.server.core.request.Request;
import com.xcc.server.core.session.HttpSession;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 23:01.
 */

public class HttpSessionEvent extends java.util.EventObject {
    private static final long serialVersionUID = -7480134772165083275L;


    public HttpSessionEvent(HttpSession session){
        super(session);
    }

    public HttpSession getSession(){
        return (HttpSession) super.getSource();
    }
}
