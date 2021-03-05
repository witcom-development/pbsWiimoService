package org.fincl.miss.server.channel.outbound.sender.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.Future;

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
public class OutBoundNoneSingleTcpSender extends OutBoundSender implements OutBoundTcpSender {
    
    private static Logger logger = LoggerFactory.getLogger(OutBoundNoneSingleTcpSender.class);
    
    private NioEventLoopGroup group;
    
    private ChannelFuture lastWriteFuture;
    
    private Channel channel;
    
    private OutBoundTcpHandler outBoundChannelHandler;
    
    public OutBoundNoneSingleTcpSender() {
        
    }
    
    public OutBoundNoneSingleTcpSender(OutBoundChannelImpl outBoundChannel) {
        super(outBoundChannel);
        
        // Single 모드일 경우는 AutoStart일지라도 startup을 수행하지 않는다.
        if (autoStartYn == AutoStartYn.YES) {
            startup();
        }
    }
    
    @EnableTraceLogging
    @Override
    public void send(byte[] bMessage) throws OutBoundSenderException {
        if (outBoundChannelHandler == null) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, this.getClass() + " handler not initialized.");
        }
        else {
            outBoundChannelHandler.sendMessage(channel, bMessage);
        }
    }
    
    @Override
    public byte[] sendAndReceive(byte[] bMessage) throws OutBoundSenderException {
        byte[] bRes = null;
        if (outBoundChannelHandler == null) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, this.getClass() + " handler not initialized.");
        }
        else {
            bRes = outBoundChannelHandler.sendMessageAndReceive(channel, bMessage);
        }
        return bRes;
    }
    
    @Override
    public boolean startup() throws OutBoundSenderException {
        boolean bResult = false;
        
        final SslContext sslCtx;
        
        if (sslYn == SSLYn.YES) {
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
        bootStrap.option(ChannelOption.SO_REUSEADDR, outBoundChannel.reUseAddress).option(ChannelOption.SO_KEEPALIVE, outBoundChannel.keepAlive).option(ChannelOption.TCP_NODELAY, outBoundChannel.tcpNoDelay).option(ChannelOption.SO_SNDBUF, outBoundChannel.sndBuf).option(ChannelOption.SO_RCVBUF, outBoundChannel.rcvBuf).option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, outBoundChannel.wbHigh).option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, outBoundChannel.wbLow).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(outBoundChannel.rcvBuf));
        
        this.outBoundChannelHandler = new OutBoundTcpHandler(this);
        
        OutBoundTcpInitializer outBoundChannelInitializer = new OutBoundTcpInitializer(this, outBoundChannelHandler, outBoundChannel.getOutBoundChannelHost(), outBoundChannel.getPort(), sslCtx);
        
        bootStrap = bootStrap.handler(outBoundChannelInitializer);
        
        logger.debug("host : {}, port : {}", outBoundChannel.getOutBoundChannelHost(), outBoundChannel.getPort());
        
        try {
            // channel = bootStrap.connect(outBoundChannel.getOutBoundChannelHost(), outBoundChannel.getPort()).addListener(new OutBoundTcpConnectionListener(this)).sync().channel();
            channel = bootStrap.connect(outBoundChannel.getOutBoundChannelHost(), outBoundChannel.getPort()).sync().channel();
            
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
        catch (Exception e) {
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
    public boolean shutdown() throws OutBoundSenderException {
        boolean bResult = false;
        
        System.out.println("shutdown start!!!!!!!!!!!!!!!!!!!!");
        try {
            if (this.lastWriteFuture != null) {
                this.lastWriteFuture.sync();
            }
            // System.out.println("shutdown xxxx 11111111");
            // getChannel().closeFuture();
            // System.out.println("shutdown xxxx 22222222");
            // getChannel().disconnect();
            // System.out.println("shutdown xxxx 33333333");
            // // getChannel().close().awaitUninterruptibly();
            // getChannel().close().awaitUninterruptibly();
            // System.out.println("shutdown xxxx 44444444");
            // Future<?> future = this.group.shutdownGracefully();
            // System.out.println("shutdown xxxx 55555555");
            // // future.awaitUninterruptibly();
            // System.out.println("shutdown xxxx 66666666");
            // bResult = true;
            System.out.println("shutdown xxxx 11111111");
            getChannel().closeFuture();
            System.out.println("shutdown xxxx 22222222");
            getChannel().disconnect();
            System.out.println("shutdown xxxx 33333333");
            // getChannel().close().awaitUninterruptibly();
            System.out.println("shutdown xxxx 44444444");
            // channelFuture.addListener(new ChannelFutureListener() {
            // public void operationComplete(ChannelFuture future) {
            //
            // logger.info("operationComplete {}", future);
            // }
            // });
            
            Future<?> groupFuture = this.group.shutdownGracefully();
            
            // groupFuture.awaitUninterruptibly();
            
            bResult = true;
        }
        catch (InterruptedException e) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        System.out.println("shutdown send!!!!!!!!!!!!!!!!!!!!");
        
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
        shutdown();
        super.finalize();
    }
    
}
