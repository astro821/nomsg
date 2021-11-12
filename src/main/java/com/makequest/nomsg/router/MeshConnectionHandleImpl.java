package com.makequest.nomsg.router;

import com.makequest.nomsg.router.client.TcpClientConnManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MeshConnectionHandleImpl implements MeshConnectionHandle {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final class Creator {
        private static final MeshConnectionHandleImpl inst = new MeshConnectionHandleImpl();
    }

    private List<MeshConnectionEventListener> listeners = new ArrayList<>();
    private LinkedBlockingQueue<NoMsgFrame> rcvQueue = new LinkedBlockingQueue<>();

    private MeshConnectionHandleImpl() {
        initComsumer();
    }

    public static MeshConnectionHandleImpl getInstance() {
        return Creator.inst;
    }

    public void addNoMsgFrame(NoMsgFrame frame) {
        rcvQueue.add(frame);
    }

    private void initComsumer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while(true) {
                    try {
                        final NoMsgFrame frame = rcvQueue.poll(10, TimeUnit.MINUTES);
                        if (frame == null) continue;
                        listeners.forEach(l -> l.OnReceiveMessage(frame));
                    } catch (InterruptedException e) {
                        log.error("Polling fail - " + e.getMessage());
                    }
                }
            }
        }, 100L, 3000L);
    }

    @Override
    public void initialize(String address, int port) {
        TcpClientConnManager.getInstance().run();
    }

    @Override
    public void addEventListener(MeshConnectionEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void addPeerRouter(String address, int port) throws InterruptedException {
        TcpClientConnManager.getInstance().connect(address, port);
    }

    @Override
    public void sendMessage(String rid, NoMsgFrame frame) throws Exception {
        ChannelHandlerContext ctx = NoMsgCtxPool.getInstance().getCtxByRid(rid);
        if (ctx == null) {
            throw new Exception("no target rid - " + rid);
        }

        ctx.writeAndFlush(frame);
    }

    @Override
    public void sendBroadcast(NoMsgFrame frame) {
        for (String rid : getAvailableRouterList().keySet()) {
            try {
                sendMessage(rid, frame);
            } catch (Exception e) {
                log.error("send fail - " + rid + " : " + e.getMessage());
            }
        }
    }

    @Override
    public Map<String, InetSocketAddress> getAvailableRouterList() {
        return NoMsgCtxPool.getInstance().getAvailableRouterList();
    }
}
