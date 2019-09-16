package com.xcc.server.core.context;

import org.dom4j.DocumentException;

/**
 * @author Brandon.
 * @date 2019/9/8.
 * @time 9:22.
 * 持有静态servletContext， 保持ServletContext在项目启动的时候就被加载
 */

public class WebApplication {
    private static ServletContext servletContext;

    static{
        try {
            servletContext = new ServletContext();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public static ServletContext getServletContext(){
        return servletContext;
    }
}
