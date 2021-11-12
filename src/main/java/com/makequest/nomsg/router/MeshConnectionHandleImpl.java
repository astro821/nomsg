package com.makequest.nomsg.router;

import com.makequest.nomsg.exception.NoMsgNetworkException;
import com.makequest.nomsg.router.client.TcpClientConnManager;
import com.makequest.nomsg.router.server.TcpServerConnManager;
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
    private LinkedBlockingQueue<NoMsgFrameData> rcvQueue = new LinkedBlockingQueue<>();

    private MeshConnectionHandleImpl() {
        initConsumer();
    }

    public static MeshConnectionHandleImpl getInstance() {
        return Creator.inst;
    }


    public void addNoMsgFrame(NoMsgFrameData frame) {
        System.err.println(" - add queue - " + frame.getType());
        rcvQueue.add(frame);
    }

    private void initConsumer() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                while(true) {
                    try {
                        final NoMsgFrameData frame = rcvQueue.poll(10, TimeUnit.MINUTES);
                        if (frame == null) continue;

                        System.err.println(" >> poll from quque : " + frame);
                        for (MeshConnectionEventListener l : listeners) {
                            l.OnReceiveMessage(frame);
                        }
                    } catch (InterruptedException e) {
                        log.error("Polling fail - " + e.getMessage());
                        continue;
                    }
                }
            }
        }, 100L, 3000L);
    }

    private TcpServerConnManager serverConnManager = null;
    @Override
    public void initialize(String address, int port) throws NoMsgNetworkException {
        log.info("Initializa - " + address + ":" + port);
        if (serverConnManager == null) {
            try {
                serverConnManager = new TcpServerConnManager(address, port);
            } catch (Exception e) {
                log.error("TCP Server connection manager exception - " + e.getMessage());
            }
            // ToDo.. Thread를 여기서?????
            new Thread(() -> {
                try {
                    serverConnManager.run();
                } catch (Exception e) {
                    log.error("TCP Server connection manager exception - " + e.getMessage());
                }
            }).start();
        } else {
            throw new NoMsgNetworkException("server already initialized.");
        }
        TcpClientConnManager.getInstance();
    }

    @Override
    public void addEventListener(MeshConnectionEventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void addPeerRouter(String address, int port) {
        TcpClientConnManager.getInstance().connect(address, port);
    }

    @Override
    public int sendMessage(String rid, NoMsgFrame frame) {
        ChannelHandlerContext ctx = NoMsgCtxPool.getInstance().getCtxByRid(rid);
        if (ctx == null) {
            log.error("no target rid - " + rid);
            return 0;
        }

        ctx.writeAndFlush(frame);
        return 1;
    }

    @Override
    public int sendBroadcast(NoMsgFrame frame) {
        int count = 0;
        for (String rid : getAvailableRouterList().keySet()) {
            try {
                count += sendMessage(rid, frame);
            } catch (Exception e) {
                log.error("send fail - " + rid + " : " + e.getMessage());
            }
        }

        return count;
    }

    @Override
    public Map<String, InetSocketAddress> getAvailableRouterList() {
        return NoMsgCtxPool.getInstance().getAvailableRouterList();
    }
}
