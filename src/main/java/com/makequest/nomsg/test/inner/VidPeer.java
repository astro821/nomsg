package com.makequest.nomsg.test.inner;

import com.makequest.nomsg.NoMsgClient;
import com.makequest.nomsg.NoMsgParser;
import com.makequest.nomsg.NoMsgReceiverInterface;
import com.makequest.nomsg.NoMsgUnit;
import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class VidPeer implements Runnable, NoMsgReceiverInterface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String sorceUid;

    private final String sourceVid;
    private final String targetVid;

    private final long period;

    private final NoMsgClient client;

    public VidPeer(String sourceUid, String sourceVid, String targetVid, long period) {
        this.sorceUid = sourceUid;
        this.sourceVid = sourceVid;
        this.targetVid = targetVid;

        this.period = period;

        log.info("Init clients.(" + sourceUid + ")");
        this.client = new NoMsgClient(sorceUid);
        log.info("Init receiver.(" + sourceUid + ")");
        initReceiver();

        try {
            this.client.attach();
        } catch (NoMsgClientException e) {
            log.error("attach fail - " + e.getMessage());
        }

        if (sourceVid != null) {
            this.client.join(sourceVid);
        }
    }

    private void initReceiver() {
        client.setReceiverInterface(this);
    }

    private int index = 0;
    private void sendTest() {
        TestVo vo = new TestVo();
        vo.setIndex(index++);
        vo.setName("Name" + index);

        log.info("Send from " + this.sorceUid + " : " + vo);
        this.client.sendGroup(this.targetVid, vo);
    }

    @Override
    public void run() {
        if (this.targetVid != null) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    sendTest();
                }
            }, period * 1000, period * 1000L);
        }
    }


    @Override
    public void OnReceiveMessage(NoMsgParser unit) {
        log.info("TEST : " + unit.getObject(TestVo.class));
    }

    @Override
    public void OnReceiveMessage(String topic, NoMsgUnit message) {
        TestVo vo = message.getObject(TestVo.class);
        log.info(this.sorceUid + " - RCV(" + topic + ") obj : " + vo);
    }
}
