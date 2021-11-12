package com.makequest.nomsg.router;

import com.makequest.nomsg.router.protocol.RouterPair;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class NoMsgCtxPool {
    private static final class Creator {
        private static final NoMsgCtxPool inst = new NoMsgCtxPool();
    }

    private Hashtable<String, ChannelHandlerContext> hashRidCtx = new Hashtable<>();
    private Hashtable<ChannelHandlerContext, RouterPair> hashCtxPair = new Hashtable<>();

    private NoMsgCtxPool() {
    }

    public static NoMsgCtxPool getInstance() {
        return Creator.inst;
    }

    public void addRouterPair(ChannelHandlerContext ctx, RouterPair pair) {
        this.hashCtxPair.put(ctx, pair);
        this.hashRidCtx.put(pair.getRid(), ctx);
    }

    public RouterPair getPairByCtx(ChannelHandlerContext ctx) {
        return this.hashCtxPair.get(ctx);
    }

    public ChannelHandlerContext getCtxByRid(String rid) {
        return this.hashRidCtx.get(rid);
    }

    public Map<String, InetSocketAddress> getAvailableRouterList() {
        HashMap<String, InetSocketAddress> hashRouter = new HashMap<>();

        for (String rid : hashRidCtx.keySet()) {
            ChannelHandlerContext ctx = hashRidCtx.get(rid);
            hashRouter.put(rid, (InetSocketAddress)ctx.channel().remoteAddress());
        }

        return hashRouter;
    }

    public boolean checkConnected(String ip, int port) {
        for (InetSocketAddress addr : getAvailableRouterList().values()) {
            if (addr.getAddress().getHostAddress().equals(ip) && addr.getPort() == port) return true;
        }

        return false;
    }
}
