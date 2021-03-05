package org.fincl.miss.server.channel.outbound.sender.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;

import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class OutBoundWebSocketInitializer extends ChannelInitializer<SocketChannel> {
    
    private final String host;
    
    private final int port;
    
    private final SslContext sslCtx;
    
    private OutBoundWebSocketHandler outBoundWebSocketHandler;
    
    private OutBoundSender outBoundWebSocketSender;
    
    public OutBoundWebSocketInitializer(OutBoundSender outBoundWebSocketSender, OutBoundWebSocketHandler outBoundWebSocketHandler, String host, int port, SslContext sslCtx) {
        this.outBoundWebSocketSender = outBoundWebSocketSender;
        this.outBoundWebSocketHandler = outBoundWebSocketHandler;
        this.sslCtx = sslCtx;
        this.host = host;
        this.port = port;
    }
    
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        
        String uriScheme = "ws";
        
        if (this.sslCtx != null) {
            uriScheme = "wss";
            pipeline.addLast(this.sslCtx.newHandler(ch.alloc(), host, port));
        }
        
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(8192));
        
        // and then business logic.
        pipeline.addLast(this.outBoundWebSocketHandler);
    }
}