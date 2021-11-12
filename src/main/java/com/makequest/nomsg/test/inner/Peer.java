package com.makequest.nomsg.test.inner;

import com.makequest.nomsg.*;
import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class Peer implements Runnable, NoMsgReceiverInterface {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String sorceUid;
    private final String targetUid;
    private final long period;

    private final NoMsgClient client;

    public Peer(String sourceUid, String targetUid, long period) {
        this.sorceUid = sourceUid;
        this.targetUid = targetUid;
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
    }

    private void initReceiver() {
        client.setReceiverInterface(this);
    }

    private int index = 0;
    private void sendTest() {
        TestVo vo = new TestVo();
        vo.setIndex(index++);
        vo.setName("Name" + index);
        log.info("Send : " + vo);
        this.client.sendDirect(targetUid, 1, vo);
    }

    @Override
    public void run() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendTest();
            }
        }, period * 1000, period * 1000L);
    }


    @Override
    public void OnReceiveMessage(NoMsgPeer from, NoMsgParser unit) {

    }

    @Override
    public void OnReceiveMessage(NoMsgPeer from, String gid, NoMsgParser unit) {

    }
}
