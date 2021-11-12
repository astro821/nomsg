package com.makequest.nomsg.router;

import lombok.Getter;

@Getter
public enum NoMsgFrameType {
    UNKNOWN(0),
    SIGNAL_HELLO(10),

    DATA(20),
    ;

    final int type;

    NoMsgFrameType(int type) {
        this.type = type;
    }

    public static NoMsgFrameType fromType(int type){
        switch (type){
            case 2: return SIGNAL_HELLO;
            case 3: return DATA;
            default: return UNKNOWN;
        }
    }
}
