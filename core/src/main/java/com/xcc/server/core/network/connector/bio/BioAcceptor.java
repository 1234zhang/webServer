package com.xcc.server.core.network.connector.bio;

import com.xcc.server.core.network.dispatchar.BaseDispatcher;
import com.xcc.server.core.network.dispatchar.bio.BioDispatcher;
import com.xcc.server.core.network.endpoint.bio.BioEndPoint;
import com.xcc.server.core.network.wrapper.bio.BioSocketWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 17:01.
 */

@Slf4j
public class BioAcceptor implements Runnable{
    private BioEndPoint server;
    private BioDispatcher dispatcher;

    public BioAcceptor(BioEndPoint server, BaseDispatcher dispatcher){
        this.server = server;
        this.dispatcher = (BioDispatcher) dispatcher;
    }

    @Override
    public void run() {
        log.info("开始监听");
        while(server.isRunning()){
            Socket client = null;
            try {
                client = server.getAcceptor();
                log.info("client : {}", client);
                dispatcher.doDispatcher(new BioSocketWrapper(client));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
