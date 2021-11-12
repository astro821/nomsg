package com.makequest.nomsg.test.inner;

public class InnerVidMain {
    public static void main(String[] args) throws InterruptedException {
        VidPeer peer1 = new VidPeer("Peer1", "test", "TestVid", 3);
//        VidPeer peer2 = new VidPeer("Peer2", "TestVid", null, 4);
//        VidPeer peer3 = new VidPeer("Peer3", "TestVid", null, 5);
        VidPeer peer4 = new VidPeer("Peer4", "TestVid", "test", 6);

        new Thread(peer1).start();
//        new Thread(peer2).start();
//        new Thread(peer3).start();
        new Thread(peer4).start();

        while(true) Thread.sleep(10000L);
    }
}
