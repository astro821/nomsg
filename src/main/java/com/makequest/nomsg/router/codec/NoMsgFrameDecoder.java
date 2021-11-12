package com.makequest.nomsg.router.codec;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.makequest.nomsg.NoMsgUnit;
import com.makequest.nomsg.router.NoMsgFrame;
import com.makequest.nomsg.router.NoMsgFrameData;
import com.makequest.nomsg.router.NoMsgFrameType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class NoMsgFrameDecoder extends ByteToMessageDecoder { // (1)
    private static final Logger log = LoggerFactory.getLogger(NoMsgFrameDecoder.class);

    private String dirTag = "<>";

    public NoMsgFrameDecoder(String dirTag) {
        this.dirTag = dirTag;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws UnsupportedEncodingException {
        System.err.println(" cache size : " + in.readableBytes());
        try {
            if (in.readableBytes() < 4) return; // type code - 4byte

            in.markReaderIndex();

            int typeCode = in.readInt();
            NoMsgFrameType type = NoMsgFrameType.getByCode(typeCode);

            System.err.println(" > type : " + type);

            if (in.readableBytes() < 4) { // hid length - 4byte
                in.resetReaderIndex();
                return;
            }

            int hidLength = in.readInt();
            String hid = null;
            if (hidLength > 0) {
                if (in.readableBytes() < hidLength) {
                    in.resetReaderIndex();
                    return;
                }
                byte[] bytes = new byte[hidLength];
                in.readBytes(bytes);

                hid = new String(bytes);

                System.err.println(" > HID : " + hid);
            }

            if (in.readableBytes() < 4) { // rid length - 4byte
                in.resetReaderIndex();
                return;
            }

            int ridLength = in.readInt();
            String rid = null;
            if (ridLength > 0) {
                if (in.readableBytes() < ridLength) {
                    in.resetReaderIndex();
                    return;
                }
                byte[] bytes = new byte[ridLength];
                in.readBytes(bytes);

                rid = new String(bytes);

                System.err.println(" > RID : " + rid);
            }

            if (in.readableBytes() < 4) { // msg length - 4byte
                in.resetReaderIndex();
                return;
            }

            NoMsgFrame frame;
            if (type == NoMsgFrameType.DATA) {
                int msgLength = in.readInt();
                NoMsgUnit unit = null;
                if (msgLength > 0) {
                    if (in.readableBytes() < msgLength) {
                        in.resetReaderIndex();
                        return;
                    }
                    byte[] bytes = new byte[msgLength];
                    in.readBytes(bytes);

                    unit = new Gson().fromJson(new String(bytes), NoMsgUnit.class);
                }

                frame = new NoMsgFrameData();
                ((NoMsgFrameData)frame).setUnit(unit);
            } else {
                frame = new NoMsgFrame();
            }

            frame.setType(type);
            frame.setHid(hid);
            frame.setRid(rid);

            System.err.println(" > ?????");

            out.add(frame);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        System.err.println(" # Docoding complete.");
    }
}