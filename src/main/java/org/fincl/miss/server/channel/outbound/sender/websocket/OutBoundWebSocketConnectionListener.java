package org.fincl.miss.server.channel.outbound.sender.websocket;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.TimeUnit;

import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutBoundWebSocketConnectionListener implements ChannelFutureListener {
    
    private static Logger logger = LoggerFactory.getLogger(OutBoundWebSocketConnectionListener.class);
    
    private OutBoundSender outBoundWebSocketSender;
    
    public OutBoundWebSocketConnectionListener(OutBoundSender outBoundWebSocketSender) {
        this.outBoundWebSocketSender = outBoundWebSocketSender;
    }
    
    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        
        if (!channelFuture.isSuccess()) {
            logger.info("OutBoundChannel operationComplete [ChannelID:{}] reconnect. {}.", outBoundWebSocketSender.getOutBoundChannel().getChannelId(), channelFuture.cause());
            OutBoundNoneSingleWebSocketSender outBoundNoneSingleSender = (OutBoundNoneSingleWebSocketSender) outBoundWebSocketSender;
            outBoundNoneSingleSender.getGroup().schedule(new Runnable() {
                @Override
                public void run() {
                    outBoundWebSocketSender.startup();
                }
            }, 1L, TimeUnit.SECONDS);
        }
        
    }
    
}
