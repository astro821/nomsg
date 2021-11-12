package com.makequest.nomsg;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public abstract class NoMsgParser implements Cloneable{
    private int messageId;
    private String body;

    public <T> T getObject(Class<T> clazz){
        return new Gson().fromJson(body, (Type)clazz);
    }

    public void setObject(Object obj){
        body = new Gson().toJson(obj);
    }

    public int getMessageId(){
        return messageId;
    }

    @Override
    public NoMsgParser clone() throws CloneNotSupportedException {
        NoMsgParser parser = (NoMsgParser) super.clone();
        parser.body = this.body;
        parser.messageId = this.messageId;
        return parser;
    }
}
