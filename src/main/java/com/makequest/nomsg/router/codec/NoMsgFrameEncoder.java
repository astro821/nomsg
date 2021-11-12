package com.makequest.nomsg.router.codec;

import com.google.gson.Gson;
import com.makequest.nomsg.router.NoMsgFrame;
import com.makequest.nomsg.router.NoMsgFrameData;
import com.makequest.nomsg.router.NoMsgFrameType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoMsgFrameEncoder extends MessageToByteEncoder<NoMsgFrame> {
    private static final Logger log = LoggerFactory.getLogger(NoMsgFrameEncoder.class);

    private String dirTag = "<>";

    public NoMsgFrameEncoder(String dirTag) {
        this.dirTag = dirTag;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NoMsgFrame msg, ByteBuf out) {

        System.err.println(" > HID : " + msg.getHid() + " - RID : " + msg.getRid());

        // type : int - 4byte
        out.writeInt(msg.getType().getCode());
        // hid length + hid
        if (msg.getHid() != null) {
            byte[] bytes = msg.getHid().getBytes();
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        } else {
            out.writeInt(0);
        }
        // rid length + rid
        if (msg.getRid() != null) {
            byte[] bytes = msg.getRid().getBytes();
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        } else {
            out.writeInt(0);
        }

        // msg length
        if (msg.getType() == NoMsgFrameType.DATA) {
            NoMsgFrameData data = (NoMsgFrameData) msg;

            if (data.getUnit() != null) {
                byte[] bytes = new Gson().toJson(data.getUnit()).getBytes();

                System.err.println(" > body length : " + bytes.length);

                out.writeInt(bytes.length);
                out.writeBytes(bytes);
            } else {
                out.writeInt(0);
            }
        }

        System.err.println(" # Encoding complete.");
    }
}