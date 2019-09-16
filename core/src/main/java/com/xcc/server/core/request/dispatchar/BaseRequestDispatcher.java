package com.xcc.server.core.request.dispatchar;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:35.
 */

public interface BaseRequestDispatcher {
    void forward(Request request, Response response) throws ServletException, IOException;

}
