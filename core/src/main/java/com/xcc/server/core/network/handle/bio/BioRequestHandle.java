package com.xcc.server.core.network.handle.bio;

import com.xcc.server.core.context.ServletContext;
import com.xcc.server.core.context.WebApplication;
import com.xcc.server.core.exception.FilterNotFoundException;
import com.xcc.server.core.exception.ResourceNotFoundException;
import com.xcc.server.core.exception.ServletNotFoundException;
import com.xcc.server.core.exception.handle.ExceptionHandle;
import com.xcc.server.core.network.handle.BaseRequestHandle;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.network.wrapper.bio.BioSocketWrapper;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.resources.ResourceHandle;
import com.xcc.server.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 16:46.
 */

@Slf4j
public class BioRequestHandle extends BaseRequestHandle {
    public BioRequestHandle(Request request, Response response, SocketWrapper socketWrapper, ServletContext servletContext, ExceptionHandle exceptionHandle, ResourceHandle resourceHandle) throws ServletNotFoundException, ResourceNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, FilterNotFoundException {
        super(request, response, socketWrapper, servletContext, exceptionHandle, resourceHandle);
    }

    @Override
    public void flushResponse() {
        isFinished = true;
        BioSocketWrapper biosocketWrapper = (BioSocketWrapper) socketWrapper;
        byte[] result = response.getResponseByte();
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(biosocketWrapper.getSocket().getOutputStream());
            os.write(result);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            log.info("报错了！！！");
        }finally {
            try {
                os.close();
                biosocketWrapper.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        WebApplication.getServletContext().afterRequestDestroyed(request);
    }
}
