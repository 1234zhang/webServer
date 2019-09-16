package com.xcc.server.core.context.holder;

import com.xcc.server.core.servlet.Servlet;
import lombok.Data;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 23:52.
 */

@Data
public class ServletHolder {
    private Servlet servlet;
    private String servletClass;

    public ServletHolder(String servletClass){
        this.servletClass = servletClass;
    }
}
