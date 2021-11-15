package com.makequest.nomsg;

import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;
import com.makequest.nomsg.exception.NoMsgRouterException;
import com.makequest.nomsg.router.NoMsgRouter;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.List;

@Getter
@Setter
public class NoMsgClient{
    private NoMsgReceiverInterface receiverInterface;
    private String cId;

    public NoMsgClient(String cid) throws NoMsgClientException {
        this.cId = cid;
        this.attach();
    }

    /**
     * Send message to process internal client(thread)
     * @param cId Client ID
     * @param mId Message ID
     * @param obj Object to send.
     * @throws NoMsgRouterException destination client is not exist or not attach yet.
     */
     public static void sendInternal(String cId, int mId, Object obj) throws NoMsgRouterException {
         NoMsgUnit unit = new NoMsgUnit();
         NoMsgPeer dest = new NoMsgPeer();
         dest.setType(NoMsgSendType.DIRECT);
         dest.setCId(cId);

         unit.setDestination(dest);
         unit.setObject(obj);
         unit.setMessageId(mId);

         NoMsgRouter.createRouter().sendMessageInternal(unit);
    }

    private void sendMessage(NoMsgUnit unit) {
        unit.setSender(this);
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.sendMessage(unit);
    }

    public void sendDirect(String cId, int mId, Object obj){
        this.sendDirect(NoMsgRouter.createRouter().getHostName(), cId, mId, obj);
    }

    public void sendDirect(String host, String cId, int mId, Object obj){
        NoMsgUnit unit = new NoMsgUnit();
        NoMsgPeer dest = new NoMsgPeer();
        dest.setType(NoMsgSendType.DIRECT);
        dest.setHostName(host);
        dest.setCId(cId);

        unit.setDestination(dest);
        unit.setObject(obj);
        unit.setMessageId(mId);
        this.sendMessage(unit);
    }

    public void sendBroadcast(String cId, int mId, Object obj){
        this.sendBroadCast(NoMsgRouter.createRouter().getHostName(), cId, mId, obj);
    }

    public void sendBroadCast(String host, String cId, int mId, Object obj){
        NoMsgUnit unit = new NoMsgUnit();
        NoMsgPeer dest = new NoMsgPeer();
        dest.setType(NoMsgSendType.BROADCAST);
        dest.setHostName(host);
        dest.setCId(cId);

        unit.setDestination(dest);
        unit.setObject(obj);
        unit.setMessageId(mId);
        this.sendMessage(unit);
    }

    public void sendGroup(String vId, int mId, Object obj){
        NoMsgUnit unit = new NoMsgUnit();
        NoMsgPeer dest = new NoMsgPeer();
        dest.setType(NoMsgSendType.GROUP);
        dest.setVId(vId);

        unit.setMessageId(mId);
        unit.setDestination(dest);
        unit.setObject(obj);
        this.sendMessage(unit);
    }

    public void setHandler(NoMsgReceiverInterface handler){
        this.receiverInterface = handler;
    }

    public void addRemotes(List<InetSocketAddress> destList) throws NoMsgRouterException {
        for (InetSocketAddress sock : destList) {
            NoMsgRouter.createRouter().addRouter(sock);
        }
    }

    public void attach(InetSocketAddress localAddress) throws NoMsgNetworkException, NoMsgClientException {
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.initRouter(localAddress.getHostName(), localAddress.getPort());
        router.addClient(this);
    }

    public void attach() throws NoMsgClientException {
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.addClient(this);
    }

    public void detach() throws NoMsgClientException{
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.removeClient(this);
    }

    public void join(String vId){
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.addTopic(vId, this);
    }

    public void leave(String vId){
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.removeTopic(vId, this);
    }

    public void setHostName(String hostname){
        NoMsgRouter.createRouter().setHostName(hostname);
    }

    public void setClusterName(String clusterName){
        NoMsgRouter.createRouter().setClusterName(clusterName);
    }
}
