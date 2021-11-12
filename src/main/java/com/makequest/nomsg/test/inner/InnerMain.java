package com.makequest.nomsg.test.inner;

public class InnerMain {
    public static void main(String[] args) throws InterruptedException {
        new Peer("Peer1", "Peer2", 3);
        new Peer("Peer2", "Peer1", 5);

        while(true) Thread.sleep(10000L);
    }
}
