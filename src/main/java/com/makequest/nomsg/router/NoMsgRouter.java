package com.makequest.nomsg.router;

import com.makequest.nomsg.NoMsgClient;
import com.makequest.nomsg.NoMsgPeer;
import com.makequest.nomsg.NoMsgSendType;
import com.makequest.nomsg.NoMsgUnit;
import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;
import com.makequest.nomsg.exception.NoMsgRouterException;
import com.makequest.nomsg.router.protocol.RouteTable;
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
public class NoMsgRouter implements MeshConnectionEventListener{
    private static final String publicCluster = "public";
    private static NoMsgRouter instance;
    private MeshConnectionEventListener eventListener;
    private MeshConnectionHandle handle;

    private String clusterName = publicCluster;
    private String hostName;
    private String routerName = UUID.randomUUID().toString();
    private Timer sendTimer;

    private final RouteTable routeTable = new RouteTable();

    private Map<String, NoMsgClient> peerIndex = new HashMap<>();
    private Map<String, List<NoMsgClient>> groupIndex = new HashMap<>();

    private BlockingQueue<NoMsgUnit> sendQueue = new LinkedBlockingQueue<>();

    private void _print(){
        log.info("-------------------------------------------------------------------------");
        log.info("Peer info");
        for(String cId : peerIndex.keySet()){
            log.info(String.format("CID (%s) : Client (%s)", cId, peerIndex.get(cId).toString()));
        }
        log.info("");

        log.info("Group Info");
        for(String gId : groupIndex.keySet()){
            log.info("GID : " + gId);
            for(NoMsgClient c : groupIndex.get(gId)){
                log.info("  - Client " + c.getCId());
            }
        }
        log.info("-------------------------------------------------------------------------");

    }

    private NoMsgRouter() {
        try {
            this.hostName = InetAddress.getLocalHost().getHostName();
            this.handle = MeshConnectionHandleImpl.getInstance();
        } catch (UnknownHostException e) {
            log.error("Fail to get local hostname : " + e.getLocalizedMessage());
            String name = UUID.randomUUID().toString();
            log.error("Set hostname to : " + name);
            this.hostName = name;
        }

        this.sendTimer = new Timer();
        this.sendTimer.scheduleAtFixedRate(new SendTimer(), 1000, 500);
    }

    private void sendDownLink(NoMsgUnit unit){
        NoMsgPeer dest = unit.getDestination();
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
                List<NoMsgClient> clients = groupIndex.get(dest.getVId());
                for(NoMsgClient client : clients){
                    try {
                        NoMsgUnit nUnit = unit.clone();
                        nUnit.setTargetCid(client.getCId());
                        sendQueue.add(nUnit);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
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
        NoMsgPeer dest = unit.getDestination();
        switch(dest.getType()){
            case DIRECT:
                List<String> routes = this.routeTable.getRouteByHost(dest.getHostName());
                for(String rid : routes){
                    handle.sendMessage(rid, unit.toDataFrame());
                }
                break;
            case BROADCAST:
            case GROUP:
                if (handle.sendBroadcast(unit.toDataFrame()) <= 0){
                    log.info("Can't send any destination.");
                }
                break;
        }
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
     * @param port
     */
    public void initRouter(int port) throws NoMsgNetworkException {
        this.initRouter("0.0.0.0", port);
    }

    /**
     * Router 를 초기화 한다. 프로세스 내부통신 전용으로 사용한다.
     */
    public void initRouter(){
    }

    /**
     * Router 를 초기화 한다. 사용자가 지정한 주소 및 포트로 바인딩 가능해야 한다.
     *
     * @param address
     * @param port
     */
    public void initRouter(String address, int port) throws NoMsgNetworkException {
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
        for(String gId : groupIndex.keySet()){
            List<NoMsgClient> clients = groupIndex.get(gId);
            if (clients == null) continue;
            clients.remove(client);
        }
    }

    public final void addTopic(String gId, NoMsgClient client){
        List<NoMsgClient> clients = groupIndex.get(gId);
        if (clients == null){
            clients = new LinkedList<>();
            clients.add(client);
            groupIndex.put(gId, clients);
        } else {
            clients.add(client);
        }
        log.info(String.format("Client (%s) join group (%s)", client.getCId(), gId));
    }

    public final void removeTopic(String gId, NoMsgClient client){
        List<NoMsgClient> clients = groupIndex.get(gId);
        if (clients == null){
            log.warn("Client did not join group : " + gId);
        } else {
            clients.remove(client);
        }
        log.info(String.format("Client (%s) leave group (%s)", client.getCId(), gId));
    }

    public final void sendMessage(NoMsgUnit message){
        NoMsgPeer dest = message.getDestination();
        log.info("Send message to : " + dest.toTopic());
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
                } else if (dest.getCId() == null && dest.getHostName() == null){
                    sendDownLink(message);
                } else if (dest.getCId() == null && dest.getHostName().equals(NoMsgRouter.createRouter().routerName)){
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

    @Override
    public void OnReceiveMessage(NoMsgFrame frame) {
        log.info("Receive from remote : " + frame.toString());
    }

    public class SendTimer extends TimerTask {
        @Override
        public void run() {
            while(!Thread.interrupted()){
                try {
//                    _print();
                    NoMsgUnit unit = sendQueue.take();
                    String cid = unit.getTargetCid();
                    NoMsgClient client = peerIndex.get(cid);
                    client.getReceiverInterface().OnReceiveMessage(unit.getSource(), unit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
