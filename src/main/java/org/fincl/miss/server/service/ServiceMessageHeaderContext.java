package org.fincl.miss.server.service;

import io.netty.util.concurrent.FastThreadLocal;

import org.fincl.miss.server.message.MessageHeader;

public class ServiceMessageHeaderContext {
    
    private static final FastThreadLocal<MessageHeader> MESSAGE_HEADER = new FastThreadLocal<MessageHeader>() {
        @Override
        protected MessageHeader initialValue() throws Exception {
            return new MessageHeader(null);
        };
    };
    
    public static void setMessageHeader(MessageHeader messageHeader) {
        MESSAGE_HEADER.set(messageHeader);
    }
    
    public static MessageHeader getMessageHeader() {
        return MESSAGE_HEADER.get();
    }
    
    public static void remove() {
        MESSAGE_HEADER.remove();
    }
    
}
