package com.xcc.server.core.network.wrapper.nio;

import com.xcc.server.core.network.connector.nio.NioPoller;
import com.xcc.server.core.network.endpoint.nio.NioEndPoint;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author Brandon.
 * @date 2019/9/9.
 * @time 23:54.
 */

@Data
public class NioSocketWrapper implements SocketWrapper {

    private final NioEndPoint server;
    private final SocketChannel socket;
    private final NioPoller poller;
    private final boolean isNewSocket;
    private volatile long waitBegin;
    private volatile boolean isWorking;

    public NioSocketWrapper(NioEndPoint server, SocketChannel socket, NioPoller poller, boolean isNewSocket){
        this.server = server;
        this.poller = poller;
        this.socket = socket;
        this.isNewSocket = isNewSocket;
        this.isWorking = false;
    }
    @Override
    public void close() throws IOException {
        socket.keyFor(poller.getSelector()).cancel();
        socket.close();
    }

    @Override
    public String toString(){
        return this.socket.toString();
    }
}
