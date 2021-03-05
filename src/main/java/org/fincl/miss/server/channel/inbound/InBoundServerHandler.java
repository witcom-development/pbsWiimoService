package org.fincl.miss.server.channel.inbound;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

import org.fincl.miss.server.exeption.ServerException;

public interface InBoundServerHandler {
    void send(ChannelHandlerContext ctx, byte[] message) throws ServerException;
    
    byte[] sendAndReceive(ChannelHandlerContext ctx, byte[] message) throws ServerException;
    
    InBoundChannelImpl getInBoundChannel();
    
    Map<String, Object> getHeader();
}
