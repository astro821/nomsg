package com.makequest.nomsg;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMsgDest implements Cloneable{
    NoMsgSendType type;

    String clusterName;
    String hostName;
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
    public NoMsgDest clone() throws CloneNotSupportedException {
        NoMsgDest dest = (NoMsgDest) super.clone();
        dest.setType(type);
        dest.setClusterName(clusterName);
        dest.setHostName(hostName);
        dest.setCId(cId);
        dest.setVId(vId);
        return dest;
    }
}
