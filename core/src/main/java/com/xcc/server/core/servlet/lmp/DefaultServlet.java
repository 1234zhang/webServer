package com.xcc.server.core.servlet.lmp;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.statusenum.RequestMethod;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 19:49.
 */

public class DefaultServlet extends HttpServlet{
    @Override
    public void service(Request request, Response response) throws ServletException, IOException {
        if(request.getMethod() == RequestMethod.GET){
            if(request.getUrl().equals("/")){
                request.setUrl("/index.html");
            }
            request.getRequestDispatcher(request.getUrl()).forward(request,response);
        }
    }
}
