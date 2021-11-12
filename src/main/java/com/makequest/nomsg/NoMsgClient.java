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

    void sendMessage(NoMsgUnit unit) throws NoMsgNetworkException {
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.sendMessage(unit);
    }

    void setHandler(NoMsgReceiverInterface handler){
        this.receiverInterface = handler;
    }

    void attach() throws NoMsgClientException {
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.addClient(this);
    }

    void detach() throws NoMsgClientException{
        NoMsgRouter router = NoMsgRouter.createRouter();
        router.removeClient(this);
    }

    void join(String vId){

    }

    void leave(String vId){

    }
}
