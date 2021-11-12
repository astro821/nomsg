package com.makequest.nomsg.router;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoMsgFrame {
    NoMsgFrameType type;
    String hid;
    String rid;

    public NoMsgFrameType getType() {
        return type;
    }

    public void setType(NoMsgFrameType type) {
        this.type = type;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }
}
