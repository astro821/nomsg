package com.makequest.nomsg.router;

import com.makequest.nomsg.router.body.NoMsgRouterId;

/**
 * NoMsgRouter 가 사용하는 Interface.
 * Netty 가 동작하는 클래스에서는 아래 인터페이스를 이용해서 이벤트 마다 Call 해야 한다.
 */
public interface MeshConnectionEventListener {
    void OnRouterInitialized();

    void OnReceiveMessage(NoMsgFrame frame);

    void OnPeerConnected(NoMsgRouterId router);

    void OnPeerDisconnected(NoMsgRouterId router);


}
