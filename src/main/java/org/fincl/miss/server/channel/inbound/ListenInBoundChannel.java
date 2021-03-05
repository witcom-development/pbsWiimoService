package org.fincl.miss.server.channel.inbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.Future;

import java.security.cert.CertificateException;
import java.util.Iterator;

import javax.net.ssl.SSLException;

import org.fincl.miss.server.channel.inbound.http.InBoundHttpServerInitializer;
import org.fincl.miss.server.channel.inbound.rest.InBoundRestServerInitializer;
import org.fincl.miss.server.channel.inbound.tcp.InBoundTcpServerInitializer;
import org.fincl.miss.server.channel.inbound.websocket.InBoundWebSocketServerInitializer;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.ProtocolType;
import org.fincl.miss.server.util.EnumCode.SSLYn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class ListenInBoundChannel extends InBoundChannelImpl {
    
    final private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private NioEventLoopGroup bossGroup;
    
    private NioEventLoopGroup workerGroup;
    
    private ChannelFuture channelFuture;
    
    private Channel channel;
    
    public ListenInBoundChannel(final ServiceHandler serviceHandler, final org.fincl.miss.server.channel.service.Channel channel) {
        super(serviceHandler, channel);
    }
    
    @Override
    public boolean startup() throws ServerException {
        boolean bResult = false;
        
        final SslContext sslCtx;
        
        if (SSLYn.getEnum(getSslYn()) == SSLYn.YES) {
            
            try {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            }
            catch (SSLException e) {
                throw new ServerException(ErrorConstant.CHANNEL_LOAD_ERROR, "InBoundChannel SSL Context create error.", e);
            }
            catch (CertificateException e) {
                throw new ServerException(ErrorConstant.CHANNEL_LOAD_ERROR, "InBoundChannel SSL Certificate create error.", e);
            }
        }
        else {
            sslCtx = null;
        }
        
        // bossGroup = new NioEventLoopGroup(100, NamedExecutors.newThreadFactory("InBoundChannel-Boss-" + getChannelId()));
        // bossGroup = new NioEventLoopGroup(100, NamedExecutors.newThreadFactory("InBoundChannel-Boss-" + getChannelId()));
        bossGroup = new NioEventLoopGroup(1);
        // workerGroup = new NioEventLoopGroup(1024, NamedExecutors.newThreadFactory("InBoundChannel-Worker-" + getChannelId()));
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootStrap = new ServerBootstrap();
        bootStrap = bootStrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        bootStrap = bootStrap.handler(new LoggingHandler(LogLevel.INFO));
        
        ProtocolType protocolType = getProtocolTypeEnum();
        if (protocolType == ProtocolType.TCP) {
            InBoundTcpServerInitializer inBoundTcpServerInitializer = new InBoundTcpServerInitializer(this, sslCtx);
            bootStrap = bootStrap.childHandler(inBoundTcpServerInitializer);
        }
        else if (protocolType == ProtocolType.HTTP) {
            InBoundHttpServerInitializer inBoundHttpServerInitializer = new InBoundHttpServerInitializer(this, sslCtx);
            bootStrap = bootStrap.childHandler(inBoundHttpServerInitializer);
        }
        else if (protocolType == ProtocolType.REST) {
            InBoundRestServerInitializer inBoundRestServerInitializer = new InBoundRestServerInitializer(this, sslCtx);
            bootStrap = bootStrap.childHandler(inBoundRestServerInitializer);
        }
        else if (protocolType == ProtocolType.WEBSOCKET) {
            InBoundWebSocketServerInitializer inBoundRestServerInitializer = new InBoundWebSocketServerInitializer(this, sslCtx);
            bootStrap = bootStrap.childHandler(inBoundRestServerInitializer);
        }
        else {
            throw new ServerException(ErrorConstant.CHANNEL_LOAD_ERROR, "unkown protocolType :" + protocolType);
        }
        
        bootStrap.option(ChannelOption.SO_BACKLOG, backlog).option(ChannelOption.SO_REUSEADDR, reUseAddress).option(ChannelOption.SO_KEEPALIVE, keepAlive).option(ChannelOption.TCP_NODELAY, tcpNoDelay).option(ChannelOption.SO_SNDBUF, sndBuf).option(ChannelOption.SO_RCVBUF, rcvBuf).option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, wbHigh).option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, wbLow).option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(rcvBuf))
        
        .childOption(ChannelOption.SO_REUSEADDR, reUseAddress).childOption(ChannelOption.SO_KEEPALIVE, keepAlive).childOption(ChannelOption.TCP_NODELAY, tcpNoDelay).childOption(ChannelOption.SO_SNDBUF, sndBuf).childOption(ChannelOption.SO_RCVBUF, rcvBuf).childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, wbHigh).childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, wbLow).childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(rcvBuf));
        
        try {
            channelFuture = bootStrap.bind(getPort()).sync();
            setChannel(channelFuture.channel());
        }
        catch (NumberFormatException e) {
            throw new ServerException(ErrorConstant.CHANNEL_LOAD_ERROR, "InBoundChannel Port error.", e);
        }
        catch (InterruptedException e) {
            throw new ServerException(ErrorConstant.CHANNEL_LOAD_ERROR, "InBoundChannel Interrupted error.", e);
        }
        
        bResult = true;
        
        return bResult;
    }
    
    // 이방법밖에 없나? 현재 멀티쓰레드로 호출시 데이터꼬이는...? 한번 주고 받으면 끝나야 하는데, 계속 주고 받으면서 데이타가 커짐.(길이 필드 때문에)
    // synchronized 있어야 될까...?
    @Override
    public byte[] sendAndReceive(String clientId, byte[] message) {
        ChannelHandlerContext context = getAllChannels().get(clientId);
        ChannelHandler handler = context.handler();
        InBoundServerHandler serverHandler = (InBoundServerHandler) handler;
        if (context.channel().isWritable()) {
            byte[] rMessage = serverHandler.sendAndReceive(context, message);
            return rMessage;
        }
        else {
            return null;
        }
    }
    
    public void broadcastTrafficMessage(byte[] message) {
        Iterator<String> it = getAllChannels().keySet().iterator();
        while (it.hasNext()) {
            String clientId = it.next();
            ChannelHandlerContext context = getAllChannels().get(clientId);
            ChannelHandler handler = context.handler();
            InBoundServerHandler serverHandler = (InBoundServerHandler) handler;
            if (context.channel().isWritable()) {
                serverHandler.send(context, message);
            }
        }
    }
    
    @Override
    public boolean shutdown() throws ServerException {
        boolean bResult = false;
        
        if (getChannel() != null && getChannel().isActive()) {
            
            // 접속해 있는 모든 Client들의 연결을 종료
            Iterator<String> itClients = getAllChannels().keySet().iterator();
            while (itClients.hasNext()) {
                getAllChannels().get(itClients.next()).channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
            
            getChannel().closeFuture();
            getChannel().disconnect();
            getChannel().close().awaitUninterruptibly();
            
            // channelFuture.addListener(new ChannelFutureListener() {
            // public void operationComplete(ChannelFuture future) {
            //
            // logger.info("operationComplete {}", future);
            // }
            // });
            
            Future<?> bossGroupFuture = this.bossGroup.shutdownGracefully();
            Future<?> workerGroupFuture = this.workerGroup.shutdownGracefully();
            
            bossGroupFuture.awaitUninterruptibly();
            workerGroupFuture.awaitUninterruptibly();
            
            bResult = true;
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
    public void send(String clientId, byte[] message) {
        // TODO Auto-generated method stub
        
    }
}
