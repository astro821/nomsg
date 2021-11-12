package com.makequest.nomsg;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMsgUnit extends NoMsgParser implements Cloneable{
    transient String targetCid;

    private NoMsgPeer source;
    private NoMsgPeer destination;

    @Override
    public NoMsgUnit clone() throws CloneNotSupportedException {
        NoMsgUnit unit = (NoMsgUnit) super.clone();
        unit.destination = this.destination.clone();
        return unit;
    }

    public void setInternalDest(String cId){
        this.destination = new NoMsgPeer();
        this.destination.setType(NoMsgSendType.DIRECT);
        this.destination.setCId(cId);
    }

    public void setDirectDest(String hostName, String cId){
        this.destination = new NoMsgPeer();
        this.destination.setType(NoMsgSendType.DIRECT);
        this.destination.setCId(cId);
        this.destination.setHostName(hostName);
    }

    public void setBroadcastDest(String hostName, String cId){
        if (hostName != null && cId != null){
            this.setDirectDest(hostName, cId);
            return;
        }
        this.destination = new NoMsgPeer();
        this.destination.setType(NoMsgSendType.BROADCAST);
        this.destination.setCId(cId);
        this.destination.setHostName(hostName);
    }

    public void setGroupDest(String gId){
        this.destination = new NoMsgPeer();
        this.destination.setType(NoMsgSendType.GROUP);
        this.destination.setVId(gId);
    }
}
