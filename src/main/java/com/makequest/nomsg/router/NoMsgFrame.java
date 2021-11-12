package com.makequest.nomsg.router;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *                      1                   2                   3
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3
 * +--------------------------------------------------------------------+
 * |                   TYPE: 메시지 타입 : enum 별도 정의,                   |
 * +--------------------------------------------------------------------+
 * |                   SIZE: Body 의 메시지 크기, byte                     |
 * +--------------------------------------------------------------------+
 * |                   BODY: ... 가변 크기.
 * +---------------------------------------------------------/
 */
public class NoMsgFrame {
    private static final int HEAD_SIZE = 8;

    NoMsgFrameType type;
    int size;
    byte[] body;

    ByteBuffer toBB(){
        ByteBuffer bb = ByteBuffer.allocate(HEAD_SIZE + body.length);
        bb.putInt(type.getType());
        bb.putInt(size);
        bb.put(body);
        bb.flip();
        return bb;
    }

    public static NoMsgFrame fromBB(ByteBuffer bb){
        NoMsgFrame frame = new NoMsgFrame();
        frame.type = NoMsgFrameType.fromType(bb.getInt());
        frame.size = bb.getInt();
        frame.body = new byte[bb.remaining()];
        bb.get(frame.body);
        return frame;
    }
}
