package com.xcc.server.core.filter;

import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 17:45.
 */

public interface Filter {
    /**
     * 过滤器初始化
     */
    void init();

    void doFilter(Request request, Response response, FilterChain filterChain);

    void destory();
}
