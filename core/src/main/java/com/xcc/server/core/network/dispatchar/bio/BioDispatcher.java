package com.xcc.server.core.network.dispatchar.bio;

import com.xcc.server.core.exception.RequestInvalidException;
import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.network.dispatchar.BaseDispatcher;
import com.xcc.server.core.network.handle.bio.BioRequestHandle;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.network.wrapper.bio.BioSocketWrapper;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 16:22.
 */

@Slf4j
public class BioDispatcher extends BaseDispatcher {

    @Override
    public void doDispatcher(SocketWrapper socketWrapper) {
        BioSocketWrapper bioSocketWrapper = (BioSocketWrapper)socketWrapper;
        Socket socket = bioSocketWrapper.getSocket();
        Request request = null;
        Response response = null;
        try {
            BufferedInputStream bin = new BufferedInputStream(socket.getInputStream());
            byte[] buf = null;
            try{
                buf = new byte[bin.available()];
                int len = bin.read(buf);
                if(len < 1){
                    throw new RequestInvalidException();
                }
            }catch(IOException e){
                log.info("请求流发生错误");
                log.error("错误是：{}", e.getMessage());
            }
            // 这里不能关闭input流，如果关闭相当于关闭服务器
            response = new Response();
            request = new Request(buf);
            pool.execute(new BioRequestHandle(request, response, bioSocketWrapper,servletContext,exceptionHandle,resourceHandle));
        } catch (IOException e) {
            e.printStackTrace();
        } catch(ServletException e){
            exceptionHandle.handle(e, response, bioSocketWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
