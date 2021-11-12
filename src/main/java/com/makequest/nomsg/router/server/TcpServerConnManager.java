package com.makequest.nomsg.router.server;

import com.makequest.nomsg.router.codec.NoMsgFrameDecoder;
import com.makequest.nomsg.router.codec.NoMsgFrameEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpServerConnManager {

    private String ip;
    private int port;

    public TcpServerConnManager(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            ServerBootstrap serverBootstrap = b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new NoMsgFrameEncoder("[SND] "));
                            pipeline.addLast(new NoMsgFrameDecoder("[RCV] "));

                            pipeline.addLast(new TcpServerConnHandler());
                        }
                    })
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
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);// (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(ip, port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
