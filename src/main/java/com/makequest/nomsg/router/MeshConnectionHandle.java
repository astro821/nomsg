package com.makequest.nomsg.router;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Netty 가 동작하는 클래스가 상속해야 하는 인터페이스.
 * NoMsgRouter 에서 접근하는 Netty 객체는 아래 인터페이스로 Call 한다.
 */
public interface MeshConnectionHandle {

    /**
     * Router 의 기본 동작을 위해 주어진 Address, port 로 TCP binding 을 한다.
     *
     * @param address 주소, IPv4 만 사용한다.
     * @param port 바인딩 대상 포트
     */
    void initialize(String address, int port);

    /**
     * Netty 에서 NoMsgRouter 의 method 를 call 하기 위해 사용하는 event handle 등록
     *
     * @param listener NoMsgRouter 객체.
     */
    void addEventListener(MeshConnectionEventListener listener);

    /**
     * 다른 시스템, 프로세스에서 구동중인 NoMsgRouter 를 연결하기 위해 사용한다.
     * 본 method 가 호출되는 경우, destination 으로 TCP 세션을 생성한다.
     * 만일 생성이 되지 않거나, 연결된 세션이 끊어지는 경우 매 단위 시간마다 재 접속
     * 시도를 해야 한다.
     *
     * @param address TCP 접속대상 IP Address
     * @param port TCP 접속대상 포트
     */
    void addPeerRouter(String address, int port);


    /**
     * 대상 Router 로 메시지를 전송한다.
     *
     * @param rid 메시지 전송대상 목적지
     * @param frame 전송 대상 메시지.
     */
    void sendMessage(String rid, NoMsgFrame frame);

    /**
     * 현재 연결된 모든 Router 로 메시지를 전송한다.
     * @param frame 전송대상 메시지.
     */
    void sendBroadcast(NoMsgFrame frame);

    /**
     * 연결되어 있는 Router 전체 목록을 반환한다.
     * @return 라우터 목록 (RID, IP_PORT)
     */
    Map<String, InetSocketAddress> getAvailableRouterList();

}
