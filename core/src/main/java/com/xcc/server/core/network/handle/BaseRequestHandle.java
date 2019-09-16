package com.xcc.server.core.network.handle;

import com.xcc.server.core.context.ServletContext;
import com.xcc.server.core.exception.FilterNotFoundException;
import com.xcc.server.core.exception.ResourceNotFoundException;
import com.xcc.server.core.exception.ServerErrorException;
import com.xcc.server.core.exception.ServletNotFoundException;
import com.xcc.server.core.exception.base.ServletException;
import com.xcc.server.core.exception.handle.ExceptionHandle;
import com.xcc.server.core.filter.Filter;
import com.xcc.server.core.filter.FilterChain;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.request.Request;
import com.xcc.server.core.resources.ResourceHandle;
import com.xcc.server.core.response.Response;
import com.xcc.server.core.servlet.Servlet;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 17:36.
 */

@Slf4j
@Getter
public abstract class BaseRequestHandle implements Runnable, FilterChain {
    protected Request request;
    protected Response response;
    protected SocketWrapper socketWrapper;
    protected ExceptionHandle exceptionHandle;
    protected ServletContext servletContext;
    protected ResourceHandle resourceHandle;
    protected boolean isFinished;
    protected Servlet servlet;
    protected List<Filter> filters;
    private int filterIndex = 0;

    public BaseRequestHandle(Request request, Response response, SocketWrapper socketWrapper, ServletContext servletContext, ExceptionHandle exceptionHandle, ResourceHandle resourceHandle) throws ServletNotFoundException, ResourceNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, FilterNotFoundException {
        this.request = request;
        this.response = response;
        this.exceptionHandle = exceptionHandle;
        this.resourceHandle = resourceHandle;
        this.isFinished = false;
        this.servletContext = servletContext;
        this.socketWrapper = socketWrapper;
        request.setServletContext(servletContext);
        request.setRequestHandle(this);
        response.setRequestHandle(this);
        //根据url查询匹配的servlet， 结果是1个或者0个
        servlet = servletContext.mapServlet(request.getUrl());
        // 根据url查询匹配的filter，结果是1个或者0个
        filters = servletContext.mapFilter(request.getUrl());
    }

    @Override
    public void run() {
        if(filters.isEmpty()){
            service();
        }else{
            doFilter(request, response);
        }
    }

    /**
     * 递归执行，自定义filter中如果同意放行，那么就会调用filterChain(也就是requestHandle())的doFilter方法
     * 此时会执行下一个filter中doFilter方法
     * 如果不放行就会在客户端使用sendRedirect将响应数据写回客户端，此次请求结束。
     * 如果所有过滤器都执行完毕，将会执行service()这个方法。
     * @param request
     * @param response
     */
    @Override
    public void doFilter(Request request, Response response) {
        if(filterIndex <= filters.size()){
            filters.get(filterIndex++).doFilter(request,response, this);
        }else{
            service();
        }
    }

    /**
     * 处理请求的方法
     */
    private void service(){
        try {
            // 处理动态资源。将请求发送到对应的servlet进行处理
            //  servlet是单例多线程
            // TODO 为什么servlet是单例多线程
            // 所有servlet在RequestHandle中执行。
            servlet.service(request,response);
        } catch (ServletException e) {
            exceptionHandle.handle(e, response, socketWrapper);
        } catch (IOException e) {
            exceptionHandle.handle(new ServerErrorException(), response, socketWrapper);
        }finally {
            if(!isFinished){
                flushResponse();
            }
        }
        log.info("请求处理完毕");
    }

    /**
     * 将请求写回客户端
     */
    public abstract void flushResponse();
}
