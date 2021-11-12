package com.makequest.nomsg.router;

public enum NoMsgFrameType {
    UNKNOWN(0),
    SIGNAL_HELLO(10),

    DATA(20),
    ;

    final int code;

    NoMsgFrameType(int type) {
        this.code = type;
    }

    public int getCode() {
        return code;
    }

    public static NoMsgFrameType getByCode(int code){
        for (NoMsgFrameType type : values()) {
            if (type.getCode() == code) return type;
        }

        return UNKNOWN;
    }
}
