package com.makequest.nomsg;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;

@Getter
@Setter
public class NoMsgUnit implements Cloneable{
    transient String targetCid;

    NoMsgDest destination;
    String body;

    public <T> T getObject(Class<T> clazz){
        return new Gson().fromJson(body, (Type)clazz);
    }

    public void setObject(Object obj){
        body = new Gson().toJson(obj);
    }

    @Override
    public NoMsgUnit clone() throws CloneNotSupportedException {
        NoMsgUnit unit = (NoMsgUnit) super.clone();
        unit.setDestination(this.destination.clone());
        unit.setBody(body);
        return unit;
    }

    public void setInternalDest(String cId){
        this.destination = new NoMsgDest();
        this.destination.setType(NoMsgSendType.DIRECT);
        this.destination.setCId(cId);
    }

    public void setDirectDest(String hostName, String cId){
        this.destination = new NoMsgDest();
        this.destination.setType(NoMsgSendType.DIRECT);
        this.destination.setCId(cId);
        this.destination.setHostName(hostName);
    }

    public void setBroadcastDest(String hostName, String cId){
        if (hostName != null && cId != null){
            this.setDirectDest(hostName, cId);
            return;
        }
        this.destination = new NoMsgDest();
        this.destination.setType(NoMsgSendType.BROADCAST);
        this.destination.setCId(cId);
        this.destination.setHostName(hostName);
    }

    public void setGroupDest(String gId){
        this.destination = new NoMsgDest();
        this.destination.setType(NoMsgSendType.GROUP);
        this.destination.setVId(gId);
    }
}
