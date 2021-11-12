package com.makequest.nomsg.test.inner;

public class InnerMain {
    public static void main(String[] args) throws InterruptedException {
        Peer peer1 = new Peer("Peer1", "Peer2", 3);
        Peer peer2 = new Peer("Peer2", "Peer1", 5);

        new Thread(peer1).start();
        new Thread(peer2).start();

        while(true) Thread.sleep(10000L);
    }
}
