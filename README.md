NoMsg is a java library that used to delivers an objects from thread to thread, where each thread can be placed in a process that exists in the same process or on a different host.

## Goles

* [Now in progress] It can delivering an Java Object from thread to another thread with simple method.
* It can send object with self-composed adHoc network without any server process
* It can send Objects to one to one or one to many.

## Features

* Send Object to a specific destination
* Send Object to group destination

## Download

Maven: Not yet



## Example

```java
package com.makequest.test;

import com.makequest.nomsg.NoMsgClient;
import com.makequest.nomsg.NoMsgParser;
import com.makequest.nomsg.NoMsgPeer;
import com.makequest.nomsg.NoMsgReceiverInterface;
import com.makequest.nomsg.exception.NoMsgClientException;
import com.makequest.nomsg.exception.NoMsgNetworkException;
import com.makequest.nomsg.exception.NoMsgRouterException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class NoMsgExample extends Thread implements NoMsgReceiverInterface {
    final NoMsgClient client;

    @Data
    public static class DataUnitOne {
        int index;
        String one;
    }

    @Data
    public static class DataUnitTwo {
        int index;
        String two;
    }

    public NoMsgExample() throws NoMsgClientException {
        this.client = new NoMsgClient("ClientID-1");
        this.client.setHandler(this);
    }

    public static void main(String[] args) throws NoMsgClientException, NoMsgNetworkException, NoMsgRouterException {
        DataUnitOne unitOne = new DataUnitOne();
        unitOne.setIndex(1);
        unitOne.setOne("ONE");

        DataUnitTwo unitTwo = new DataUnitTwo();
        unitTwo.setIndex(1);
        unitTwo.setTwo("TWO");

        NoMsgExample example = new NoMsgExample();
        if (args.length == 0) {
            example.start();

            example.client.sendDirect("ClientID-2", 10, unitOne);
            example.client.sendBroadCast(null, null, 11, unitTwo);
            example.client.sendGroup("TestGroup", 11, unitTwo);

        } else if (args.length == 2){
            String remoteAddress = args[0];
            int remotePort = Integer.parseInt(args[1]);
            example.client.attach(InetSocketAddress.createUnresolved("0.0.0.0", 13854));
            List<InetSocketAddress> remotes = new LinkedList<>();
            remotes.add(InetSocketAddress.createUnresolved(remoteAddress, remotePort));
            example.client.addRemotes(remotes);
            example.start();

            example.client.sendDirect("RemoteHost", "ClientID-2", 10, unitOne);
            example.client.sendBroadCast(null, null, 11, unitTwo);
            example.client.sendGroup("TestGroup", 11, unitTwo);
        }
    }

    @Override
    public void OnReceiveMessage(NoMsgPeer noMsgPeer, NoMsgParser noMsgParser) {
        log.info("Message receivd from : " + noMsgPeer.toString());
        int objType = noMsgParser.getMessageId();
        switch (objType){
            case 10:
                DataUnitOne voOne = noMsgParser.getObject(DataUnitOne.class);
                log.info(String.format("Receive message from [%s] : %s", noMsgPeer.getCId(), voOne.toString()));
                break;
            case 11:
                DataUnitTwo voTwo = noMsgParser.getObject(DataUnitTwo.class);
                log.info(String.format("Receive message from [%s] : %s", noMsgPeer.getCId(), voTwo.toString()));
                break;
            default:
                log.info("Unknown object type");
                break;
        }
    }

    @Override
    public void OnReceiveMessage(NoMsgPeer noMsgPeer, String s, NoMsgParser noMsgParser) {
        int objType = noMsgParser.getMessageId();
        switch (objType){
            case 10:
                DataUnitOne voOne = noMsgParser.getObject(DataUnitOne.class);
                log.info(String.format("Receive message from [%s] : %s", noMsgPeer.getCId(), s, voOne.toString()));
                break;
            case 11:
                DataUnitTwo voTwo = noMsgParser.getObject(DataUnitTwo.class);
                log.info(String.format("Receive message from [%s] : %s", noMsgPeer.getCId(), s, voTwo.toString()));
                break;
            default:
                log.info("Unknown object type");
                break;
        }
    }
}

}
```

## License