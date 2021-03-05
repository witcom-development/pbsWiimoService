package org.fincl.miss.server.channel.outbound.sender.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

import java.nio.charset.Charset;
import java.util.Properties;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.codec.tcp.StxEtxFrameDecoder;
import org.fincl.miss.server.codec.tcp.StxEtxFrameEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class OutBoundTcpInitializer extends ChannelInitializer<SocketChannel> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final String host;
    
    private final int port;
    
    private final SslContext sslCtx;
    
    private OutBoundTcpHandler outBoundTcpHandler;
    
    private OutBoundSender outBoundTcpSender;
    
    public OutBoundTcpInitializer(OutBoundSender outBoundTcpSender, OutBoundTcpHandler outBoundTcpHandler, String host, int port, SslContext sslCtx) {
        this.outBoundTcpSender = outBoundTcpSender;
        this.outBoundTcpHandler = outBoundTcpHandler;
        this.sslCtx = sslCtx;
        this.host = host;
        this.port = port;
    }
    
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        
        if (this.sslCtx != null) {
            pipeline.addLast(this.sslCtx.newHandler(ch.alloc(), host, port));
        }
        
        Properties serviceProps = (Properties) ApplicationContextSupport.getBean("serviceProps");
        String baseCharset = serviceProps.getProperty("channel.inbound.charset.convert", "BYPASS");
        Charset sourceCharset = Charset.forName(outBoundTcpSender.getOutBoundChannel().getCharsetEnum().toString());
        Charset targetCharset = null;
        
        pipeline.addLast(new StxEtxFrameDecoder(outBoundTcpSender.getOutBoundChannel()));
        pipeline.addLast(new StxEtxFrameEncoder(outBoundTcpSender.getOutBoundChannel()));
        // if (!"BYPASS".equals(baseCharset)) {
        // targetCharset = Charset.forName(baseCharset);
        // pipeline.addLast(new CharsetConvertDecoder(sourceCharset, targetCharset));
        // }
        // pipeline.addLast(new CharsetConvertEncoder(Charset.defaultCharset(), sourceCharset));
        if (logger.isDebugEnabled()) {
            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        // and then business logic.
        pipeline.addLast(this.outBoundTcpHandler);
    }
}