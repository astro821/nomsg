package com.makequest.nomsg;

/**
 * 메시지 수신용 인터페이스.
 */
public interface NoMsgReceiverInterface {
    /**
     * 메시지 수신
     * @param message
     */
    void OnReceiveMessage(NoMsgUnit message);

    /**
     * 멀티캐스트 메시지 수신
     * @param topic
     * @param message
     */
    void OnReceiveMessage(String topic, NoMsgUnit message);

    /**
     * 라우팅 테이블 업데이트
     */
    void OnTableUpdate();

    /**
     * Peer 추가 삭제, 상태 변경 이벤트
     * @param peer
     * @param status
     */
    void OnPeerChanged(NoMsgPeer peer, NoMsgPeerStatus status);
}
