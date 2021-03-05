package org.fincl.miss.server.channel.outbound.sender.tcp;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.TimeUnit;

import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutBoundTcpConnectionListener implements ChannelFutureListener {
    
    private static Logger logger = LoggerFactory.getLogger(OutBoundTcpConnectionListener.class);
    
    private OutBoundSender outBoundTcpSender;
    
    public OutBoundTcpConnectionListener(OutBoundSender outBoundTcpSender) {
        this.outBoundTcpSender = outBoundTcpSender;
    }
    
    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        
        if (!channelFuture.isSuccess()) {
            logger.info("OutBoundChannel operationComplete [ChannelID:{}] reconnect. {}.", outBoundTcpSender.getOutBoundChannel().getChannelId(), channelFuture.cause());
            OutBoundNoneSingleTcpSender outBoundNoneSingleSender = (OutBoundNoneSingleTcpSender) outBoundTcpSender;
            outBoundNoneSingleSender.getGroup().schedule(new Runnable() {
                @Override
                public void run() {
                    outBoundTcpSender.startup();
                }
            }, 1L, TimeUnit.SECONDS);
        }
        
    }
    
}
