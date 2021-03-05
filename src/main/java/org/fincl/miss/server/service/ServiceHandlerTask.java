package org.fincl.miss.server.service;

import java.util.Map;
import java.util.concurrent.Callable;

import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.BoundChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("serviceHandlerTask")
public class ServiceHandlerTask implements Callable<Object> {
    
    private BoundChannel extChannel;
    private Map<String, Object> header;
    private byte[] bMessage;
    
    @Autowired
    private ServiceHandler serviceHandler;
    
    public ServiceHandlerTask() {
        
    }
    
    public ServiceHandlerTask(BoundChannel extChannel, Map<String, Object> header, byte[] bMessage) {
        this.extChannel = extChannel;
        this.header = header;
        this.bMessage = bMessage;
    }
    
    @EnableTraceLogging
    @Override
    public Object call() throws Exception {
        Object object = serviceHandler.handle(extChannel, header, bMessage);
        return object;
    }
    
}
