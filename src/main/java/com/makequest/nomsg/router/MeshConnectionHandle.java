package com.makequest.nomsg.router;

import java.net.InetSocketAddress;
import java.util.List;
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
     * 연결중, 혹은 연결시도를 하고 있는 NoMsgRouter 를 해제한다.
     * 연결중이라면 TCP 연결을 끊고, Peer 목록에서 제거한다.
     *
     * @param peerUid
     */
    void delPeerRouter(String peerUid);


    /**
     * 연결중인 NoMsgRouter 를 연결해제 만 하고, pending  list 에 추가한다.
     * pending list 에 있는 경우 아무런 동작을 하지 않는다.
     * 일반적으로, TCP 연결은 되어 있으나, 대상이 무응답인 경우 (hang 혹은 cable unplugged)
     * 접속을 재 확인 하기 위해 강제로 끊고 재 접속 시도하기 위해 사용한다.
     *
     * @param peerUid
     */
    void disconnectPeerRouter(String peerUid);

    /**
     * 대상 Router 로 메시지를 전송한다.
     *
     * @param peerRouter 메시지 전송대상 목적지
     * @param frame 전송 대상 메시지.
     */
    void sendMessage(String peerRouter, NoMsgFrame frame);

    /**
     * 현재 연결된 모든 Router 로 메시지를 전송한다.
     * @param frame 전송대상 메시지.
     */
    void sendBroadcast(NoMsgFrame frame);

    /**
     * 연결되어 있는 Router 전체 목록을 반환한다.
     * @return 라우터 목록
     */
    Map<String, InetSocketAddress> getAvailableRouterList();

    /**
     * 연결대상 Router 이나, 아직 연결이 되지 않은 Router 목록을 반환한다.
     * @return 라우터 목록
     */
    List<String> getPendingPeerList();




}
