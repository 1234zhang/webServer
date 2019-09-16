package com.xcc.server.core.exception.handle;

import com.xcc.server.core.contant.ContextConstant;
import com.xcc.server.core.exception.RequestInvalidException;
import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.response.Header;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.util.IOUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:10.
 * 异常处理器，会根据异常处理http statue 设置response的状态以及相应的错误界面
 */

@Slf4j
public class ExceptionHandle {

    public void handle(ServletException e, Response response, SocketWrapper socketWrapper) {
        try {
            if (e instanceof RequestInvalidException) {
                log.info("请求无效，丢弃");
                socketWrapper.close();
            }else{
                log.info("抛出异常 : {}", e.getClass().getName());
                e.printStackTrace();
                response.addHeader(new Header("Connect", "close"));
                response.setStatus(e.getStatus());
                response.setBody(IOUtil.getBytesFromFile(String.format(ContextConstant.ERROR_PAGE,String.valueOf(e.getStatus().getCode()))));
            }
        }catch (IOException exception){
            exception.printStackTrace();
        }
    }
}
