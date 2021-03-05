package org.fincl.miss.server.channel.inbound;

import org.fincl.miss.core.server.channel.InBoundChannel;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.channel.service.Channel;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;

public abstract class InBoundChannelImpl extends BoundChannel implements InBoundChannel {
    
    public InBoundChannelImpl(final ServiceHandler serviceHandler, final Channel channel) {
        super(serviceHandler, channel);
    }
    
    abstract public byte[] sendAndReceive(String clientId, byte[] message);
    
    abstract public boolean startup() throws ServerException;
    
    abstract public boolean shutdown() throws ServerException;
    
    abstract public ChannelStatus getStatus() throws ServerException;
    
}
