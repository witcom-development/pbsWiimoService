package org.fincl.miss.server.channel.inbound.websocket;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.SSLYn;
import org.fincl.miss.server.util.EnumCode.SingleYn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Handles a server-side channel.
 */

@Scope("prototype")
@Component
@Sharable
public class InBoundWebSocketServerHandler extends SimpleChannelInboundHandler<Object> implements InBoundServerHandler {
    
    private static Logger logger = LoggerFactory.getLogger(InBoundWebSocketServerHandler.class);
    
    private static enum State {
        SEND_WAIT_RECEIVE, NORMARL_WAIT_RECEIVE
    }
    
    private final AttributeKey<State> STATE_ATTRIBUTE = AttributeKey.valueOf("state");
    
    @Autowired
    private ServiceHandler serviceHandler;
    
    private Map<String, Object> webSocketHeaders = new LinkedHashMap<String, Object>();
    
    private ListenInBoundChannel inBoundChannel;
    
    private SingleYn singleYn = SingleYn.NO;
    
    private SSLYn sslYn = SSLYn.NO;
    
    private static final String WEBSOCKET_PATH = "/";
    
    private WebSocketServerHandshaker handshaker;
    
    private ChannelFuture future;
    
    private BlockingQueue<byte[]> responseQueue = null;
    
    public InBoundWebSocketServerHandler() {
        
    }
    
    public InBoundWebSocketServerHandler(ListenInBoundChannel inBoundChannel) {
        this.inBoundChannel = inBoundChannel;
        
        this.singleYn = SingleYn.getEnum(inBoundChannel.getSingleYn());
        this.sslYn = SSLYn.getEnum(inBoundChannel.getSslYn());
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.inBoundChannel.getAllChannels().put(System.identityHashCode(ctx.channel()) + "", ctx);
        ctx.attr(STATE_ATTRIBUTE).set(State.NORMARL_WAIT_RECEIVE);
        
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.inBoundChannel.getAllChannels().remove(System.identityHashCode(ctx.channel()) + "");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
        System.out.println("message [" + message + "] " + this);
        try {
            if (message instanceof FullHttpRequest) {
                System.out.println(1111);
                handleHttpRequest(ctx, (FullHttpRequest) message);
            }
            else if (message instanceof WebSocketFrame) {
                System.out.println(22222);
                handleWebSocketFrame(ctx, (WebSocketFrame) message);
            }
        }
        catch (Exception ex) {
            // if (logger.isDebugEnabled()) {
            ex.printStackTrace();
            // }
            throw new ServerException(ErrorConstant.CHANNEL_ERROR, ex);
        }
    }
    
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }
        
        // Allow only GET methods.
        if (req.getMethod() != GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }
        
        // Send the demo page and favicon.ico
        // if ("/".equals(req.getUri())) {
        // ByteBuf content = WebSocketServerIndexPage.getContent(getWebSocketLocation(this.sslYn, req));
        // FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
        //
        // res.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        // HttpHeaders.setContentLength(res, content.readableBytes());
        //
        // sendHttpResponse(ctx, req, res);
        // return;
        // }
        if ("/favicon.ico".equals(req.getUri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }
        
        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(this.sslYn, req), null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }
        else {
            handshaker.handshake(ctx.channel(), req);
        }
    }
    
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }
        
        if (responseQueue != null) {
            ByteBuf request = ((TextWebSocketFrame) frame).content();
            byte[] gg = new byte[request.readableBytes()];
            request.readBytes(gg);
            responseQueue.add(gg);
            
            if (this.singleYn == SingleYn.YES) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
            
            return;
        }
        
        // Send the uppercase string back.
        String request = ((TextWebSocketFrame) frame).text();
        byte[] resMessage = serviceHandler.handle(inBoundChannel, webSocketHeaders, request.getBytes());
        System.out.println("resMessage1:" + new String(resMessage));
        
        // System.err.printf("%s received %s%n", ctx.channel(), request);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(new String(resMessage)));
        
        if (this.singleYn == SingleYn.YES) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }
        
        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
        
    }
    
    private static String getWebSocketLocation(SSLYn sslYn, FullHttpRequest req) {
        String location = req.headers().get(HOST) + WEBSOCKET_PATH;
        if (sslYn == SSLYn.YES) {
            return "wss://" + location;
        }
        else {
            return "ws://" + location;
        }
    }
    
    @Override
    public void send(ChannelHandlerContext ctx, byte[] message) {
        future = ctx.channel().writeAndFlush(new TextWebSocketFrame(new String(message)));
        try {
            future.sync();
        }
        catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServerException(ErrorConstant.CHANNEL_ERROR, e);
        }
    }
    
    @Override
    public byte[] sendAndReceive(ChannelHandlerContext ctx, byte[] message) {
        ctx.attr(STATE_ATTRIBUTE).set(State.SEND_WAIT_RECEIVE);
        responseQueue = new LinkedBlockingQueue<byte[]>();
        
        future = ctx.channel().writeAndFlush(new TextWebSocketFrame(new String(message)));
        
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
        return this.webSocketHeaders;
    }
    
}