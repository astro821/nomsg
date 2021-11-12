package com.makequest.nomsg;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Type;

@Getter
@Setter
public abstract class NoMsgParser implements Cloneable{

    protected int messageId;
    protected String body;

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
