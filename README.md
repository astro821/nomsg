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
import com.makequest.nomsg.test.inner.TestVo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PeerExample extends Thread implements NoMsgReceiverInterface {
    final NoMsgClient client;

    public PeerExample() throws NoMsgClientException {
        this.client = new NoMsgClient("ClientID");
        this.client.attach();
        this.client.join("SubscribeTopic");
        this.start();
    }

    public void sendTestVo(TestVo vo, String hostId, String clientId){
        this.client.sendDirect(hostId, clientId, 1, vo);
    }
    
    @Override
    public void OnReceiveMessage(NoMsgPeer noMsgPeer, NoMsgParser noMsgParser) {
        int objType = noMsgParser.getMessageId();
        switch (objType){
            case 1:
                TestVo vo = noMsgParser.getObject(TestVo.class);
                log.info(String.format("Receive message from [%s] : %s", noMsgPeer.getCId(), vo.toString()));
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
            case 1:
                TestVo vo = noMsgParser.getObject(TestVo.class);
                log.info(String.format("Receive message from [%s] : %s", noMsgPeer.getCId(), vo.toString()));
                break;
            default:
                log.info("Unknown object type");
                break;
        }

    }
}
```

## License