package com.makequest.nomsg.router.body;

import lombok.Data;

@Data
@Deprecated
public class NoMsgRouterId {
    String cluster;
    String address;
    String port;

    protected final String getKey(){
        return String.format("%s|%s|%s", cluster, address, port);
    }
}
