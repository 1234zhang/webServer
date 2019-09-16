package com.xcc.server.core.resources;

import com.xcc.server.core.contant.CharsetContant;
import com.xcc.server.core.exception.RequestParseException;
import com.xcc.server.core.exception.ResourceNotFoundException;
import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.exception.handle.ExceptionHandle;
import com.xcc.server.core.network.wrapper.nio.NioSocketWrapper;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.template.TemplateResolver;
import com.xcc.server.core.util.IOUtil;
import com.xcc.server.core.util.MineTypeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 16:09.
 */

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResourceHandle {
    private ExceptionHandle exceptionHandle;
    public void handle(Request request, Response response, NioSocketWrapper nioSocketWrapper){
        String url = request.getUrl();
        try{
            if(ResourceHandle.class.getResource(url) == null){
                throw new ResourceNotFoundException();
            }
            byte[] body = IOUtil.getBytesFromFile(url);
            if(url.endsWith(".html")){
                body = TemplateResolver.
                        resolve(new String(body, CharsetContant.UTF_8_CHARSET), request).
                        getBytes(CharsetContant.UTF_8_CHARSET);
            }
            response.setContentType(MineTypeUtil.getTypes(url));
            response.setBody(body);
        }catch(ServletException e){
            exceptionHandle.handle(e, response, nioSocketWrapper);
        } catch (IOException e) {
            exceptionHandle.handle(new RequestParseException(), response, nioSocketWrapper);
        }
    }
}
