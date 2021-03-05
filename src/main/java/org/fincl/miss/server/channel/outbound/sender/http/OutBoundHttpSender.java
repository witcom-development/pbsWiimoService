package org.fincl.miss.server.channel.outbound.sender.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.ACCEPT;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.sender.HttpClientFactory;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.OutBoundSenderException;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.RequestDataType;
import org.fincl.miss.server.util.EnumCode.SSLYn;
import org.fincl.miss.server.util.FileDownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Scope("prototype")
@Component
public class OutBoundHttpSender extends OutBoundSender {
    
    private static Logger logger = LoggerFactory.getLogger(OutBoundHttpSender.class);
    
    private PoolingHttpClientConnectionManager httpConnectionManager;
    
    private HttpClient httpClient;
    
    public OutBoundHttpSender() {
        
    }
    
    public OutBoundHttpSender(OutBoundChannelImpl outBoundChannel) {
        super(outBoundChannel);
        if (autoStartYn == AutoStartYn.YES) {
            setupHttpClient();
        }
    }
    
    @EnableTraceLogging
    @Override
    public byte[] sendAndReceive(byte[] bMessage) throws OutBoundSenderException {
        byte[] bRes;
        System.out.println("mkkang111:" + new String(bMessage));
        bRes = sendMessage(bMessage, true);
        return bRes;
    }
    
    @Override
    public void send(byte[] bMessage) throws OutBoundSenderException {
        sendMessage(bMessage, false);
    }
    
    private byte[] sendMessage(byte[] bMessage, boolean bReceive) throws OutBoundSenderException {
        byte[] bRes;
        
        System.out.println("bMessagebMessage:" + new String(bMessage));
        String url = null;
        String uri = getOutBoundChannel().getOutBoundHttpUri();
        if (uri == null) {
            uri = "/";
        }
        else {
            if (!uri.startsWith("/")) {
                uri = "/" + uri;
            }
        }
        
        if (sslYn == SSLYn.YES) {
            url = "https://" + getOutBoundChannel().getOutBoundChannelHost() + ":" + getOutBoundChannel().getPort() + uri;
        }
        else {
            url = "http://" + getOutBoundChannel().getOutBoundChannelHost() + ":" + getOutBoundChannel().getPort() + uri;
        }
        
        if (httpClient == null) {
            // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, "[" + getOutBoundChannel().getChannelId() + "] HttpClient not initialized.");
            startup();
        }
        
        HttpPost post = new HttpPost(url);
        try {
            
            if (outBoundChannel.getRequestDataTypeEnum() == RequestDataType.FILEDOWNLOAD) {
                post.setHeader(ACCEPT, "application/force-download");
            }
            
            ContentType contentType = ContentType.TEXT_PLAIN;
            if (outBoundChannel.getRequestDataTypeEnum() == RequestDataType.FORM) {
                contentType = ContentType.create(ContentType.APPLICATION_FORM_URLENCODED.getMimeType(), Charset.forName(outBoundChannel.getCharsetEnum().toString()));
            }
            else if (outBoundChannel.getRequestDataTypeEnum() == RequestDataType.JSON) {
                contentType = ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Charset.forName(outBoundChannel.getCharsetEnum().toString()));
            }
            else if (outBoundChannel.getRequestDataTypeEnum() == RequestDataType.XML) {
                contentType = ContentType.create(ContentType.APPLICATION_XML.getMimeType(), Charset.forName(outBoundChannel.getCharsetEnum().toString()));
            }
            
            HttpEntity requestEntity = new ByteArrayEntity(bMessage, contentType);
            post.setEntity(requestEntity);
            
            HttpResponse httpResponse = httpClient.execute(post);
            System.out.println("Response Code : " + httpResponse.getStatusLine().getStatusCode());
            
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            if (request != null) {
                if (httpResponse.getEntity().getContentType() != null && httpResponse.getEntity().getContentType().getValue().toLowerCase().indexOf(MediaType.APPLICATION_OCTET_STREAM.toString().toLowerCase()) > -1) {
                    String contentDisposition = httpResponse.getFirstHeader("Content-Disposition").getValue();
                    if (contentDisposition != null) {
                        int idxFilename = StringUtils.indexOfIgnoreCase(contentDisposition, "filename");
                        String paramFilename = StringUtils.substring(contentDisposition, idxFilename);
                        if (paramFilename != null && paramFilename.indexOf("=") > -1) {
                            String[] arrParamFilename = StringUtils.split(paramFilename, "=");
                            if (arrParamFilename != null && arrParamFilename.length == 2) {
                                request.setAttribute(FileDownload.Attribute.FILENAME.getValue(), StringUtils.remove(arrParamFilename[1], "\""));
                                request.setAttribute(FileDownload.Attribute.CHECKSUM.getValue(), httpResponse.getFirstHeader(FileDownload.Attribute.CHECKSUM.getValue()));
                            }
                        }
                    }
                }
            }
            
            bRes = EntityUtils.toByteArray(httpResponse.getEntity());
        }
        catch (ParseException e) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        catch (IOException e) {
            throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e);
        }
        
        if (bRes == null) {
            bRes = new byte[0];
        }
        
        return bRes;
    }
    
    @Override
    public boolean startup() throws ServerException {
        HttpClientFactory factory = new HttpClientFactory();
        httpConnectionManager = factory.getHttpConnectionManager();
        httpClient = factory.getClient();
        return true;
        
    }
    
    @Override
    public boolean shutdown() throws ServerException {
        if (httpConnectionManager != null) {
            httpConnectionManager.shutdown();
            httpConnectionManager = null;
        }
        httpClient = null;
        return true;
        
    }
    
    private void setupHttpClient() {
        HttpClientFactory factory = new HttpClientFactory();
        httpConnectionManager = factory.getHttpConnectionManager();
        httpClient = factory.getClient();
    }
    
    @Override
    public ChannelStatus getStatus() throws ServerException {
        System.out.println("httpConnectionManager:" + httpConnectionManager);
        System.out.println("httpClient:" + httpClient);
        ChannelStatus channelStatus = ChannelStatus.DEAD;
        if (httpConnectionManager != null && httpClient != null) {
            channelStatus = ChannelStatus.ALIVE;
        }
        return channelStatus;
    }
    
}
