package com.xcc.server.core.network.wrapper.bio;

import com.xcc.server.core.network.wrapper.SocketWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Brandon.
 * @date 2019/9/10.
 * @time 16:18.
 */

@Slf4j
@Data
@AllArgsConstructor
public class BioSocketWrapper implements SocketWrapper {
    private Socket socket;

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
