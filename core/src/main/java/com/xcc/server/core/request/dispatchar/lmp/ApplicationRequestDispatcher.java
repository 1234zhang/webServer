package com.xcc.server.core.request.dispatchar.lmp;

import com.xcc.server.core.contant.CharsetContant;
import com.xcc.server.core.exception.ResourceNotFoundException;
import com.xcc.server.core.exception.TemplateResolveException;
import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.request.dispatchar.BaseRequestDispatcher;
import com.xcc.server.core.resources.ResourceHandle;
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
 * @date 2019/9/8.
 * @time 18:08.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ApplicationRequestDispatcher implements BaseRequestDispatcher {
    private String url;
    @Override
    public void forward(Request request, Response response) throws ServletException, IOException {
        if(ResourceHandle.class.getResource(url) == null){
            throw new ResourceNotFoundException();
        }
        log.info("forword 至{} 页面", url);
        String body = TemplateResolver.resolve(new String(IOUtil.getBytesFromFile(url), CharsetContant.UTF_8_CHARSET), request);
        response.setContentType(MineTypeUtil.getTypes(url));
        response.setBody(body.getBytes(CharsetContant.UTF_8_CHARSET));
    }
}
