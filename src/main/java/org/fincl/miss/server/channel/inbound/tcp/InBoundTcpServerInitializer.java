package org.fincl.miss.server.channel.inbound.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

import java.nio.charset.Charset;
import java.util.Properties;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;
import org.fincl.miss.server.codec.CharsetConvertDecoder;
import org.fincl.miss.server.codec.tcp.StxEtxFrameDecoder;
import org.fincl.miss.server.codec.tcp.StxEtxFrameEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InBoundTcpServerInitializer extends ChannelInitializer<SocketChannel> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final SslContext sslCtx;
    private ListenInBoundChannel inBoundChannel;
    InBoundTcpServerHandler inBoundTcpServerHandler;
    
    public InBoundTcpServerInitializer(ListenInBoundChannel inBoundChannel, SslContext sslCtx) {
        this.inBoundChannel = inBoundChannel;
        this.sslCtx = sslCtx;
        
    }
    
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        
        Properties serviceProps = (Properties) ApplicationContextSupport.getBean("serviceProps");
        String baseCharset = serviceProps.getProperty("channel.inbound.charset.convert", "BYPASS");
        
        Charset sourceCharset = Charset.forName(inBoundChannel.getCharsetEnum().toString());
        Charset targetCharset = null;
        
        pipeline.addLast(new StxEtxFrameDecoder(inBoundChannel));
        pipeline.addLast(new StxEtxFrameEncoder(inBoundChannel));
        
        if (!"BYPASS".equals(baseCharset)) {
            targetCharset = Charset.forName(baseCharset);
            pipeline.addLast(new CharsetConvertDecoder(sourceCharset, targetCharset));
        }
        // pipeline.addLast(new CharsetConvertEncoder(Charset.defaultCharset(), sourceCharset));
        // if (logger.isDebugEnabled()) {
        pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        // }
        // and then business logic.
        inBoundTcpServerHandler = ApplicationContextSupport.getBean(InBoundTcpServerHandler.class, new Object[] { this.inBoundChannel });
        pipeline.addLast(inBoundTcpServerHandler);
    }
    
    public InBoundServerHandler getServerHandler() {
        return (InBoundServerHandler) this.inBoundTcpServerHandler;
    }
}