package org.fincl.miss.server.channel.inbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.service.Channel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlashPolicyInBoundChannel extends InBoundChannelImpl {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private NioEventLoopGroup bossGroup = null;
    private NioEventLoopGroup workerGroup = null;
    
    private ChannelFuture channelFuture = null;
    
    public FlashPolicyInBoundChannel(ServiceHandler serviceHandler, Channel channel) {
        super(serviceHandler, channel);
    }
    
    @Override
    public byte[] sendAndReceive(String clientId, byte[] message) {
        throw new ServerException(ErrorConstant.CHANNEL_NOT_SUPPORTED);
    }
    
    @Override
    public boolean startup() throws ServerException {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        ServerBootstrap bootStrap = new ServerBootstrap();
        bootStrap = bootStrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
        bootStrap = bootStrap.handler(new LoggingHandler(LogLevel.INFO));
        
        bootStrap = bootStrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                
                pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                    
                    private static final String NEWLINE = "\r\n";
                    
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        super.channelActive(ctx);
                    }
                    
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        super.channelInactive(ctx);
                    }
                    
                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        if (logger.isDebugEnabled()) {
                            cause.printStackTrace();
                        }
                        ctx.close();
                    }
                    
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf xmessage) throws Exception {
                        System.out.println("read!!!!!");
                        byte[] bMessage = new byte[xmessage.readableBytes()];
                        xmessage.readBytes(bMessage);
                        String sReq = StringUtils.trim(new String(bMessage));
                        System.out.println("[" + sReq + "]");
                        if (sReq.equals("<policy-file-request/>")) {
                            String sRes = "<?xml version=\"1.0\"?>" + NEWLINE + "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">" + NEWLINE + "" + NEWLINE + "<!-- Policy file for xmlsocket://socks.example.com -->" + NEWLINE + "<cross-domain-policy> " + NEWLINE + "" + NEWLINE + "   <!-- This is a master socket policy file -->" + NEWLINE + "   <!-- No other socket policies on the host will be permitted -->" + NEWLINE + "   <site-control permitted-cross-domain-policies=\"master-only\"/>" + NEWLINE + "" + NEWLINE + "   <!-- Instead of setting to-ports=\"*\", administrator's can use ranges and commas -->" + NEWLINE + "   <allow-access-from domain=\"*\" to-ports=\"*\" />" + NEWLINE + "" + NEWLINE + "</cross-domain-policy>" + NEWLINE;
                            
                            ByteBuf bf = Unpooled.wrappedBuffer(sRes.getBytes());
                            
                            ChannelFuture channelFuture = ctx.writeAndFlush(bf);
                            channelFuture.sync();
                        }
                        
                        ctx.close();
                    }
                });
            }
        });
        
        try {
            // monitoring.flash.policy.port
            Properties serverProps = (Properties) ApplicationContextSupport.getBean("serverProps");
            this.channelFuture = bootStrap.bind(Integer.parseInt(serverProps.getProperty("monitoring.flash.policy.port", "843"))).sync();
        }
        catch (NumberFormatException e) {
            throw new ServerException(ErrorConstant.CHANNEL_LOAD_ERROR, "InBoundChannel Port error.", e);
        }
        catch (InterruptedException e) {
            throw new ServerException(ErrorConstant.CHANNEL_LOAD_ERROR, "InBoundChannel Interrupted error.", e);
        }
        
        return true;
    }
    
    @Override
    public boolean shutdown() throws ServerException {
        boolean bResult = false;
        
        if (channelFuture != null && channelFuture.channel() != null && channelFuture.channel().isActive()) {
            
            channelFuture.channel().closeFuture();
            channelFuture.channel().disconnect();
            channelFuture.channel().close().awaitUninterruptibly();
            
            channelFuture.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    
                    logger.info("operationComplete {}", future);
                }
            });
            
            Future<?> bossGroupFuture = this.bossGroup.shutdownGracefully();
            Future<?> workerGroupFuture = this.workerGroup.shutdownGracefully();
            
            bossGroupFuture.awaitUninterruptibly();
            workerGroupFuture.awaitUninterruptibly();
            
            bResult = true;
        }
        
        return bResult;
    }
    
    @Override
    public ChannelStatus getStatus() throws ServerException {
        throw new ServerException(ErrorConstant.CHANNEL_NOT_SUPPORTED);
    }
    
    @Override
    public void send(String clientId, byte[] message) {
        // TODO Auto-generated method stub
        
    }
    
}
