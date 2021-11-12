package com.makequest.nomsg;

/**
 * 메시지 수신용 인터페이스.
 */
public interface NoMsgReceiverInterface {
    void OnReceiveMessage(NoMsgPeer from, NoMsgParser unit);

    void OnReceiveMessage(NoMsgPeer from, String gid, NoMsgParser unit);
}