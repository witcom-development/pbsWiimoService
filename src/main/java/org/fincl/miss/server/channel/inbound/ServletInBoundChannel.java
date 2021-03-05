package org.fincl.miss.server.channel.inbound;

import io.netty.channel.ChannelException;

import org.fincl.miss.server.channel.service.Channel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ServletInBoundChannel extends InBoundChannelImpl {
    
    public ServletInBoundChannel(ServiceHandler serviceHandler, Channel channelVO) {
        super(serviceHandler, channelVO);
    }
    
    @Override
    public boolean startup() throws ServerException {
        if (channelStatus == ChannelStatus.ALIVE) {
            return false;
        }
        else {
            channelStatus = ChannelStatus.ALIVE;
            return true;
        }
    }
    
    @Override
    public boolean shutdown() throws ChannelException {
        if (channelStatus == ChannelStatus.DEAD) {
            return false;
        }
        else {
            channelStatus = ChannelStatus.DEAD;
            return true;
        }
    }
    
    @Override
    public ChannelStatus getStatus() throws ChannelException {
        return channelStatus;
    }
    
    @Override
    public byte[] sendAndReceive(String clientId, byte[] message) {
        throw new ChannelException(ErrorConstant.CHANNEL_NOT_SUPPORTED);
    }
    
    @Override
    public void send(String clientId, byte[] message) {
        throw new ChannelException(ErrorConstant.CHANNEL_NOT_SUPPORTED);
    }
}
