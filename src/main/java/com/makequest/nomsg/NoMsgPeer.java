package com.makequest.nomsg;

/**
 * FIXME: 아래 자료형은 임시.
 *  - 라이브러리 밖에서 new 못하게 하자.
 *  - Getter 필요.
 *
 */
public class NoMsgPeer {
    final String cluster;
    final String host;
    final String process;
    final String uid;

    public NoMsgPeer(String cluster, String uid) {
        this.cluster = cluster;
        this.host = "";
        this.process = "";
        this.uid = uid;
    }

    public String key(){
        return String.format("%s|%s|%s", host, process, uid);
    }
}
