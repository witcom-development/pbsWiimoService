package org.fincl.miss.server.service;

import java.util.concurrent.Callable;

import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("serviceInvokerTask")
public class ServiceInvokerTask implements Callable<Object> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ServiceInvoker invoker;
    
    private MessageInterfaceVO interfaceIdVo;
    
    private MessageHeader messageHeader;
    
    public ServiceInvokerTask() {
        
    }
    
    public ServiceInvokerTask(ServiceInvoker invoker, MessageInterfaceVO interfaceIdVo, MessageHeader messageHeader) {
        this.invoker = invoker;
        this.interfaceIdVo = interfaceIdVo;
        this.messageHeader = messageHeader;
    }
    
    @EnableTraceLogging
    @Override
    public Object call() throws Exception {
        Object object = invoker.invoke(interfaceIdVo, messageHeader);
        return object;
    }
    
}
