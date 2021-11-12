package com.makequest.nomsg;

import com.makequest.nomsg.router.NoMsgRouter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMsgPeer implements Cloneable{
    NoMsgSendType type;

    String clusterName = NoMsgRouter.createRouter().getClusterName();
    String hostName = NoMsgRouter.createRouter().getHostName();
    String cId;
    String vId;

    public final String toTopic(){
        switch (type){
            case DIRECT:
                return String.format(
                        "p/%s/%s/%s",
                        clusterName,
                        hostName,
                        cId
                        );
            case BROADCAST:
                return String.format(
                        "p/%s/%s/%s",
                        clusterName,
                        hostName == null ? "*" : hostName,
                        cId == null ? "*" : cId
                );
            case GROUP:
                return String.format(
                        "v/%s/%s",
                        clusterName,
                        vId
                );
        }
        throw new RuntimeException("WARNING : NoMsg format invalid.");
    }

    @Override
    public NoMsgPeer clone() throws CloneNotSupportedException {
        NoMsgPeer dest = (NoMsgPeer) super.clone();
        dest.setType(type);
        dest.setClusterName(clusterName);
        dest.setHostName(hostName);
        dest.setCId(cId);
        dest.setVId(vId);
        return dest;
    }
}
