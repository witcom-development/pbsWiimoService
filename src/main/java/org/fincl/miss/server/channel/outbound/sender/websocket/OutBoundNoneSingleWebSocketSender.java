package org.fincl.miss.server.channel.outbound.sender.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.Future;

import java.net.URI;

import javax.net.ssl.SSLException;

import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.concurrent.NamedExecutors;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.OutBoundSenderException;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.SSLYn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class OutBoundNoneSingleWebSocketSender extends OutBoundSender {
    
    private static Logger logger = LoggerFactory.getLogger(OutBoundNoneSingleWebSocketSender.class);
    
    private NioEventLoopGroup group;
    
    private ChannelFuture lastWriteFuture;
    
    private Channel channel;
    
    private OutBoundWebSocketHandler outBoundWebSocketHandler;
    
    public OutBoundNoneSingleWebSocketSender() {
        
    }
    
    public OutBoundNoneSingleWebSocketSender(OutBoundChannelImpl outBoundChannel) {
        super(outBoundChannel);
        
        if (autoStartYn == AutoStartYn.YES) {
            startup();
        }
        
    }
    
    @EnableTraceLogging
    @Override
    public byte[] sendAndReceive(byte[] bMessage) throws OutBoundSenderException {
        return this.sendMessage(bMessage, true);
    }
    
    @Override
    public void send(byte[] bMessage) throws OutBoundSenderException {
        this.sendMessage(bMessage, false);
    }
    
    private byte[] sendMessage(byte[] bMessage, boolean bReceive) throws OutBoundSenderException {
        byte[] bRes = null;
        if (bReceive) {
            bRes = outBoundWebSocketHandler.sendMessageAndReceive(channel, bMessage);
        }
        else {
            outBoundWebSocketHandler.sendMessage(channel, bMessage);
        }
        return bRes;
    }
    
    @Override
    public boolean startup() throws ServerException {
        boolean bResult = false;
        
        final SslContext sslCtx;
        
        String uriScheme = "ws";
        
        if (sslYn == SSLYn.YES) {
            uriScheme = "wss";
            try {
                sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
            }
            catch (SSLException e) {
                throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
            }
        }
        else {
            sslCtx = null;
        }
        
        if (group == null) {
            group = new NioEventLoopGroup(1, NamedExecutors.newThreadFactory("OutBoundChannel-" + outBoundChannel.getChannelId()));
        }
        
        Bootstrap bootStrap = new Bootstrap();
        bootStrap = bootStrap.group(group);
        bootStrap = bootStrap.channel(NioSocketChannel.class);
        bootStrap.option(ChannelOption.SO_BACKLOG, outBoundChannel.backlog).option(ChannelOption.SO_REUSEADDR, outBoundChannel.reUseAddress).option(ChannelOption.SO_KEEPALIVE, outBoundChannel.keepAlive).option(ChannelOption.TCP_NODELAY, outBoundChannel.tcpNoDelay).option(ChannelOption.SO_SNDBUF, outBoundChannel.sndBuf).option(ChannelOption.SO_RCVBUF, outBoundChannel.rcvBuf).option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, outBoundChannel.wbHigh).option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, outBoundChannel.wbLow).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(outBoundChannel.rcvBuf));
        
        URI uri = URI.create(uriScheme + "://" + outBoundChannel.getOutBoundChannelHost() + ":" + outBoundChannel.getPort() + "" + outBoundChannel.getOutBoundHttpUri());
        this.outBoundWebSocketHandler = new OutBoundWebSocketHandler(this, WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
        OutBoundWebSocketInitializer outBoundWebSocketInitializer = new OutBoundWebSocketInitializer(this, outBoundWebSocketHandler, outBoundChannel.getOutBoundChannelHost(), outBoundChannel.getPort(), sslCtx);
        
        bootStrap = bootStrap.handler(outBoundWebSocketInitializer);
        
        logger.debug("host : {}, port : {}", outBoundChannel.getOutBoundChannelHost(), outBoundChannel.getPort());
        
        try {
            channel = bootStrap.connect(outBoundChannel.getOutBoundChannelHost(), outBoundChannel.getPort()).addListener(new OutBoundWebSocketConnectionListener(this)).sync().channel();
            this.outBoundWebSocketHandler.getHandshakeFuture().sync();
            if (outBoundChannel.getCloseListener() != null) {
                ChannelFuture closeFuture = channel.closeFuture();
                closeFuture.addListener(outBoundChannel.getCloseListener());
            }
            bResult = true;
        }
        catch (NumberFormatException e) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (InterruptedException e) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        finally {
            if (!bResult) {
                group.shutdownGracefully();
                group = null;
            }
        }
        
        logger.debug("channel info : {}", getChannel());
        logger.debug("channel info : {}", getChannel().isActive());
        
        return bResult;
        
    }
    
    public NioEventLoopGroup getGroup() {
        return group;
    }
    
    public void setGroup(NioEventLoopGroup group) {
        this.group = group;
    }
    
    @Override
    public boolean shutdown() throws ServerException {
        boolean bResult = false;
        
        try {
            if (this.lastWriteFuture != null) {
                this.lastWriteFuture.sync();
            }
            getChannel().closeFuture();
            getChannel().disconnect();
            // getChannel().close().awaitUninterruptibly();
            getChannel().close().awaitUninterruptibly();
            Future<?> future = this.group.shutdownGracefully();
            future.awaitUninterruptibly();
            
            bResult = true;
        }
        catch (InterruptedException e) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        return bResult;
        
    }
    
    public Channel getChannel() {
        return channel;
    }
    
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    
    @Override
    public ChannelStatus getStatus() throws ServerException {
        ChannelStatus channelStatus = ChannelStatus.DEAD;
        if (getChannel() != null) {
            if (getChannel().isActive() && getChannel().isOpen()) {
                channelStatus = ChannelStatus.ALIVE;
            }
            else {
                channelStatus = ChannelStatus.DEAD;
            }
        }
        else {
            channelStatus = ChannelStatus.DEAD;
        }
        return channelStatus;
    }
    
    @Override
    protected void finalize() throws Throwable {
        
        System.out.println("OutBoundNoneSingleTcpSender finalize========");
        
        shutdown();
        
        super.finalize();
    }
    
}
