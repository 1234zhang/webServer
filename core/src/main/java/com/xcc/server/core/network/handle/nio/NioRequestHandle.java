package com.xcc.server.core.network.handle.nio;

import com.xcc.server.core.context.ServletContext;
import com.xcc.server.core.context.WebApplication;
import com.xcc.server.core.exception.*;
import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.exception.handle.ExceptionHandle;
import com.xcc.server.core.network.handle.BaseRequestHandle;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.network.wrapper.nio.NioSocketWrapper;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.resources.ResourceHandle;
import com.xcc.server.core.response.Response;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Brandon.
 * @date 2019/9/17.
 * @time 8:50.
 */

@Getter
@Setter
@Slf4j
public class NioRequestHandle extends BaseRequestHandle {
    public NioRequestHandle(Request request, Response response, SocketWrapper socketWrapper, ServletContext servletContext, ExceptionHandle exceptionHandle, ResourceHandle resourceHandle) throws ServletNotFoundException, ResourceNotFoundException, FilterNotFoundException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        super(request, response, socketWrapper, servletContext, exceptionHandle, resourceHandle);
    }

    /**
     * 写入之后会根据请求头connection来判断是否连接，如果还有连接则注册到poller
     */
    @Override
    public void flushResponse() {
        isFinished = true;
        NioSocketWrapper wrapper = (NioSocketWrapper) socketWrapper;
        ByteBuffer[] buffer = response.getResponseByteBuffer();
        try {
            wrapper.getSocket().write(buffer);
            List<String> result = request.getHeaders().get("Connection");
            if(result != null && "close".equals(result.get(0))){
                log.info("CLOSE : 客户端 {} 连接已经关闭", wrapper.getSocket());
                wrapper.getSocket().close();
            }else{
                log.info("KEEP-ALIVE : 将客户端 {} 注册到poller中 ", wrapper.getSocket());
                wrapper.getPoller().register(wrapper.getSocket(), false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        WebApplication.getServletContext().afterRequestDestroyed(request);
    }
}
