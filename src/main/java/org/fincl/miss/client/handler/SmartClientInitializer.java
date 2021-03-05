package org.fincl.miss.client.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.codec.tcp.StxEtxFrameDecoder;
import org.fincl.miss.server.codec.tcp.StxEtxFrameEncoder;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.DataRawType;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SmartClientInitializer extends ChannelInitializer<SocketChannel> {
    
    private final SslContext sslCtx;
    
    private final String host;
    
    private final String port;
    
    public SmartClientInitializer(String host, String port, SslContext sslCtx) {
        this.host = host;
        this.port = port;
        this.sslCtx = sslCtx;
    }
    
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), this.host, Integer.parseInt(this.port)));
        }
        
        ListenInBoundChannel extChannel = new ListenInBoundChannel(null, null);
        extChannel.setAutoStartYnEnum(AutoStartYn.YES);
        extChannel.setTxRawDataTypeEnum(DataRawType.STRING);
        extChannel.setHeaderLengthSize(4);
        extChannel.setStxSize(0);
        extChannel.setEtxSize(0);
      //  extChannel.setStxHex("");
      //  extChannel.setEtxHex();
        pipeline.addLast(new StxEtxFrameDecoder(extChannel));
        pipeline.addLast(new StxEtxFrameEncoder(extChannel));
        
 
        // Add the text line codec combination first,
        // pipeline.addLast(new LengthHeaderFrameDecoder(6));
        // pipeline.addLast(new LengthHeaderFrameEncoder(6));
        // pipeline.addLast(new StringDecoder());
        // pipeline.addLast(new StringEncoder());
        
        // and then business logic.
        pipeline.addLast(new SmartClientHandler());
    }
}