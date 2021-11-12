package com.makequest.nomsg;

import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.router.NoMsgRouter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMsgClient{
    private NoMsgReceiverInterface receiverInterface;
    private String cId;

    public NoMsgClient(String cid) {
        this.cId = cid;
    }

    private void sendMessage(NoMsgUnit unit) {
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.sendMessage(unit);
    }

    public void sendDirect(String cId, Object obj){
        this.sendDirect(NoMsgRouter.createRouter().getHostName(), cId, obj);
    }

    public void sendDirect(String host, String cId, Object obj){
        NoMsgUnit unit = new NoMsgUnit();
        NoMsgPeer dest = new NoMsgPeer();
        dest.setType(NoMsgSendType.DIRECT);
        dest.setHostName(host);
        dest.setCId(cId);

        unit.setDestination(dest);
        unit.setObject(obj);
        this.sendMessage(unit);
    }

    public void sendBroadcast(String cId, Object obj){
        this.sendBroadCast(NoMsgRouter.createRouter().getHostName(), cId, obj);
    }

    public void sendBroadCast(String host, String cId, Object obj){
        NoMsgUnit unit = new NoMsgUnit();
        NoMsgPeer dest = new NoMsgPeer();
        dest.setType(NoMsgSendType.BROADCAST);
        dest.setHostName(host);
        dest.setCId(cId);

        unit.setDestination(dest);
        unit.setObject(obj);
        this.sendMessage(unit);
    }

    public void sendGroup(String vId, Object obj){
        NoMsgUnit unit = new NoMsgUnit();
        NoMsgPeer dest = new NoMsgPeer();
        dest.setType(NoMsgSendType.GROUP);
        dest.setVId(vId);

        unit.setDestination(dest);
        unit.setObject(obj);
        this.sendMessage(unit);
    }

    public void setHandler(NoMsgReceiverInterface handler){
        this.receiverInterface = handler;
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
}
