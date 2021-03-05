package org.fincl.miss.server.channel.outbound.sender.rest;

import java.nio.charset.Charset;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.sender.HttpClientFactory;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.exeption.OutBoundSenderException;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.RequestDataType;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.fincl.miss.server.util.EnumCode.SSLYn;
import org.fincl.miss.server.util.FileDownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Scope("prototype")
@Component
public class OutBoundRestSender extends OutBoundSender {
    
    private static Logger logger = LoggerFactory.getLogger(OutBoundRestSender.class);
    
    private PoolingHttpClientConnectionManager httpConnectionManager;
    private HttpClient httpClient;
    private RestTemplate restTemplate;
    
    public OutBoundRestSender() {
        
    }
    
    public OutBoundRestSender(OutBoundChannelImpl outBoundChannel) {
        super(outBoundChannel);
        if (autoStartYn == AutoStartYn.YES) {
            startup();
        }
    }
    
    @EnableTraceLogging
    @Override
    public byte[] sendAndReceive(byte[] bMessage) throws OutBoundSenderException {
        byte[] bRes = null;
        bRes = sendMessage(bMessage, true);
        return bRes;
    }
    
    @Override
    public void send(byte[] bMessage) throws OutBoundSenderException {
        sendMessage(bMessage, false);
    }
    
    private byte[] sendMessage(byte[] bMessage, boolean bReceive) throws OutBoundSenderException {
        
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
        
        if (restTemplate == null) {
            // throw new OutBoundSenderException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, "[" + getOutBoundChannel().getChannelId() + "] HttpClient not initialized.");
            startup();
        }
        
        HttpHeaders headers = new HttpHeaders();
        if (outBoundChannel.getRequestDataTypeEnum() == RequestDataType.FILEDOWNLOAD) {
            headers.setAccept(Arrays.asList(MediaType.parseMediaType("application/force-download")));
        }
        else {
            if (outBoundChannel.getResponseDataTypeEnum() == ResponseDataType.XML) {
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
            }
            else if (outBoundChannel.getResponseDataTypeEnum() == ResponseDataType.JSON) {
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            }
        }
        
        if (outBoundChannel.getRequestDataTypeEnum() == RequestDataType.XML) {
            headers.setContentType(new MediaType("application", "xml", Charset.forName(outBoundChannel.getCharsetEnum().toString())));
        }
        else if (outBoundChannel.getRequestDataTypeEnum() == RequestDataType.JSON) {
            headers.setContentType(new MediaType("application", "json", Charset.forName(outBoundChannel.getCharsetEnum().toString())));
        }
        
        HttpEntity<byte[]> entity = new HttpEntity<byte[]>(bMessage, headers);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
        
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (request != null) {
            HttpHeaders resHeaders = responseEntity.getHeaders();
            if (resHeaders.getContentType() != null && resHeaders.getContentType().toString().toLowerCase().indexOf(MediaType.APPLICATION_OCTET_STREAM.toString().toLowerCase()) > -1) {
                String contentDisposition = resHeaders.getFirst("Content-Disposition");
                if (contentDisposition != null) {
                    int idxFilename = StringUtils.indexOfIgnoreCase(contentDisposition, "filename");
                    String paramFilename = StringUtils.substring(contentDisposition, idxFilename);
                    if (paramFilename != null && paramFilename.indexOf("=") > -1) {
                        String[] arrParamFilename = StringUtils.split(paramFilename, "=");
                        if (arrParamFilename != null && arrParamFilename.length == 2) {
                            request.setAttribute(FileDownload.Attribute.FILENAME.getValue(), StringUtils.remove(arrParamFilename[1], "\""));
                            request.setAttribute(FileDownload.Attribute.CHECKSUM.getValue(), resHeaders.getETag());
                        }
                    }
                }
            }
        }
        
        byte[] bRes = responseEntity.getBody();
        if (bRes == null) {
            bRes = new byte[0];
        }
        return bRes;
    }
    
    @Override
    public boolean startup() throws ServerException {
        setupHttpClient();
        restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        return true;
    }
    
    private void setupHttpClient() {
        HttpClientFactory factory = new HttpClientFactory();
        httpConnectionManager = factory.getHttpConnectionManager();
        httpClient = factory.getClient();
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
