package com.xcc.server.core.network.endpoint.nio;

import com.xcc.server.core.network.connector.nio.IdeConnectionCleaner;
import com.xcc.server.core.network.connector.nio.NioAcceptor;
import com.xcc.server.core.network.connector.nio.NioPoller;
import com.xcc.server.core.network.dispatchar.nio.NioDispatcher;
import com.xcc.server.core.network.endpoint.BaseEndPoint;
import com.xcc.server.core.network.wrapper.nio.NioSocketWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Brandon.
 * @date 2019/9/16.
 * @time 21:11.
 */

@Getter
@Setter
@Slf4j
public class NioEndPoint extends BaseEndPoint {
    private volatile boolean  isRunning = true;
    private int pollerCount = Math.min(2, Runtime.getRuntime().availableProcessors());
    private NioAcceptor acceptor;
    private NioDispatcher dispatcher;
    private List<NioPoller> pollers;
    private ServerSocketChannel server;
    /**
     * poller 轮询器
     */
    private AtomicInteger pollerRotater = new AtomicInteger(0);
    /**
     * 设置过时事件
     */
    private long keepAliveTimeout = 6 * 1000L;

    /**
     * 如果长时间没有数据传输，则关闭连接
     */
    private IdeConnectionCleaner cleaner;

    private void initDispatcher(){
        this.dispatcher = new NioDispatcher();
    }
    private void initServerSocket(int port) throws IOException {
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(port));
        server.configureBlocking(true);
    }

    private void initPoller() throws IOException {
        pollers = new ArrayList<>(pollerCount);
        for (int i = 0; i < pollerCount; i++) {
            String pollerName = "NioPoller-" + i;
            NioPoller poller = new NioPoller(this, pollerName);
            Thread t = new Thread(poller, pollerName);
            t.setDaemon(true);
            t.start();
            pollers.add(poller);
        }
    }

    private void initAcceptor(){
        this.acceptor = new NioAcceptor(this);
        Thread t = new Thread(acceptor,"NioAcceptor");
        t.setDaemon(true);
        t.start();
    }

    private void initCleaner(){
        this.cleaner = new IdeConnectionCleaner(pollers);
        cleaner.start();
    }
    @Override
    public void start(int port) throws IOException {
        log.info("启动服务器");
        try {
            initDispatcher();
            initServerSocket(port);
                initCleaner();
            initPoller();
            initAcceptor();
            log.info("服务器初始化完毕。。。。。。。");
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }


    /**
     * 调用dispatcher 处理这个已经读的socketChannel
     * @param attachment
     */
    public void executor(NioSocketWrapper attachment){
        dispatcher.doDispatcher(attachment);
    }

    /**
     * 使用阻塞的方式接收socket channel
     * @return
     * @throws IOException
     */
    public SocketChannel accept() throws IOException {
        return server.accept();
    }

    /**
     * 关闭客户端
     */
    @Override
    public void close() {
        isRunning = false;
        cleaner.shutdown();
        pollers.forEach(poller -> {
            try {
                poller.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try{
            server.close();
            dispatcher.shutdown();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 使用轮询的方式返回一个poller对象，实现负载均衡。
     * @return
     */
    public NioPoller getPoller(){
        int id = Math.abs(pollerRotater.incrementAndGet() % pollers.size());
        return pollers.get(id);
    }

    public boolean isRunning(){
        return this.isRunning;
    }

    /**
     * 通过轮询获取到一个poller， 将接收到的socketChannel注册到该poller的queue中
     * @param socketChannel
     * @throws IOException
     */
    public void registerToPoller(SocketChannel socketChannel) throws IOException {
        server.configureBlocking(false);
        getPoller().register(socketChannel,true);
        server.configureBlocking(true);
    }

    public long getKeepAliveTimeout(){
        return this.keepAliveTimeout;
    }
}
