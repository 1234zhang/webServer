package com.xcc.server.core;

import com.xcc.server.core.network.endpoint.BaseEndPoint;
import com.xcc.server.core.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;


/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 10:32.
 * 启动类
 */

@Slf4j
public class BootStrap {
    public static void run() throws IOException {
        String port = PropertiesUtil.getProperty("server.port");
        if(port == null){
            throw new IllegalArgumentException("server.port 不存在-.-");
        }
        String connector = PropertiesUtil.getProperty("server.connector");
        if(connector == null || (!connector.equalsIgnoreCase("bio") && !connector.equalsIgnoreCase("nio") && !connector.equalsIgnoreCase("aio"))){
            throw new IllegalArgumentException("network方式不存在！！");
        }
        BaseEndPoint server = BaseEndPoint.getInstance(connector);
        server.start(Integer.parseInt(port));
        Scanner scanner = new Scanner(System.in);
        String order;
        while(scanner.hasNext()){
            order = scanner.next();
            if(order.equals("EXIT")){
                server.close();
                System.exit(0);
            }
        }
    }
}
