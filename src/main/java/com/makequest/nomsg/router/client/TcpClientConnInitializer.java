package com.makequest.nomsg.router.client;

import com.makequest.nomsg.router.codec.NoMsgFrameDecoder;
import com.makequest.nomsg.router.codec.NoMsgFrameEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientConnInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger log = LoggerFactory.getLogger(TcpClientConnInitializer.class);
    private SslContext sslCtx;

    public TcpClientConnInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new NoMsgFrameEncoder("[SND] "));
        pipeline.addLast(new NoMsgFrameDecoder("[RCV] "));

        pipeline.addLast(new TcpClientConnHandler());
    }
}
