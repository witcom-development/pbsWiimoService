package org.fincl.miss.server.channel.outbound.sender.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.SingleYn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutBoundWebSocketHandler extends SimpleChannelInboundHandler<Object> {
    
    private static Logger logger = LoggerFactory.getLogger(OutBoundWebSocketHandler.class);
    
    private OutBoundSender outBoundWebSocketSender;
    
    private SingleYn singleYn = SingleYn.NO;
    
    private ChannelFuture future;
    
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    
    public ChannelPromise getHandshakeFuture() {
        return handshakeFuture;
    }
    
    public void setHandshakeFuture(ChannelPromise handshakeFuture) {
        this.handshakeFuture = handshakeFuture;
    }
    
    private final BlockingQueue<byte[]> responseQueue = new LinkedBlockingQueue<byte[]>();
    
    public OutBoundWebSocketHandler(OutBoundSender outBoundWebSocketSender, WebSocketClientHandshaker handshaker) {
        super(false);
        this.outBoundWebSocketSender = outBoundWebSocketSender;
        
        this.singleYn = SingleYn.getEnum(outBoundWebSocketSender.getOutBoundChannel().getSingleYn());
        
        this.handshaker = handshaker;
    }
    
    public byte[] sendMessageAndReceive(Channel channel, byte[] bMessage) {
        
        // System.out.println("this [" + this + "]");
        // System.out.println("channel [" + this.channel + "]");
        // System.out.println("message [" + message + "]");
        ByteBuf bf = Unpooled.wrappedBuffer(bMessage);
        WebSocketFrame frame = new TextWebSocketFrame(bf);
        future = channel.writeAndFlush(frame);
        boolean interrupted = false;
        byte[] bRes;
        for (;;) {
            try {
                bRes = responseQueue.take();
                break;
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
        }
        
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        
        return bRes;
    }
    
    public void sendMessage(Channel channel, byte[] bMessage) {
        ByteBuf bf = Unpooled.wrappedBuffer(bMessage);
        WebSocketFrame frame = new TextWebSocketFrame(bf);
        future = channel.writeAndFlush(frame);
        if (future != null) {
            try {
                future.sync();
            }
            catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                throw new ServerException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e, bMessage);
            }
        }
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        handshaker.handshake(ctx.channel());
        outBoundWebSocketSender.getOutBoundChannel().getAllChannels().put(System.identityHashCode(ctx.channel()) + "", ctx);
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        outBoundWebSocketSender.getOutBoundChannel().getAllChannels().remove(ctx.channel().toString());
        logger.info("OutBoundChannel [ChannelID:{}] {} inactive.", outBoundWebSocketSender.getOutBoundChannel().getChannelId(), ctx.channel());
        
        // if (outBoundWebSocketSender instanceof OutBoundNoneSingleWebSocketSender) {
        // OutBoundNoneSingleWebSocketSender outBoundNoneSinleTcpsender = (OutBoundNoneSingleWebSocketSender) outBoundWebSocketSender;
        // outBoundNoneSinleTcpsender.getGroup().schedule(new Runnable() {
        // @Override
        // public void run() {
        // logger.info("OutBoundChannel channelInactive [ChannelID:{}] reconnect. {}.", outBoundWebSocketSender.getOutBoundChannel().getChannelId(), ctx.channel().closeFuture().cause());
        // outBoundWebSocketSender.startup();
        // }
        // }, 1L, TimeUnit.SECONDS);
        // }
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
        // System.err.println("received [" + message + "]");
        // System.err.println("=>" + Thread.currentThread().getId() + " ==>" + Thread.currentThread());
        //
        
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) message);
            System.out.println("WebSocket Client connected!");
            handshakeFuture.setSuccess();
            return;
        }
        
        if (message instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) message;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.getStatus() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        
        WebSocketFrame frame = (WebSocketFrame) message;
        if (frame instanceof TextWebSocketFrame) {
            
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            
            System.out.println("WebSocket Client received message: " + textFrame.text());
            byte[] bRes = new byte[textFrame.content().readableBytes()];
            textFrame.content().readBytes(bRes);
            responseQueue.add(bRes);
        }
        else if (frame instanceof PongWebSocketFrame) {
            System.out.println("WebSocket Client received pong");
        }
        else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client received closing");
            ch.close();
        }
        
        // Single 모드일 경우 응답을 받고 종료한다.
        if (singleYn == SingleYn.YES) {
            System.out.println(System.currentTimeMillis() + "]close=================================");
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}