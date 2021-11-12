package com.makequest.nomsg.router.client;

import com.makequest.nomsg.router.NoMsgCtxPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class TcpClientConnManager implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TcpClientConnManager.class);

    private static final int WorkerCount = 2;
    private EventLoopGroup workerGroup = null;
    private Bootstrap bootstraps = null;

    private int sourcePort = 0;

    private static final class Creator {
        private static final TcpClientConnManager inst = new TcpClientConnManager();
    }

    private TcpClientConnManager() {
        this.workerGroup = new NioEventLoopGroup(WorkerCount);
        this.bootstraps = new Bootstrap();

        Thread runner = new Thread(this);

        runner.start();
        try {
            runner.join();
        } catch (InterruptedException e) {
            log.error("[Client]join fail  - " + e.getMessage());
        }
    }

    public static TcpClientConnManager getInstance() {
        return Creator.inst;
    }

    public void run() {
        try {
            runManager();
        } catch (InterruptedException e) {
            log.error("[Client]Start fail - " + e.getMessage());
        } catch (SSLException e) {
            log.error("[Client]Start fail - " + e.getMessage());
        }
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    private void runManager() throws InterruptedException, SSLException {
        this.bootstraps.group(this.workerGroup)
                .channel(NioSocketChannel.class)
                // 반응속도를 높이기 위해 Nagle 알고리즘을 비활성화 합니다.
                .option(ChannelOption.TCP_NODELAY, true)
                // 소켓이 close될 때 신뢰성있는 종료를 위해 4way-handshake가 발생하고 이때 TIME_WAIT로 리소스가 낭비됩니다.
                // 이를 방지하기 위해 0으로 설정합니다.
                .option(ChannelOption.SO_LINGER, 0)
                // SO_LINGER설정이 있으면 안해도 되나 혹시나병(!)으로 TIME_WAIT걸린 포트를 재사용할 수 있도록 설정합니다.
                .option(ChannelOption.SO_REUSEADDR, true)
                //Keep-alive를 켭니다.
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                .handler(new LoggingHandler(LogLevel.DEBUG))
//                .handler(new IdleStateHandler(READER_IDLE_TIME_SECONDS, WRITER_IDLE_TIME_SECONDS, ALL_IDLE_TIME_SECONDS))
                .handler(new TcpClientConnInitializer())
        ;

        log.info("[Client] ready....");
    }

    public void stopManager() {
        this.workerGroup.shutdownGracefully();
        log.info("[Client]Connection Handler stop.");
    }

    public void connect(final String ip, final int port) {
        new Thread(() -> {
            try {
                _connect(ip, port);
            } catch (InterruptedException e) {
                log.error("connect sync interupped.");
            }
        }).start();
    }

    private void _connect(final String ip, final int port) throws InterruptedException {
        log.info(String.format("[TCP] try connect - %s:%d", ip, port));

        if (NoMsgCtxPool.getInstance().checkConnected(ip, port)) {
            log.warn(String.format("[TCP] already connected. - %s:%d", ip, port));
            return;
        }

        SocketAddress remote = new InetSocketAddress(ip, port);
        SocketAddress local = new InetSocketAddress(ip, this.sourcePort);

        ChannelFuture f = null;
        if (this.sourcePort == 0) {
            f = bootstraps.connect(ip, port).sync();
        } else {
            f = bootstraps.connect(remote, local).sync();
        }

        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    Throwable cause = future.cause();
                    log.error(String.format("####################################### [TCP] connect fail : %s:%p - %s",
                            ip, port, cause));
                    future.channel().close();
                }
            }
        });
    }}