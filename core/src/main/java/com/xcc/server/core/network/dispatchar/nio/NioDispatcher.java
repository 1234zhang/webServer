package com.xcc.server.core.network.dispatchar.nio;

import com.xcc.server.core.exception.*;
import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.network.dispatchar.BaseDispatcher;
import com.xcc.server.core.network.handle.nio.NioRequestHandle;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.network.wrapper.nio.NioSocketWrapper;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Brandon.
 * @date 2019/9/16.
 * @time 21:12.
 */

@Getter
@Setter
@Slf4j
public class NioDispatcher extends BaseDispatcher{


    /**
     * 请求分发，io处理放在io线程中，不能放在线程池中，如果放在线程池中，会出现多个线程读取一个socket现象。
     * 1.读取数据，
     * 2.组装request，response
     * 3.将request，response放入线程池中处理
     */
    @Override
    public void doDispatcher(SocketWrapper socketWrapper) {
        NioSocketWrapper wrapper = (NioSocketWrapper) socketWrapper;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        log.info("已经将请求线程放在worker工作线程中");
        log.info("开始处理");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Response response = null;
        Request request = null;
        try {
            while(wrapper.getSocket().read(buffer) > 0){
                buffer.flip();
                baos.write(buffer.array());
            }
            baos.close();
            response = new Response();
            request = new Request(baos.toByteArray());
            pool.execute(new NioRequestHandle(request, response,wrapper, servletContext, exceptionHandle,resourceHandle));
        } catch (IOException e) {
            e.printStackTrace();
            exceptionHandle.handle(new ServerErrorException(), response,  wrapper);
        } catch (ServletException e) {
            e.printStackTrace();
            exceptionHandle.handle(e, response, wrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
