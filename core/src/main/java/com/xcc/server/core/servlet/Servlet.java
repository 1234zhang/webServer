package com.xcc.server.core.servlet;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 17:42.
 */

public interface Servlet {
    void init();

    void destory();

    void service(Request request, Response response) throws ServletException, IOException;
}
