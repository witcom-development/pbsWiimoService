package org.fincl.miss.server.channel.inbound.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.FastThreadLocal;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.logging.profile.TraceLog;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.service.ServiceHandlerTask;
import org.fincl.miss.server.util.EnumCode.SingleYn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Handles a server-side channel.
 */

@Scope("prototype")
@Component
@Sharable
public class InBoundTcpServerHandler extends SimpleChannelInboundHandler<ByteBuf> implements InBoundServerHandler {
    
    private static Logger logger = LoggerFactory.getLogger(InBoundTcpServerHandler.class);
    
    private static enum State {
        SEND_WAIT_RECEIVE, NORMARL_WAIT_RECEIVE
    }
    
    private final AttributeKey<State> STATE_ATTRIBUTE = AttributeKey.valueOf("state");
    
    @Autowired
    private ServiceHandler serviceHandler;
    // private ExtMessage resMessage;
    
    @Autowired
    private ThreadPoolTaskExecutor handlerTaskExecutor;
    
    private byte[] resMessage;
    
    private Map<String, Object> tcpHeaders = new LinkedHashMap<String, Object>();
    
    private ListenInBoundChannel inBoundChannel;
    
    private SingleYn singleYn = SingleYn.NO;
    
    private BlockingQueue<byte[]> responseQueue = null;
    
    private ChannelFuture future;
    
    public static FastThreadLocal<List<TraceLog>> traceLog = new FastThreadLocal<List<TraceLog>>() {
        @Override
        protected java.util.List<TraceLog> initialValue() throws Exception {
            return new ArrayList<TraceLog>();
        }
    };
    
    public InBoundTcpServerHandler() {
    }
    
    public InBoundTcpServerHandler(ListenInBoundChannel inBoundChannel) {
        this.inBoundChannel = inBoundChannel;
        this.singleYn = SingleYn.getEnum(inBoundChannel.getSingleYn());
        
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.inBoundChannel.getAllChannels().put(System.identityHashCode(ctx.channel()) + "", ctx);
        ctx.attr(STATE_ATTRIBUTE).set(State.NORMARL_WAIT_RECEIVE);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.inBoundChannel.getAllChannels().remove(System.identityHashCode(ctx.channel()) + "");
        super.channelInactive(ctx);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("cause::" + cause);
        if (logger.isDebugEnabled()) {
            cause.printStackTrace();
        }
        ctx.close();
    }
    
    /**
     * 선언해 주어야 ExceptionHandler Aop에서 Catch가 됨.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf xmessage) throws Exception {
        System.out.println("read!!!!!!!!!!!!!!!!!!");
        if (ctx.attr(STATE_ATTRIBUTE).get() == State.SEND_WAIT_RECEIVE) {
            byte[] gg = new byte[xmessage.readableBytes()];
            xmessage.readBytes(gg);
            responseQueue.add(gg);
            
            if (this.singleYn == SingleYn.YES) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            ctx.attr(STATE_ATTRIBUTE).set(State.NORMARL_WAIT_RECEIVE);
            return;
        }
        
        byte[] message = new byte[xmessage.readableBytes()];
        xmessage.readBytes(message);
        
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        InetAddress inetaddress = socketAddress.getAddress();
        String remoteAddress = inetaddress.getHostAddress();
        
        tcpHeaders.put(MessageHeader.CLIENT_IP, remoteAddress);
        
        ServiceHandlerTask serviceHandlerTask = ApplicationContextSupport.getBean(ServiceHandlerTask.class, new Object[] { inBoundChannel, tcpHeaders, message });
        try {
            Future<Object> futureTask = handlerTaskExecutor.submit(serviceHandlerTask);
            resMessage = (byte[]) futureTask.get(inBoundChannel.getTimeoutSeconds(), TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.CHANNEL_ERROR, message);
        }
        catch (ExecutionException e) {
            ServerException ex = (ServerException) e.getCause();
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            if (ex.getParserMessage() == null) {
                ex.setParserMessage(message);
            }
            throw ex;
        }
        catch (TimeoutException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.CHANNEL_ERROR, e, message);
        }
        catch (Exception e) {
            System.out.println("aabbb:" + e);
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.CHANNEL_ERROR, e, message);
        }
        try{
        	ChannelFuture future = ctx.writeAndFlush(resMessage);
        	 future.sync();
             // Single 모드일 경우 응답 후 연결을 종료한다.
             if (this.singleYn == SingleYn.YES) {
                 future.addListener(ChannelFutureListener.CLOSE);
             }
        }catch(Exception e){}
       
    }
    
    @Override
    public void send(ChannelHandlerContext ctx, byte[] message) {
        future = ctx.channel().writeAndFlush(message);
        try {
            future.sync();
        }
        catch (InterruptedException e) {
            throw new ServerException(ErrorConstant.CHANNEL_ERROR, e);
        }
    }
    
    @Override
    public byte[] sendAndReceive(ChannelHandlerContext ctx, byte[] message) {
        ctx.attr(STATE_ATTRIBUTE).set(State.SEND_WAIT_RECEIVE);
        responseQueue = new LinkedBlockingQueue<byte[]>();
        
        future = ctx.channel().writeAndFlush(message);
        
        boolean interrupted = false;
        byte[] response;
        for (;;) {
            try {
                response = responseQueue.take();
                break;
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
        }
        
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        
        responseQueue = null;
        ctx.attr(STATE_ATTRIBUTE).set(State.NORMARL_WAIT_RECEIVE);
        
        return response;
    }
    
    @Override
    public InBoundChannelImpl getInBoundChannel() {
        return this.inBoundChannel;
    }
    
    @Override
    public Map<String, Object> getHeader() {
        return this.tcpHeaders;
    }
    
}