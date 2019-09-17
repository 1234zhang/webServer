package com.xcc.server.core.network.endpoint.nio;

import com.xcc.server.core.network.endpoint.BaseEndPoint;
import com.xcc.server.core.network.wrapper.nio.NioSocketWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Brandon.
 * @date 2019/9/16.
 * @time 21:11.
 */

@Getter
@Setter
@Slf4j
@AllArgsConstructor
public class NioEndPoint extends BaseEndPoint {
    boolean isRunning;
    private long keepAliveTimeout;


    @Override
    public void start(int port) {

    }

    public void executor(NioSocketWrapper attachment){

    }

    @Override
    public void close() {

    }
}
