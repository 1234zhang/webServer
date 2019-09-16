package com.xcc.server.core.network.endpoint.bio;

import com.xcc.server.core.network.connector.bio.BioAcceptor;
import com.xcc.server.core.network.dispatchar.bio.BioDispatcher;
import com.xcc.server.core.network.endpoint.BaseEndPoint;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Brandon.
 * @date 2019/9/7.
 * @time 15:53.
 *
 * BIO网络传输模块的入口
 */

@Slf4j
@Getter
public class BioEndPoint extends BaseEndPoint  {

    private ServerSocket server;
    private BioDispatcher dispatcher;
    private BioAcceptor acceptor;
    private volatile boolean isRunning = true;


    @Override
    public void start(int port) {
        dispatcher = new BioDispatcher();
        try {
            server = new ServerSocket(port);
            initAcceptor();
            log.info("启动服务器。。。。。。。");
        } catch (IOException e) {
            log.info("服务器启动失败。。。。。。");
            e.printStackTrace();
            close();
        }

    }

    private void initAcceptor(){
        acceptor = new BioAcceptor(this, dispatcher);
        Thread t = new Thread(acceptor);
        t.setDaemon(true);
        t.start();
    }
    @Override
    public void close() {
        isRunning = false;
        dispatcher.shutdown();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getAcceptor() throws IOException {
        return server.accept();
    }

    public boolean isRunning(){
        return isRunning;
    }
}
