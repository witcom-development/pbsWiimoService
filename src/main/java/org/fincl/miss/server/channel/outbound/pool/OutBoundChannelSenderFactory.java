package org.fincl.miss.server.channel.outbound.pool;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class OutBoundChannelSenderFactory extends BasePoolableObjectFactory<OutBoundSender> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private OutBoundChannelImpl outBoundChannel;
    private Class<OutBoundSender> outBoundSenderClazz;
    
    public OutBoundChannelSenderFactory() {
        
    }
    
    public OutBoundChannelSenderFactory(OutBoundChannelImpl outBoundChannel, Class<OutBoundSender> outBoundSenderClazz) {
        this.outBoundChannel = outBoundChannel;
        this.outBoundSenderClazz = outBoundSenderClazz;
    }
    
    @Override
    public OutBoundSender makeObject() throws Exception {
        // OutBoundSender oubBoundSender = (OutBoundSender) outBoundSender.getDeclaredConstructor(OutBoundChannel.class).newInstance(outBoundChannel);
        OutBoundSender outBoundSender = ApplicationContextSupport.getBean(outBoundSenderClazz, new Object[] { outBoundChannel });
        
        return outBoundSender;
    }
    
    @Override
    public void destroyObject(OutBoundSender obj) throws Exception {
        logger.debug("OutBoundChannelSenderFactory.destroyObject");
        obj.shutdown();
        super.destroyObject(obj);
    }
}
