package org.fincl.miss.server.channel.inbound.servlet;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.USER_AGENT;
import io.netty.channel.ChannelHandlerContext;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.fincl.miss.core.viewresolver.header.HeaderParamEntity;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.inbound.InBoundServerHandler;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.InBoundServletException;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.exeption.ServiceHandlerException;
import org.fincl.miss.server.message.MessageHandler;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.security.e2e.service.E2EService;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.fincl.miss.server.util.FileDownload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.dkitec.cfood.core.CfoodException;

@Scope("prototype")
@Component
public class InBoundServletHandler implements InBoundServerHandler {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    static final String E2E_HEADER_NAME = "X-E2E";
    
    @Autowired
    private E2EService e2eService;
    
    @Autowired
    private ChannelManagerImpl channelManager;
    
    @Autowired
    private ServiceHandler serviceHandler;
    
    @Autowired
    private MessageHandler messageHandler;
    
    @Autowired
    private MessageSource errorSource;
    
    private InBoundChannelImpl inBoundChannel;
    
    private Map<String, Object> httpHeaders = new LinkedHashMap<String, Object>();
    
    public ResponseEntity<?> servletChannelRead(HeaderParamEntity deviceHeader, String channelId, HttpEntity<byte[]> requestEntity) {
        byte[] rMessage = null;
        ResponseEntity<?> responseEntity = null;
        MultiValueMap<String, String> resHeaders = new LinkedMultiValueMap<String, String>();
        
        inBoundChannel = channelManager.getInBoundChannel(channelId);
        if (inBoundChannel == null) {
            byte[] bRes = errorSource.getMessage(ErrorConstant.CHANNEL_NOT_FOUND, new String[] { channelId }, Locale.getDefault()).getBytes();
            responseEntity = new ResponseEntity<byte[]>(bRes, HttpStatus.NOT_FOUND);
        }
        else {
            
            HttpHeaders headers = requestEntity.getHeaders();
            Iterator<Entry<String, List<String>>> itHeaders = headers.entrySet().iterator();
            while (itHeaders.hasNext()) {
                Entry<String, List<String>> header = itHeaders.next();
                List<String> values = header.getValue();
                if (values.size() > 0) {
                    httpHeaders.put(header.getKey(), (values.size() > 1) ? values : values.get(0));
                }
                else {
                    httpHeaders.put(header.getKey(), "");
                }
                System.out.println("header [" + header.getKey() + "][" + httpHeaders.get(header.getKey()) + "]");
            }
            
            httpHeaders.put("deviceHeader", deviceHeader);
            
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            httpHeaders.put(MessageHeader.CLIENT_IP, request.getRemoteAddr());
            byte[] bMessage = requestEntity.getBody();
            
            System.out.println("x1:[" + new String(bMessage) + "]");
            System.out.println("x2:[" + isE2E(httpHeaders) + "]");
            
            String xx = StringUtils.remove(new String(bMessage), "\n");
            
            if (isE2E(httpHeaders)) {
                System.out.println("x3:[" + xx + "]");
                bMessage = e2eService.decrypt(deviceHeader.getUuid(), xx.getBytes());
            }
            
            if (inBoundChannel.getStatus() == ChannelStatus.ALIVE) {
                try {
                    // byte[] mm = "{\"request\":{\"id\":\"testid\",\"serviceId\":\"servletService\", \"param\": {\"field1\":\"abc\", \"field2\":[1,2,3,4],\"field3\":[1,2,3,4,5]}}}".getBytes();
                    // byte[] mm = "<request><id>testid</id><serviceId>servletService</serviceId><param><field1>abc</field1></param></request>".getBytes();
                    rMessage = serviceHandler.handle(inBoundChannel, httpHeaders, bMessage);
                    
                    if (isFileDownload(requestEntity.getHeaders().getAccept())) {
                        resHeaders.add(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_OCTET_STREAM.getMimeType(), Charset.forName(inBoundChannel.getCharsetEnum().toString())).toString());
                        
                        String userAgent = (String) httpHeaders.get(USER_AGENT.toLowerCase());
                        String fileDownloadName = (String) request.getAttribute(FileDownload.Attribute.FILENAME.getValue());
                        String checkSum = (String) request.getAttribute(FileDownload.Attribute.CHECKSUM.getValue());
                        
                        if (StringUtils.isEmpty(fileDownloadName)) {
                            fileDownloadName = UUID.randomUUID().toString();
                        }
                        if (userAgent.contains("msie") || userAgent.contains("trident") || userAgent.contains("chrome")) {
                            fileDownloadName = URLEncoder.encode(fileDownloadName, "UTF-8").replaceAll("\\+", "%20");
                        }
                        else {
                            fileDownloadName = new String(fileDownloadName.getBytes("UTF-8"), "ISO-8859-1");
                        }
                        
                        resHeaders.add("Content-Disposition", "attachment;filename=\"" + fileDownloadName + "\"");
                        resHeaders.add(CONTENT_TYPE, "application/octet-stream");
                        resHeaders.add("Content-Transfer-Encoding", "binary;");
                        if (StringUtils.isNotEmpty(checkSum)) {
                            resHeaders.add("ETag", checkSum);
                        }
                        
                        Object resObj = request.getAttribute(FileDownload.Attribute.FILE.getValue());
                        
                        if (resObj instanceof byte[]) {
                            return new ResponseEntity<byte[]>((byte[]) resObj, resHeaders, HttpStatus.OK);
                        }
                        else if (resObj instanceof InputStreamResource) {
                            return new ResponseEntity<InputStreamResource>((InputStreamResource) resObj, resHeaders, HttpStatus.OK);
                        }
                        else {
                            //
                        }
                    }
                    else {
                        if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.JSON) {
                            resHeaders.add(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Charset.forName(inBoundChannel.getCharsetEnum().toString())).toString());
                        }
                        else if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.XML) {
                            resHeaders.add(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_XML.getMimeType(), Charset.forName(inBoundChannel.getCharsetEnum().toString())).toString());
                        }
                    }
                    
                    System.out.println("mkaaaaaaaaaa:" + inBoundChannel.getCharsetEnum().toString());
                    
                }
                catch (ServiceHandlerException ex) {
                    if (logger.isDebugEnabled()) {
                        ex.printStackTrace();
                    }
                    throw new InBoundServletException(this, ex.getCode(), ex, bMessage);
                }
                catch (Exception ex) {
                    if (logger.isDebugEnabled()) {
                        ex.printStackTrace();
                    }
                    System.out.println("servletChannelRead : ex " + ex);
                    System.out.println("servletChannelRead : ex.cause " + ex.getCause());
                    if (ex instanceof CfoodException) {
                        throw new InBoundServletException(this, ((CfoodException) ex).getCode(), ex, bMessage);
                    }
                    else {
                        throw new InBoundServletException(this, ErrorConstant.INTERNAL_ERROR, ex, bMessage);
                    }
                }
                
                if (isE2E(httpHeaders)) {
                    rMessage = e2eService.encrypt(deviceHeader.getUuid(), rMessage);
                }
                
                responseEntity = new ResponseEntity<byte[]>(rMessage, resHeaders, HttpStatus.OK);
            }
            else {
                if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.JSON) {
                    resHeaders.add(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_JSON.getMimeType(), Charset.forName(inBoundChannel.getCharsetEnum().toString())).toString());
                }
                else if (inBoundChannel.getResponseDataTypeEnum() == ResponseDataType.XML) {
                    resHeaders.add(CONTENT_TYPE, ContentType.create(ContentType.APPLICATION_XML.getMimeType(), Charset.forName(inBoundChannel.getCharsetEnum().toString())).toString());
                }
                System.out.println("gg:" + new String(bMessage));
                throw new InBoundServletException(this, ErrorConstant.CHANNEL_NOT_STARTED_ERROR, bMessage);
            }
        }
        
        return responseEntity;
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
    
    private boolean isFileDownload(List<MediaType> mediaTypeList) {
        boolean bFile = false;
        
        for (MediaType mediaType : mediaTypeList) {
            if (StringUtils.startsWith(mediaType.toString().toLowerCase(), "application/force-download")) {
                bFile = true;
            }
        }
        return bFile;
    }
    
    private boolean isE2E(Map<String, Object> headers) {
        boolean b = false;
        
        if ("1".equals((String) headers.get(E2E_HEADER_NAME.toLowerCase()))) {
            b = true;
        }
        
        System.out.println("mk------ e2e [" + b + "]");
        
        return b;
    }
}
