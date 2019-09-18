package com.xcc.server.core.network.connector.nio;

import com.xcc.server.core.network.endpoint.nio.NioEndPoint;
import com.xcc.server.core.network.wrapper.SocketWrapper;
import com.xcc.server.core.network.wrapper.nio.NioSocketWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Brandon.
 * @date 2019/9/16.
 * @time 21:12.
 * 每一个poller线程中维护一个selector，在连接中存在不止一个selector（包括了检测是否超时的selector）
 * 在poller线程中维护的是一个主selector。poller是整个nio实现的主线程。
 *
 */

@Data
@Slf4j
public class NioPoller implements Runnable{
    private Selector selector;
    private NioEndPoint nioEndPoint;
    private Queue<PollerEvent> events;
    private Map<SocketChannel, NioSocketWrapper> sockets;
    private String pollerName;

    public NioPoller(NioEndPoint nioEndPoint, String pollerName) throws IOException {
        this.selector = Selector.open();
        this.nioEndPoint = nioEndPoint;
        this.events = new ConcurrentLinkedQueue<PollerEvent>();
        this.sockets = new ConcurrentHashMap<>();
        this.pollerName = pollerName;
    }

    /**
     * 将一个socketChannel存放到poller之中。
     * 在这里初始化waitBegin
     * @param socketChannel
     * @param isNewSocket
     */
    public void register(SocketChannel socketChannel, boolean isNewSocket){
        NioSocketWrapper wrapper;
        log.info("将Acceptor连接到的socketChannel 推入{} 中的queue中", pollerName);
        if(isNewSocket){
            wrapper = new NioSocketWrapper(nioEndPoint, socketChannel, this, isNewSocket);
            sockets.put(socketChannel, wrapper);
        }else{
            wrapper = sockets.get(socketChannel);
            wrapper.setWorking(false);
        }
        wrapper.setWaitBegin(System.currentTimeMillis());
        events.offer(new PollerEvent(wrapper));
        // 当某个selector调用select之后阻塞，即使没有通道就绪，也有办法使得从select()中返回
        // 只要其他线程在第一个线程调用select()方法的对象上调用selector.wakeup()方法，就会唤醒阻塞线程。
        selector.wakeup();
    }

    public void close() throws IOException {
        for(NioSocketWrapper wrapper : sockets.values()){
            wrapper.close();
        }
        sockets.clear();
        selector.close();
    }
    @Override
    public void run() {
        log.info("{} 开始工作", Thread.currentThread().getName());
        while(nioEndPoint.isRunning()){
            try {
                events();
                if(selector.select() == 0){
                    continue;
                }
                log.info("selector.select()接收到通道数据，开启所有消息监听器");
                // 遍历获取当前所有的监听事件。
                for(Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();){
                    SelectionKey key = it.next();
                    // 判断”接收“事件是否就绪
                    if(key.isReadable()){
                        // 如果接收事件已经就绪，将这个事件交给接收处理器处理
                        log.info("serverSocket 事件已经准备就绪，开始接收");
                        NioSocketWrapper attachment = (NioSocketWrapper)key.attachment();
                        if(attachment != null){
                            process(attachment);
                        }
                    }
                    it.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }catch (ClosedSelectorException e){
                log.info("{} 对应的selector 已经关闭", pollerName);
            }
        }
    }

    private void process(NioSocketWrapper attachment){
        attachment.setWorking(true);
        nioEndPoint.executor(attachment);
    }
    public void cleanTimeOutSockets(){
        for (Iterator<Map.Entry<SocketChannel, NioSocketWrapper>> it = sockets.entrySet().iterator(); it.hasNext();){
            NioSocketWrapper wrapper = it.next().getValue();
            log.info("缓存中的socket : {}", wrapper);
            if(!wrapper.getSocket().isConnected()){
                log.info("该 socket {} 已经断开连接", wrapper.getSocket());
                it.remove();
                continue;
            }
            if(wrapper.isWorking()){
                log.info("该socket {} 正在工作， 不给予关闭 ", wrapper.getSocket());
                continue;
            }
            if(System.currentTimeMillis() - wrapper.getWaitBegin() >= nioEndPoint.getKeepAliveTimeout()){
                log.info("{} keepAlive 已经过期，将会被清除", wrapper.getSocket());
                try {
                    wrapper.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                it.remove();
            }
        }
    }
    private void events(){
        log.info("Queue 大小为 {}, 清空Queue，将连接到的socketChannel注册到selector中", events.size());
        PollerEvent pollerEvent;
        for (int i = 0, size = events.size(); i < size && (pollerEvent = events.poll()) != null; i++){
            pollerEvent.run();
        }
    }

    @Data
    @AllArgsConstructor
    class PollerEvent implements Runnable{
        private NioSocketWrapper wrapper;

        @Override
        public void run() {
            log.info("将socketChannel读事件注册到poller中");
            try{
                if(wrapper.getSocket().isOpen()){
                    wrapper.getSocket().register(wrapper.getPoller().getSelector(), SelectionKey.OP_READ, wrapper);
                }else{
                    log.error("{} socket channel 已经关闭, 无法注册到poller", wrapper.getSocket());
                }
            }catch(ClosedChannelException e){
                log.error("关闭socketChannel出错。。。。");
                e.printStackTrace();
            }
        }
    }
}
