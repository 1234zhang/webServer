package com.xcc.server.core.network.handle.aio;

import com.xcc.server.core.context.ServletContext;
import com.xcc.server.core.exception.FilterNotFoundException;
import com.xcc.server.core.exception.ResourceNotFoundException;
import com.xcc.server.core.exception.ServletNotFoundException;
import com.xcc.server.core.exception.handle.ExceptionHandle;
import com.xcc.server.core.network.handle.BaseRequestHandle;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.resources.ResourceHandle;
import com.xcc.server.core.response.Response;

/**
 * @author Brandon.
 * @date 2019/9/19.
 * @time 15:07.
 */

public class AioRequestHandle extends BaseRequestHandle {
    public AioRequestHandle(Request request, Response response, SocketWrapper socketWrapper, ServletContext servletContext, ExceptionHandle exceptionHandle, ResourceHandle resourceHandle) throws ServletNotFoundException, ResourceNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, FilterNotFoundException {
        super(request, response, socketWrapper, servletContext, exceptionHandle, resourceHandle);
    }

    @Override
    public void flushResponse() {

    }
}
