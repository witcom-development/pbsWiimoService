package org.fincl.miss.server.channel.inbound.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;

public class InBoundWebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    
    private final SslContext sslCtx;
    private ListenInBoundChannel inBoundChannel;
    private InBoundWebSocketServerHandler inBoundWebSocketServerHandler;
    
    public InBoundWebSocketServerInitializer(ListenInBoundChannel inBoundChannel, SslContext sslCtx) {
        this.inBoundChannel = inBoundChannel;
        this.sslCtx = sslCtx;
    }
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        
        InBoundWebSocketServerHandler inBoundWebSocketServerHandler = ApplicationContextSupport.getBean(InBoundWebSocketServerHandler.class, new Object[] { this.inBoundChannel });
        // and then business logic.
        pipeline.addLast(inBoundWebSocketServerHandler);
    }
    
    public InBoundServerHandler getServerHandler() {
        return (InBoundServerHandler) this.inBoundWebSocketServerHandler;
    }
}