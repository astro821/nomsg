package com.makequest.nomsg.router.client;

import com.makequest.nomsg.router.*;
import com.makequest.nomsg.router.protocol.RouterPair;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class TcpClientConnHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(TcpClientConnHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.debug("channelRead method in. " + ctx.channel());

        if (!(msg instanceof NoMsgFrame)) {
            log.error("Unknown message received : " + msg.getClass());
            return;
        }

        NoMsgFrame message = (NoMsgFrame) msg;

        switch (message.getType()) {
            case UNKNOWN:
                log.error("Unknown type received.");
                return;
            case SIGNAL_HELLO:
                if (message.getHid() == null || message.getRid() == null) {
                    log.error("Invalid NoMsgFrame(hid=" + message.getHid()
                            + ", rid=" + message.getRid() + " - Drop.");
                    return;
                }
                NoMsgRouter.createRouter().getRouteTable().addTable(message.getHid(), message.getRid());
                RouterPair pair = new RouterPair(message.getHid(), message.getRid());

                NoMsgCtxPool.getInstance().addRouterPair(ctx, pair);
                break;
            case DATA:
                MeshConnectionHandleImpl.getInstance().addNoMsgFrame((NoMsgFrameData) message);
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("channelActive method in. " + ctx);
        log.info("[CTX] - activated : " + ((InetSocketAddress) ctx.channel().localAddress()).getPort());

        NoMsgFrame frame = new NoMsgFrame();
        frame.setType(NoMsgFrameType.SIGNAL_HELLO);
        frame.setHid(NoMsgRouter.createRouter().getHostName());
        frame.setRid(NoMsgRouter.createRouter().getRouterName());

        ctx.writeAndFlush(frame);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("channelInactive method in. " + ctx);
        log.info("[CTX] - inactivated : " + ((InetSocketAddress) ctx.channel().localAddress()).getPort());

        RouterPair routerPair = NoMsgCtxPool.getInstance().getPairByCtx(ctx);

        if (routerPair == null) {
            log.error("un mapped connect - " + ctx.channel().remoteAddress());
            return;
        } else {
            NoMsgRouter.createRouter().getRouteTable().removeTable(routerPair.getHid(), routerPair.getRid());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            log.error("exceptionCaught method in. " + cause.toString(), cause);
        } catch (Exception e) {
        }
        ctx.close();
    }
}