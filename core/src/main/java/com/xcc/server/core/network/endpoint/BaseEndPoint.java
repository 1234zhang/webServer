package com.xcc.server.core.network.endpoint;

import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 15:37.
 */

public abstract class BaseEndPoint {

    /**
     * 启动服务器
     * @param port 服务器端口号
     */
    public abstract void start( int port) throws IOException;

    /**
     * 关闭服务器
     */
    public abstract void close();

    /**
     * 根据传入的nio、 bio、 aio获取到对应的实列
     * @param connector
     * @return
     */
    public static BaseEndPoint getInstance(String connector){
        StringBuilder sb = new StringBuilder();
        // 找到对应io的类，并获取这个类的实列
        sb.append("com.xcc.server.core.network.endpoint")
                .append(".")
                .append(connector)
                .append(".")
                //capitalize 将首字母大写
                .append(StringUtils.capitalize(connector))
                .append("EndPoint");
        try {
            return (BaseEndPoint) Class.forName(sb.toString()).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException(connector + "不存在");
    }
}
