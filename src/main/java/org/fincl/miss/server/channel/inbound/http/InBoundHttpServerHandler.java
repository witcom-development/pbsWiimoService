package org.fincl.miss.server.channel.inbound.http;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
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
public class InBoundHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> implements InBoundServerHandler {
    
    private static Logger logger = LoggerFactory.getLogger(InBoundHttpServerHandler.class);
    
    @Autowired
    private ServiceHandler serviceHandler;
    
    private HttpRequest request;
    
    // private HttpFormMessage reqMessage = null;
    private String reqMessage = "";
    
    // private ExtMessage resMessage = null;
    private String resMessage = "";
    
    private Map<String, Object> httpHeaders = new LinkedHashMap<String, Object>();
    // private Map<String, Object> httpParams = new LinkedHashMap<String, Object>();
    // private String reqContent = "";
    
    private boolean readingChunks;
    
    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk
    
    private HttpPostRequestDecoder decoder;
    
    private ListenInBoundChannel inBoundChannel;
    
    public InBoundHttpServerHandler() {
        
    }
    
    public InBoundHttpServerHandler(ListenInBoundChannel inBoundChannel) {
        this.inBoundChannel = inBoundChannel;
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
        inBoundChannel.getAllChannels().remove(ctx.channel().toString());
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        inBoundChannel.getAllChannels().put(System.identityHashCode(ctx.channel()) + "", ctx);
        ctx.writeAndFlush(Unpooled.wrappedBuffer("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>".getBytes()));
    }
    
    @EnableTraceLogging(isStart = true)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws URISyntaxException {
        System.out.println("msg==>" + msg);
        if (msg instanceof HttpRequest) {
            HttpRequest request = this.request = (HttpRequest) msg;
            URI uri = new URI(request.getUri());
            
            if (uri.getPath().equals("/favicon.ico")) {
                return;
            }
            if (uri.getPath().equals("/")) {
                return;
            }
            
            // new getMethod
            for (Entry<String, String> entry : request.headers()) {
                this.httpHeaders.put(entry.getKey().toLowerCase(), entry.getValue());
            }
            
            System.out.println("httpHeaders:" + httpHeaders);
            
            httpHeaders.put("version", request.getProtocolVersion().toString());
            httpHeaders.put("host", HttpHeaders.getHost(request, "unknown"));
            httpHeaders.put("uri", request.getUri());
            
            // remote addr
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            InetAddress inetaddress = socketAddress.getAddress();
            String remoteAddress = inetaddress.getHostAddress();
            this.httpHeaders.put(MessageHeader.CLIENT_IP, remoteAddress);
            
            QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                // for (String attrVal : attr.getValue()) {
                // if (attr.getValue().size() > 1) {
                // this.httpParams.put(attr.getKey(), attr.getValue().toArray(new String[attr.getValue().size()]));
                // }
                // else {
                // this.httpParams.put(attr.getKey(), attr.getValue().get(0));
                // }
                // }
            }
            
            // if GET Method: should not try to create a HttpPostRequestDecoder
            if (request.getMethod().equals(HttpMethod.GET)) {
                // GET Method: should not try to create a HttpPostRequestDecoder
                // So stop here
                writeResponse(ctx.channel());
                return;
            }
            
            // Post
            if (request.getMethod().equals(HttpMethod.POST)) {
                try {
                    decoder = new HttpPostRequestDecoder(factory, request);
                }
                catch (ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    writeResponse(ctx.channel());
                    ctx.channel().close();
                    return;
                }
                
                readingChunks = HttpHeaders.isTransferEncodingChunked(request);
                if (readingChunks) {
                    // Chunk version
                    readingChunks = true;
                }
            }
        }
        
        if (decoder != null) {
            if (msg instanceof HttpContent) {
                // New chunk is received
                HttpContent chunk = (HttpContent) msg;
                try {
                    decoder.offer(chunk);
                }
                catch (ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    writeResponse(ctx.channel());
                    ctx.channel().close();
                    return;
                }
                try {
                    while (decoder.hasNext()) {
                        InterfaceHttpData data = decoder.next();
                        if (data != null) {
                            try {
                                writeHttpData(data);
                            }
                            finally {
                                data.release();
                            }
                        }
                    }
                }
                catch (EndOfDataDecoderException e1) {
                    // END OF CONTENT CHUNK BY CHUNK
                }
                
                // example of reading only if at the end
                if (chunk instanceof LastHttpContent) {
                    writeResponse(ctx.channel());
                    readingChunks = false;
                    reset();
                }
            }
        }
        
    }
    
    private void reset() {
        request = null;
        // destroy the decoder to release all resources
        decoder.destroy();
        decoder = null;
    }
    
    private void writeHttpData(InterfaceHttpData data) {
        /**
         * HttpDataType
         * Attribute, FileUpload, InternalAttribute
         */
        if (data.getHttpDataType() == HttpDataType.Attribute) {
            Attribute attribute = (Attribute) data;
            // attribute.getHttpDataType().name()
            // attribute.getName()
            // attribute.toString()
            try {
                
                // if (attribute.getValue().length() > 1) {
                // this.httpParams.put(attribute.getName(), attribute.getValue());
                // }
                // else {
                // this.httpParams.put(attribute.getName(), attribute.getValue().le);
                // }
                // this.httpParams.put(attribute.getName(), attribute.getValue());
                
                // System.out.println(">>" + attribute. + " " + attribute.getName() + "  " + attribute.getValue());
                reqMessage += attribute.getName() + "=" + attribute.getValue() + "&";
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private void writeResponse(Channel channel) {
        // Convert the response content to a ChannelBuffer.
        // reqMessage = new HttpFormMessage(DataType.REQUEST, inBoundChannel.getRequestDataTypeEnum(), inBoundChannel.getResponseDataTypeEnum(), request, httpHeaders);
        resMessage = new String(serviceHandler.handle(inBoundChannel, httpHeaders, reqMessage.getBytes()));
        
        if (logger.isDebugEnabled()) {
            logger.debug("httpHeaders : {}", httpHeaders);
            // logger.debug("httpParams : {}", httpParams);
            logger.debug("serviceHandler : {}", serviceHandler);
            logger.debug("reqMessage : {}", reqMessage);
            logger.debug("resMessage : {}", resMessage);
            logger.debug("resMessage.getPayload() : {}", resMessage);
        }
        
        ByteBuf buf = copiedBuffer(resMessage, CharsetUtil.UTF_8);
        
        // Decide whether to close the connection or not.
        boolean close = request.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true) || request.getProtocolVersion().equals(HttpVersion.HTTP_1_0) && !request.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true);
        
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.JSON) {
            response.headers().set(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), inBoundChannel.getCharsetEnum().toString()));
        }
        else if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.XML) {
            response.headers().set(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_XML.getMimeType(), inBoundChannel.getCharsetEnum().toString()));
        }
        
        if (!close) {
            // There's no need to add 'Content-Length' header
            // if this is the last response.
            response.headers().set(CONTENT_LENGTH, buf.readableBytes());
        }
        
        // Write the response.
        ChannelFuture future = channel.writeAndFlush(response);
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
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