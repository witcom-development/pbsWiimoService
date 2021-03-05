package org.fincl.miss.server.channel.inbound.rest;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;

public class InBoundRestServerInitializer extends ChannelInitializer<SocketChannel> {
    
    private final SslContext sslCtx;
    private ListenInBoundChannel inBoundChannel;
    private InBoundRestServerHandler inBoundRestServerHandler;
    
    public InBoundRestServerInitializer(ListenInBoundChannel inBoundChannel, SslContext sslCtx) {
        this.inBoundChannel = inBoundChannel;
        this.sslCtx = sslCtx;
    }
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        
        // Add the text line codec combination first,
        // pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        // pipeline.addLast(DECODER);
        // pipeline.addLast(ENCODER);
        
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());
        
        // and then business logic.
        inBoundRestServerHandler = ApplicationContextSupport.getBean(InBoundRestServerHandler.class, new Object[] { this.inBoundChannel });
        pipeline.addLast(inBoundRestServerHandler);
    }
    
    public InBoundServerHandler getServerHandler() {
        return (InBoundServerHandler) this.inBoundRestServerHandler;
    }
}