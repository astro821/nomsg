package com.makequest.nomsg.test.inner;

import com.makequest.nomsg.NoMsgClient;
import com.makequest.nomsg.NoMsgReceiverInterface;
import com.makequest.nomsg.NoMsgUnit;
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

        this.client = new NoMsgClient(sorceUid);
        initReceiver();
    }

    private void initReceiver() {
        client.setReceiverInterface(this);
    }

    private int index = 0;
    private void sendTest() {
        TestVo vo = new TestVo();
        vo.setIndex(index++);
        vo.setName("Name" + index);

        NoMsgUnit unit = new NoMsgUnit();
        unit.setInternalDest(targetUid);
        unit.setObject(vo);

        try {
            log.info("Send : " + vo);
            this.client.sendMessage(unit);
        } catch (NoMsgNetworkException e) {
            log.error(e.getMessage(), e);
        }
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
    public void OnReceiveMessage(NoMsgUnit message) {
        TestVo vo = message.getObject(TestVo.class);
        log.info("RCV obj : " + vo);
        log.info("RCV json : " + message.getBody());
    }

    @Override
    public void OnReceiveMessage(String topic, NoMsgUnit message) {
        TestVo vo = message.getObject(TestVo.class);
        log.info("RCV(" + topic + ") obj : " + vo);
        log.info("REV(" + topic + ") json : " + message.getBody());
    }
}
