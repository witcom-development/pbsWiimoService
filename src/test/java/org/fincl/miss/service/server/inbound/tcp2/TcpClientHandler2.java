package org.fincl.miss.service.server.inbound.tcp2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.xml.bind.DatatypeConverter;

/**
 * Handles a client-side channel.
 */
@Sharable
public class TcpClientHandler2 extends SimpleChannelInboundHandler<ByteBuf> {
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelActive(ctx);
        
        System.out.println("acccc");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf xmessage) throws Exception {
        byte[] message = new byte[xmessage.readableBytes()];
        xmessage.readBytes(message);
        System.err.println("client response message [" + DatatypeConverter.printHexBinary(message) + "]");
        
    }
}