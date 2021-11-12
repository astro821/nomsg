package com.makequest.nomsg;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMsgUnit implements Cloneable{
    transient String targetCid;

    NoMsgDest destination;
    String body;

    public Object getObject(Class clazz){
        return new Gson().fromJson(body, clazz);
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
}
