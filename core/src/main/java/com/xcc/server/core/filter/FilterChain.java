package com.xcc.server.core.filter;

import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 17:37.
 */

public interface FilterChain {
    void doFilter(Request request, Response response);
}
