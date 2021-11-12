package com.makequest.nomsg.test.inner;

import com.makequest.nomsg.NoMsgClient;
import com.makequest.nomsg.NoMsgParser;
import com.makequest.nomsg.NoMsgPeer;
import com.makequest.nomsg.NoMsgReceiverInterface;
import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;
import com.makequest.nomsg.exception.NoMsgRouterException;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class RemoteMain extends Thread implements NoMsgReceiverInterface {
    NoMsgClient client = new NoMsgClient("P_ONE");
    String vid = "TEST1";
    Timer timer = new Timer();
    final String destHost;


    public RemoteMain(String destHost) throws NoMsgClientException, NoMsgNetworkException, NoMsgRouterException {
        this.destHost = destHost;
        client.attach(InetSocketAddress.createUnresolved("0.0.0.0", 13854));
        client.join(this.vid);
        client.setHandler(this);
        List<InetSocketAddress> remotes = new LinkedList<>();
        remotes.add(InetSocketAddress.createUnresolved(destHost, 13854));
        client.addRemotes(remotes);
        this.start();
        timer.scheduleAtFixedRate(new SendTask(), 1000, 5000);
    }

    @Override
    public void OnReceiveMessage(NoMsgPeer noMsgPeer, NoMsgParser noMsgParser) {
        log.info(String.format("[ONE] Receive message with type (%d) : %s  ",
                noMsgParser.getMessageId(),
                noMsgParser.getObject(TestVo.class).toString()));
    }

    @Override
    public void OnReceiveMessage(NoMsgPeer noMsgPeer, String s, NoMsgParser noMsgParser) {
        log.info(String.format("[ONE] Receive message with type (%d) : %s  ",
                noMsgParser.getMessageId(),
                noMsgParser.getObject(TestVo.class).toString()));
    }

    public class SendTask extends TimerTask {
        int seq = 1;

        @Override
        public void run() {
            TestVo vo = new TestVo();
            vo.setIndex(this.seq++);
            vo.setName("PEER SAM : " + vo.getIndex());
            client.sendBroadCast(null, null,1,  vo);
            log.info("Send data done. SEQ : " + vo.getIndex());
        }
    }

    public static void main(String[] args) throws NoMsgNetworkException, NoMsgRouterException, NoMsgClientException {
        RemoteMain main = new RemoteMain("192.168.0.114");
    }
}