package org.fincl.miss.server.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.util.concurrent.LinkedBlockingQueue;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.exeption.BizServiceException;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.message.GuidTopicMessage;
import org.fincl.miss.server.message.GuidTopicMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ITopic;

public class SimpleTcpClient {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private String host;
    private int port;
    
    private EventLoopGroup group;
    
    private Channel channel;
    private ChannelFuture channelFuture;
    
    private LinkedBlockingQueue<byte[]> responseQueue = new LinkedBlockingQueue<byte[]>();
    
    public SimpleTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void init() {
        
        try {
            group = new OioEventLoopGroup();
            
            Bootstrap b = new Bootstrap();
            b.group(group).channel(OioSocketChannel.class).handler(new SimpleChannelInboundHandler<ByteBuf>() {
                
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    byte[] bRes = new byte[msg.readableBytes()];
                    msg.readBytes(bRes);
                    responseQueue.add(bRes);
                }
                
                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                    super.exceptionCaught(ctx, cause);
                    cause.printStackTrace();
                    ctx.close();
                }
            });
            
            // Start the connection attempt.
            channel = b.connect(this.host, this.port).sync().channel();
            
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_CONNECTION_ERROR, e);
        }
    }
    
    public void send(byte[] bMessage) {
        responseQueue.clear();
        
        init();
        
        ByteBuf bf = Unpooled.wrappedBuffer(bMessage);
        channelFuture = channel.writeAndFlush(bf);
        try {
            channelFuture.sync();
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_SENDMESSAGE_ERROR, e);
        }
        finally {
            close();
        }
    }
    
    public byte[] sendAndReceive(byte[] bMessage) {
        byte[] rMessage = null;
        
        responseQueue.clear();
        
        init();
        
        ByteBuf bf = Unpooled.wrappedBuffer(bMessage);
        channelFuture = channel.writeAndFlush(bf);
        try {
            channelFuture.sync();
            rMessage = responseQueue.take();
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_SENDMESSAGE_ERROR, e);
        }
        finally {
            close();
        }
        
        return rMessage;
    }
    
    public byte[] sendAndReceive(String guid, byte[] bMessage) {
        
        byte[] rMessage = null;
        
        responseQueue.clear();
        
        init();
        
        ByteBuf bf = Unpooled.wrappedBuffer(bMessage);
        channelFuture = channel.writeAndFlush(bf);
        
        ITopic<GuidTopicMessage> pubSubTopic = null;
        String listener = null;
        
        try {
            pubSubTopic = (ITopic<GuidTopicMessage>) ApplicationContextSupport.getBean("pubSubMessage");
            GuidTopicMessageListener guidTopicMessageListener = new GuidTopicMessageListener(guid, 30L); // 30sec
            listener = pubSubTopic.addMessageListener(guidTopicMessageListener);
            
            channelFuture = channel.writeAndFlush(bf);
            
            GuidTopicMessage guidTopicMessage = guidTopicMessageListener.getResponse();
            rMessage = guidTopicMessage.getPayload();
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_SENDMESSAGE_ERROR, e);
        }
        finally {
            if (pubSubTopic != null) {
                pubSubTopic.removeMessageListener(listener);
            }
            close();
        }
        
        return rMessage;
    }
    
    private void close() {
        try {
            channel.closeFuture().sync();
        }
        catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_CLOSE_ERROR, e);
        }
        group.shutdownGracefully();
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
}
