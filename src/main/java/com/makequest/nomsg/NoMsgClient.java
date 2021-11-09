package com.makequest.nomsg;

import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;

public class NoMsgClient extends NoMsgPeer{


    public NoMsgClient(String cluster, String uid) {
        super(cluster, uid);
    }

    // Message 송신.
    void sendMessage(NoMsgUnit unit) throws NoMsgNetworkException {

    }

    void setHandler(NoMsgReceiverInterface handler){

    }

    void attach() throws NoMsgClientException {

    }

    void detach() throws NoMsgClientException{

    }

    void join(String topic){

    }

    void leave(String topic){

    }
}
