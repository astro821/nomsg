package com.makequest.nomsg;

/**
 * 메시지 수신용 인터페이스.
 */
public interface NoMsgReceiverInterface {
    /**
     * 메시지 수신
     * @param message
     */
    void OnReceiveMessage(NoMsgParser unit);

    /**
     * 멀티캐스트 메시지 수신
     * @param topic
     * @param message
     */
    void OnReceiveMessage(String topic, NoMsgUnit message);
}