package com.xcc.server.core.network.connector.nio;

import com.xcc.server.core.network.endpoint.nio.NioEndPoint;
import com.xcc.server.core.network.wrapper.nio.NioSocketWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author Brandon.
 * @date 2019/9/16.
 * @time 21:13.
 * 接收socket的线程
 */

@Data
@Slf4j
public class NioAcceptor implements Runnable{
    private NioEndPoint nioEndPoint;

    public NioAcceptor(NioEndPoint nioEndPoint){
        this.nioEndPoint = nioEndPoint;
    }

    @Override
    public void run() {
        log.info("{} 开始监听",Thread.currentThread().getName());
        while(nioEndPoint.isRunning()){
            SocketChannel channel;
            try {
                channel = nioEndPoint.accept();
                if(channel == null){
                    continue;
                }
                channel.configureBlocking(false);
                log.info("接收到一个连接 {} ", channel);
                nioEndPoint.registerToPoller(channel);
                log.info("socketWrapper : {} ", channel);
            } catch (IOException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
