package com.xcc.server.core.servlet.lmp;

import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.servlet.Servlet;
import com.xcc.server.core.statusenum.RequestMethod;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 19:41.
 */

public abstract class HttpServlet implements Servlet {
    @Override
    public void init() {

    }

    @Override
    public void destory() {

    }

    @Override
    public void service(Request request, Response response) throws ServletException, IOException {
        if(request.getMethod() == RequestMethod.GET){
            doGet(request, response);
        }
        if(request.getMethod() == RequestMethod.POST){
            doPost(request, response);
        }
        if(request.getMethod() == RequestMethod.DELETE){
            doDelete(request,response);
        }
        if(request.getMethod() == RequestMethod.PUT){
            doPut(request,response);
        }
    }

    public void doGet(Request request, Response response) throws IOException, ServletException{

    }

    public void doPost(Request request, Response response) throws IOException, ServletException{

    }

    public void doDelete(Request request, Response response) throws IOException, ServletException{

    }
    public void doPut(Request request, Response response) throws IOException, ServletException{

    }
}
