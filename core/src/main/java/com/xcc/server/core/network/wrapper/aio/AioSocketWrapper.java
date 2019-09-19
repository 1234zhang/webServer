package com.xcc.server.core.network.wrapper.aio;

import com.xcc.server.core.network.wrapper.SocketWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * @author Brandon.
 * @date 2019/9/19.
 * @time 15:06.
 */

@Data
@Slf4j
public class AioSocketWrapper implements SocketWrapper {
    private AsynchronousSocketChannel socketChannel;
    @Override
    public void close() throws IOException {

    }
}
