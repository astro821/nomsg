package com.makequest.nomsg;

/**
 * * NoMsgPeer 참고.
 *
 * DIRECT
 *  - host, process, thread 모두 지정.
 * BROADCAST
 *  - 전체 지정 X
 *  - host 지정.
 *  - host, process 지정.
 * MULTICAST
 *  - join 그룹 대상.
 */
public enum NoMsgSendType {
    DIRECT,
    BROADCAST,
    MULTICAST,
}
