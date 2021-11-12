package com.makequest.nomsg;

import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;
import com.makequest.nomsg.router.NoMsgRouter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMsgClient{
    private NoMsgReceiverInterface receiverInterface;
    private String cId;

    public NoMsgClient(String uid) {
    }

    public void sendMessage(NoMsgUnit unit) throws NoMsgNetworkException {
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.sendMessage(unit);
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

    }

    public void leave(String vId){

    }
}
