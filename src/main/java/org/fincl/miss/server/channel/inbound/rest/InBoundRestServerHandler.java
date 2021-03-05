package org.fincl.miss.server.channel.inbound.rest;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.entity.ContentType;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
@Sharable
public class InBoundRestServerHandler extends SimpleChannelInboundHandler<Object> implements InBoundServerHandler {
    
    private static Logger logger = LoggerFactory.getLogger(InBoundRestServerHandler.class);
    
    @Autowired
    private ServiceHandler serviceHandler;
    
    private HttpRequest request;
    
    // private RestMessage reqMessage = null;
    private String reqMessage = "";
    
    // private ExtMessage resMessage = null;
    private String resMessage = "";
    
    private Map<String, Object> httpHeaders = new LinkedHashMap<String, Object>();
    private Map<String, String> httpParams = new LinkedHashMap<String, String>();
    
    private ListenInBoundChannel inBoundChannel;
    
    public InBoundRestServerHandler(ListenInBoundChannel inBoundChannel) {
        this.inBoundChannel = inBoundChannel;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("ggggg------");
        inBoundChannel.getAllChannels().remove(ctx.channel().toString());
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        inBoundChannel.getAllChannels().put(System.identityHashCode(ctx.channel()) + "", ctx);
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    @EnableTraceLogging(isStart = true)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) { // header
            HttpRequest request = this.request = (HttpRequest) msg;
            
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            
            // buf.append("VERSION: ").append(request.getProtocolVersion()).append("\r\n");
            // buf.append("HOSTNAME: ").append(HttpHeaders.getHost(request, "unknown")).append("\r\n");
            // buf.append("REQUEST_URI: ").append(request.getUri()).append("\r\n\r\n");
            
            httpHeaders.put("version", request.getProtocolVersion().toString());
            httpHeaders.put("host", HttpHeaders.getHost(request, "unknown"));
            httpHeaders.put("uri", request.getUri());
            
            // remote addr
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            InetAddress inetaddress = socketAddress.getAddress();
            String remoteAddress = inetaddress.getHostAddress();
            this.httpHeaders.put(MessageHeader.CLIENT_IP, remoteAddress);
            
            HttpHeaders headers = request.headers();
            if (!headers.isEmpty()) {
                for (Map.Entry<String, String> h : headers) {
                    String key = h.getKey();
                    String value = h.getValue();
                    
                    httpHeaders.put(key.toLowerCase(), value);
                }
            }
            
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            if (!params.isEmpty()) {
                for (Entry<String, List<String>> p : params.entrySet()) {
                    String key = p.getKey();
                    List<String> values = p.getValue();
                    for (String value : values) {
                        httpParams.put(key, value);
                    }
                    
                }
            }
            
            appendDecoderResult(request);
        }
        
        if (msg instanceof HttpContent) { // body
            HttpContent httpContent = (HttpContent) msg;
            
            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                
                reqMessage = content.toString(CharsetUtil.UTF_8);
                
                appendDecoderResult(request);
            }
            
            if (msg instanceof LastHttpContent) {
                // buf.append("END OF CONTENT\r\n");
                
                LastHttpContent trailer = (LastHttpContent) msg;
                if (!trailer.trailingHeaders().isEmpty()) {
                    // buf.append("\r\n");
                    for (String name : trailer.trailingHeaders().names()) {
                        for (String value : trailer.trailingHeaders().getAll(name)) {
                            // buf.append("TRAILING HEADER: ");
                            // buf.append(name).append(" = ").append(value).append("\r\n");
                            
                            httpHeaders.put(name.toLowerCase(), value);
                        }
                    }
                    // buf.append("\r\n");
                }
                
                if (!writeResponse(trailer, ctx)) {
                    // If keep-alive is off, close the connection once the content is fully written.
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }
    
    private static void appendDecoderResult(HttpObject o) {
        DecoderResult result = o.getDecoderResult();
        if (result.isSuccess()) {
            return;
        }
    }
    
    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        // Decide whether to close the connection or not.
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        // reqMessage = new RestMessage(DataType.REQUEST, inBoundChannel.getRequestDataTypeEnum(), inBoundChannel.getResponseDataTypeEnum(), reqContent, httpHeaders);
        resMessage = new String(serviceHandler.handle(inBoundChannel, httpHeaders, reqMessage.getBytes()));
        
        // System.out.println("resMessage:" + resMessage);
        
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, currentObj.getDecoderResult().isSuccess() ? OK : BAD_REQUEST, Unpooled.copiedBuffer(resMessage, CharsetUtil.UTF_8));
        if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.JSON) {
            response.headers().set(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), inBoundChannel.getCharsetEnum().toString()));
        }
        else if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.XML) {
            response.headers().set(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_XML.getMimeType(), inBoundChannel.getCharsetEnum().toString()));
        }
        
        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        
        // Write the response.
        ctx.write(response);
        
        return keepAlive;
    }
    
    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void send(ChannelHandlerContext ctx, byte[] message) {
        throw new ServerException(ErrorConstant.CHANNEL_NOT_SUPPORTED);
    }
    
    @Override
    public byte[] sendAndReceive(ChannelHandlerContext ctx, byte[] message) {
        throw new ServerException(ErrorConstant.CHANNEL_NOT_SUPPORTED);
    }
    
    @Override
    public InBoundChannelImpl getInBoundChannel() {
        return this.inBoundChannel;
    }
    
    @Override
    public Map<String, Object> getHeader() {
        return this.httpHeaders;
    }
    
}