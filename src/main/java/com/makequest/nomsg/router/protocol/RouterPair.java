package com.makequest.nomsg.router.protocol;

public class RouterPair {
    private final String hid;
    private final String rid;

    public RouterPair(String hid, String rid) {
        this.hid = hid;
        this.rid = rid;
    }

    public String getHid() {
        return hid;
    }

    public String getRid() {
        return rid;
    }

    @Override
    public String toString() {
        return String.format("hid=" + this.hid + ", rid=" + this.rid);
    }
}