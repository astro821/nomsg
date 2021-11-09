package com.makequest.nomsg;

import com.google.gson.Gson;

public class NoMsgUnit {
    NoMsgTrxType typeTrx;
    NoMsgSendType typeSend;
    NoMsgPeer from;
    NoMsgPeer to;

    String body;

    public Object getObject(Class clazz){
        return new Gson().fromJson(body, clazz);
    }

    public void setObject(Object obj){
        body = new Gson().toJson(obj);
    }
}
