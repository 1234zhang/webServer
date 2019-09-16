package com.xcc.server.core.network.dispatchar;

import com.xcc.server.core.context.ServletContext;
import com.xcc.server.core.context.WebApplication;
import com.xcc.server.core.exception.handle.ExceptionHandle;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.resources.ResourceHandle;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 15:58.
 * 所有dispatchar（请求分发器)的父类
 */

public abstract class BaseDispatcher {
    protected ResourceHandle resourceHandle;
    protected ExceptionHandle exceptionHandle;
    protected ServletContext servletContext;
    protected ThreadPoolExecutor pool;

    public BaseDispatcher(){
        servletContext = WebApplication.getServletContext();
        exceptionHandle =new ExceptionHandle();
        resourceHandle = new ResourceHandle(exceptionHandle);
        ThreadFactory threadFactory = new ThreadFactory() {
            private int count = 0;

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "WorkThread-" + count++);
            }
        };
        this.pool = new ThreadPoolExecutor(100, 100, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 关闭
     */
    public void shutdown(){
        pool.shutdown();
        servletContext.destroy();
    }

    /**
     * 创建请求派生器
     * @param socketWrapper
     */
    protected abstract void doDispatcher(SocketWrapper socketWrapper);
}
