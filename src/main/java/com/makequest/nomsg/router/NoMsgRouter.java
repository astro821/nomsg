package com.makequest.nomsg.router;

import com.makequest.nomsg.NoMsgClient;
import com.makequest.nomsg.NoMsgDest;
import com.makequest.nomsg.NoMsgSendType;
import com.makequest.nomsg.NoMsgUnit;
import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgRouterException;
import com.makequest.nomsg.router.body.NoMsgRouterId;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Singleton.
 *
 */

@Getter
@Setter
@Slf4j
public class NoMsgRouter {
    private static NoMsgRouter instance;
    private MeshConnectionEventListener eventListener;
    private MeshConnectionHandle handle;

    private NoMsgRouterId routerId;

    private String clusterName;
    private String hostName;
    private String routerName;
    private Timer sendTimer;

    private Map<String, NoMsgClient> peerIndex = new HashMap<>();
    private Map<String, NoMsgClient> groupIndex = new HashMap<>();

    private BlockingQueue<NoMsgUnit> sendQueue = new LinkedBlockingQueue<>();

    private NoMsgRouter() {
        try {
            this.hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("Fail to get local hostname : " + e.getLocalizedMessage());
            String name = UUID.randomUUID().toString();
            log.error("Set hostname to : " + name);
            this.hostName = name;
        }

        this.sendTimer.scheduleAtFixedRate(new SendTimer(), 1000, 500);
    }

    private void sendDownLink(NoMsgUnit unit){
        NoMsgDest dest = unit.getDestination();
        switch(dest.getType()){
            case DIRECT:
                unit.setTargetCid(dest.getCId());
                sendQueue.add(unit);
                break;
            case BROADCAST:
                if (dest.getCId() == null){
                    for(String cId : getPeerIndex().keySet()){
                        try {
                            NoMsgUnit nUnit = unit.clone();
                            NoMsgClient client = peerIndex.get(cId);
                            nUnit.setTargetCid(client.getCId());
                            sendQueue.add(nUnit);
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                            log.error("Fail to message clone : " + e.getLocalizedMessage());
                        }
                    }
                }
                break;
            case GROUP:
                for(String vId : getGroupIndex().keySet()){
                    try {
                        NoMsgUnit nUnit = unit.clone();
                        NoMsgClient client = groupIndex.get(vId);
                        nUnit.setTargetCid(client.getCId());
                        sendQueue.add(nUnit);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        log.error("Fail to message clone : " + e.getLocalizedMessage());
                    }
                }
                break;
        }
    }

    private void sendUpLink(NoMsgUnit unit){
        if (this.handle == null){
            log.info("Uplink not connected");
            return;
        }
        log.error("NOT IMPLEMENTED YET");
    }

    public static NoMsgRouter createRouter(){
        if (instance == null){
            instance = new NoMsgRouter();
        }
        return instance;
    }

    /**
     * Router 를 추가 한다.
     * @param destination 연결대상 목적지 주소.
     * @throws NoMsgRouterException local binding 하지 않은 Router 일 때 addRouter 불가.
     *
     */
    public void addRouter(InetSocketAddress destination) throws NoMsgRouterException{
        this.handle.addPeerRouter(destination.getHostName(), destination.getPort());
    }

    /**
     * Router 를 초기화 한다. 0.0.0.0 으로 바인딩 하며, 포트만 지정한다.
     *
     * @param cluster
     * @param port
     */
    public void initRouter(String cluster, int port) throws NoMsgRouterException {
        this.initRouter(cluster, "0.0.0.0", port);
    }

    /**
     * Router 를 초기화 한다. 프로세스 내부통신 전용으로 사용한다.
     */
    public void initRouter(){
        this.clusterName = UUID.randomUUID().toString();
    }

    /**
     * Router 를 초기화 한다. 사용자가 지정한 주소 및 포트로 바인딩 가능해야 한다.
     *
     * @param cluster
     * @param address
     * @param port
     */
    public void initRouter(String cluster, String address, int port) throws NoMsgRouterException {
        this.clusterName = cluster;
        this.handle.initialize(address, port);
    }

    public final void addClient(NoMsgClient client) throws NoMsgClientException {
        String key = client.getCId();
        if (peerIndex.containsKey(key)){
            throw new NoMsgClientException("Client key already used");
        }
        peerIndex.put(client.getCId(), client);
        log.info("NoMSG client attached : " + client.getCId());
    }

    public final void removeClient(NoMsgClient client){
        peerIndex.remove(client.getCId());
        log.info("NoMSG client removed : " + client.getCId());
    }

    public final void sendMessage(NoMsgUnit message){
        NoMsgDest dest = message.getDestination();
        NoMsgSendType type =  dest.getType();
        switch (type){
            case DIRECT:
                if (peerIndex.containsKey(dest.getCId())) {
                    sendDownLink(message);
                }
                break;
            case BROADCAST:
                if (dest.getCId() != null && peerIndex.containsKey(dest.getCId())) {
                    sendDownLink(message);
                }
                sendUpLink(message);
                break;
            case GROUP:
                if (groupIndex.containsKey(dest.getVId())){
                    sendDownLink(message);
                }
                sendUpLink(message);
                break;
        }
    }

    public class SendTimer extends TimerTask {
        @Override
        public void run() {
            while(!Thread.interrupted()){
                try {
                    NoMsgUnit unit = sendQueue.take();
                    String cid = unit.getTargetCid();
                    NoMsgClient client = peerIndex.get(cid);
                    client.getReceiverInterface().OnReceiveMessage(unit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
