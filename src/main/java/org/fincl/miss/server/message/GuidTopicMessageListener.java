package org.fincl.miss.server.message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.OutBoundSenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

public class GuidTopicMessageListener implements MessageListener<GuidTopicMessage> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private String guid;
    
    private long timeout;
    
    private final BlockingQueue<GuidTopicMessage> responseQueue = new LinkedBlockingQueue<GuidTopicMessage>();
    
    public GuidTopicMessageListener(String guid, long timeout) {
        this.guid = guid;
        this.timeout = timeout;
    }
    
    @Override
    public void onMessage(Message<GuidTopicMessage> message) {
        logger.debug("onMessage:" + message.getMessageObject().getGuid());
        if (guid.equals(message.getMessageObject().getGuid())) {
            responseQueue.add(message.getMessageObject());
        }
    }
    
    public GuidTopicMessage getResponse() {
        GuidTopicMessage message = null;
        try {
            message = responseQueue.poll(timeout, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        return message;
    }
    
}