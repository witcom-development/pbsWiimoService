package org.fincl.miss.service.server.inbound.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handles a client-side channel.
 */
@Sharable
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf xmessage) throws Exception {
        byte[] message = new byte[xmessage.readableBytes()];
        xmessage.readBytes(message);
        // TODO Auto-generated method stub
        System.err.println("client response message [" + new String(message) + "]");
        
    }
}