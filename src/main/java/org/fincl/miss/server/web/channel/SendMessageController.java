package org.fincl.miss.server.web.channel;

import io.netty.channel.ChannelHandlerContext;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.server.channel.OutBoundRequest;
import org.fincl.miss.core.server.channel.OutBoundResponse;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.channel.service.Channel;
import org.fincl.miss.server.channel.service.ChannelMasterService;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.util.CharsetConvertUtil;
import org.fincl.miss.server.util.EnumCode.ProtocolType;
import org.fincl.miss.server.util.EnumCode.RequestDataType;
import org.fincl.miss.server.util.EnumCode.SyncType;
import org.fincl.miss.server.util.FileDownload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.dkitec.cfood.core.web.annotation.RequestCategory;

@Controller
@RequestCategory("메시지 전송 테스트")
public class SendMessageController<RMIConnector$RemoteMBeanServerConnection> {
    
    @Autowired
    private ChannelManagerImpl channelManager;
    
    @Autowired
    private ServiceHandler serviceHandler;
    
    @Autowired
    private ChannelMasterService channelMasterService;
    
    @RequestMapping(value = "/sendMessage", method = RequestMethod.GET)
    public void sendMessageForm(@RequestParam Map<String, String> param, ModelMap model) {
        List<InBoundChannelImpl> listInBound = channelManager.getInBoundChannelList();
        List<OutBoundChannelImpl> listOutBound = channelManager.getOutBoundChannelList();
        
        if (StringUtils.isNotEmpty(param.get("inBoundChannelId"))) {
            Channel channel = channelManager.getInBoundChannel(param.get("inBoundChannelId"));
            List<Channel> matchList = channelMasterService.getMatchingChannel(channel);
            model.addAttribute("matchList", matchList);
        }
        else {
            if (listInBound != null && listInBound.size() > 0) {
                Channel channel = listInBound.get(0);
                List<Channel> matchList = channelMasterService.getMatchingChannel(channel);
                model.addAttribute("matchList", matchList);
            }
        }
        
        model.addAttribute("listInBound", listInBound);
        model.addAttribute("listOutBound", listOutBound);
    }
    
    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public void sendMessageProc(@RequestParam Map<String, String> param, ModelMap model, HttpServletRequest httpRequest) {
        Map<String, Object> headerMap = new HashMap<String, Object>();
        
        String targetInBoundServerIpAddress = param.get("ip");
        String fileDownload = param.get("fileDownload");
        
        Enumeration headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = httpRequest.getHeader(key);
            headerMap.put(key, value);
        }
        
        List<InBoundChannelImpl> listInBound = channelManager.getInBoundChannelList();
        List<OutBoundChannelImpl> listOutBound = channelManager.getOutBoundChannelList();
        
        model.addAttribute("listInBound", listInBound);
        model.addAttribute("listOutBound", listOutBound);
        
        if (StringUtils.isNotEmpty(param.get("inBoundChannelId"))) {
            Channel channel = channelManager.getInBoundChannel(param.get("inBoundChannelId"));
            System.out.println(channel);
            List<Channel> matchList = channelMasterService.getMatchingChannel(channel);
            model.addAttribute("matchList", matchList);
        }
        else {
            if (listInBound != null && listInBound.size() > 0) {
                Channel channel = listInBound.get(0);
                List<Channel> matchList = channelMasterService.getMatchingChannel(channel);
                model.addAttribute("matchList", matchList);
            }
        }
        
        if ("in".equals(param.get("type"))) {
            
            Channel channel = new Channel();
            channel.setChannelId(param.get("sendChannelId"));
            channel = channelMasterService.getChannel(channel);
            channel.setOutBoundChannelHost(targetInBoundServerIpAddress);
            channel.setPort(channel.getPort());
            
            OutBoundChannelImpl sendChannel = ApplicationContextSupport.getBean(OutBoundChannelImpl.class, new Object[] { serviceHandler, channel });
            // OutBoundChannel sendChannel = new OutBoundChannel(serviceHandler, channelVO);
            if (sendChannel.getProtocolTypeEnum() == ProtocolType.SERVLET) {
                sendChannel.setPort(httpRequest.getServerPort());
                sendChannel.setOutBoundHttpUri(httpRequest.getContextPath() + "/channel/" + channel.getChannelId());
                
                if ("Y".equals(fileDownload)) {
                    sendChannel.setRequestDataTypeEnum(RequestDataType.FILEDOWNLOAD);
                }
            }
            // sendChannel.setCharsetEnum(channel.getCharsetEnum());
            byte[] response = new byte[0];
            byte[] viewResponse = new byte[0];
            try {
                byte[] request = param.get("inRequest").getBytes();
                // 받아주는 서버에 맞게 인코딩을 변경한다.
                request = CharsetConvertUtil.convert(request, Charset.defaultCharset(), Charset.forName(channel.getCharsetEnum().toString()));
                
                OutBoundRequest outBoundRequest = new OutBoundRequest();
                outBoundRequest.setPayload(request);
                
                if (sendChannel.getSyncTypeEnum() == SyncType.PUBSUB) {
                    System.out.println("mk11:" + ServiceMessageHeaderContext.getMessageHeader());
                    
                    // response = sendChannel.sendAndReceive("abcd", request);
                    OutBoundResponse outBoundResponse = sendChannel.sendAndReceive(outBoundRequest);
                    response = outBoundResponse.getPayload();
                }
                else {
                    if (sendChannel.getSyncTypeEnum() == SyncType.SYNC) {
                        // response = sendChannel.sendAndReceive(request);
                        OutBoundResponse outBoundResponse = sendChannel.sendAndReceive(outBoundRequest);
                        response = outBoundResponse.getPayload();
                    }
                    else if (sendChannel.getSyncTypeEnum() == SyncType.ASYNC) {
                        // sendChannel.send(request);
                        sendChannel.send(outBoundRequest);
                    }
                }
                
                // if ("Y".equals(fileDownload)) {
                // System.out.println(111);
                // String fileDownloadName = (String) httpRequest.getAttribute(FileDownload.Attribute.FILENAME.getValue());
                // String browser = httpRequest.getHeader("User-Agent");
                // // 파일 인코딩
                // if (browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")) {
                // fileDownloadName = URLEncoder.encode(fileDownloadName, "UTF-8").replaceAll("\\+", "%20");
                // }
                // else {
                // fileDownloadName = new String(fileDownloadName.getBytes("UTF-8"), "ISO-8859-1");
                // }
                // httpResponse.setHeader("Content-Disposition", "attachment;filename=\"" + fileDownloadName + "\"");
                // httpResponse.setContentType("application/octer-stream");
                // httpResponse.setHeader("Content-Transfer-Encoding", "binary;");
                // httpResponse.getOutputStream().write(response);
                // }
                // else {
                System.out.println(2222);
                // 화면에 올리기 위해 시스템 인코딩으로 변경한다.
                viewResponse = CharsetConvertUtil.convert(response, Charset.forName(channel.getCharsetEnum().toString()), Charset.defaultCharset());
                // }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = e.getMessage().getBytes();
            }
            model.addAttribute("inRequest", param.get("inRequest"));
            model.addAttribute("type", "인바운드채널(서버)");
            model.addAttribute("response", new String(viewResponse));
            model.addAttribute("channelId", param.get("inBoundChannelId"));
        }
        else {
            OutBoundChannelImpl channel = channelManager.getOutBoundChannel(param.get("outBoundChannelId"));
            OutBoundRequest outBoundRequest = new OutBoundRequest();
            outBoundRequest.setPayload(param.get("outRequest").getBytes());
            OutBoundResponse outBoundResponse = channel.sendAndReceive(outBoundRequest);
            byte[] response = outBoundResponse.getPayload();
            model.addAttribute("type", "아웃바운드채널(클라이언트)");
            model.addAttribute("outRequest", param.get("outRequest"));
            model.addAttribute("response", new String(response));
            model.addAttribute("channelId", param.get("outBoundChannelId"));
        }
        
    }
    
    @RequestMapping(value = "/sendMessageGetFile", method = RequestMethod.POST)
    public void sendMessageProcGetFile(@RequestParam Map<String, String> param, ModelMap model, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Map<String, Object> headerMap = new HashMap<String, Object>();
        
        String targetInBoundServerIpAddress = param.get("ip");
        String fileDownload = param.get("fileDownload");
        
        Enumeration headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = httpRequest.getHeader(key);
            headerMap.put(key, value);
        }
        
        List<InBoundChannelImpl> listInBound = channelManager.getInBoundChannelList();
        List<OutBoundChannelImpl> listOutBound = channelManager.getOutBoundChannelList();
        
        model.addAttribute("listInBound", listInBound);
        model.addAttribute("listOutBound", listOutBound);
        
        if (StringUtils.isNotEmpty(param.get("inBoundChannelId"))) {
            Channel channel = channelManager.getInBoundChannel(param.get("inBoundChannelId"));
            System.out.println(channel);
            List<Channel> matchList = channelMasterService.getMatchingChannel(channel);
            model.addAttribute("matchList", matchList);
        }
        else {
            if (listInBound != null && listInBound.size() > 0) {
                Channel channel = listInBound.get(0);
                List<Channel> matchList = channelMasterService.getMatchingChannel(channel);
                model.addAttribute("matchList", matchList);
            }
        }
        
        if ("in".equals(param.get("type"))) {
            
            Channel channel = new Channel();
            channel.setChannelId(param.get("sendChannelId"));
            channel = channelMasterService.getChannel(channel);
            channel.setOutBoundChannelHost(targetInBoundServerIpAddress);
            channel.setPort(channel.getPort());
            
            OutBoundChannelImpl sendChannel = ApplicationContextSupport.getBean(OutBoundChannelImpl.class, new Object[] { serviceHandler, channel });
            // OutBoundChannel sendChannel = new OutBoundChannel(serviceHandler, channelVO);
            if (sendChannel.getProtocolTypeEnum() == ProtocolType.SERVLET) {
                sendChannel.setPort(httpRequest.getServerPort());
                sendChannel.setOutBoundHttpUri(httpRequest.getContextPath() + "/channel/" + channel.getChannelId());
                
                if ("Y".equals(fileDownload)) {
                    sendChannel.setRequestDataTypeEnum(RequestDataType.FILEDOWNLOAD);
                }
            }
            // sendChannel.setCharsetEnum(channel.getCharsetEnum());
            byte[] response = new byte[0];
            byte[] viewResponse = new byte[0];
            try {
                byte[] request = param.get("inRequest").getBytes();
                // 받아주는 서버에 맞게 인코딩을 변경한다.
                request = CharsetConvertUtil.convert(request, Charset.defaultCharset(), Charset.forName(channel.getCharsetEnum().toString()));
                
                OutBoundRequest outBoundRequest = new OutBoundRequest();
                outBoundRequest.setPayload(request);
                
                if (sendChannel.getSyncTypeEnum() == SyncType.PUBSUB) {
                    System.out.println("mk11:" + ServiceMessageHeaderContext.getMessageHeader());
                    // response = sendChannel.sendAndReceive("abcd", request);
                    
                    OutBoundResponse outBoundResponse = sendChannel.sendAndReceive(outBoundRequest);
                    response = outBoundResponse.getPayload();
                }
                else {
                    if (sendChannel.getSyncTypeEnum() == SyncType.SYNC) {
                        // response = sendChannel.sendAndReceive(request);
                        
                        OutBoundResponse outBoundResponse = sendChannel.sendAndReceive(outBoundRequest);
                        response = outBoundResponse.getPayload();
                    }
                    else if (sendChannel.getSyncTypeEnum() == SyncType.ASYNC) {
                        // sendChannel.send(request);
                        
                        sendChannel.send(outBoundRequest);
                    }
                }
                
                if ("Y".equals(fileDownload)) {
                    System.out.println(111);
                    String fileDownloadName = (String) httpRequest.getAttribute(FileDownload.Attribute.FILENAME.getValue());
                    String browser = httpRequest.getHeader("User-Agent");
                    // 파일 인코딩
                    if (browser.contains("MSIE") || browser.contains("Trident") || browser.contains("Chrome")) {
                        fileDownloadName = URLEncoder.encode(fileDownloadName, "UTF-8").replaceAll("\\+", "%20");
                    }
                    else {
                        fileDownloadName = new String(fileDownloadName.getBytes("UTF-8"), "ISO-8859-1");
                    }
                    httpResponse.setHeader("Content-Disposition", "attachment;filename=\"" + fileDownloadName + "\"");
                    httpResponse.setContentType("application/octer-stream");
                    httpResponse.setHeader("Content-Transfer-Encoding", "binary;");
                    httpResponse.getOutputStream().write(response);
                }
                else {
                    System.out.println(2222);
                    // 화면에 올리기 위해 시스템 인코딩으로 변경한다.
                    viewResponse = CharsetConvertUtil.convert(response, Charset.forName(channel.getCharsetEnum().toString()), Charset.defaultCharset());
                }
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = e.getMessage().getBytes();
            }
            model.addAttribute("inRequest", param.get("inRequest"));
            model.addAttribute("type", "인바운드채널(서버)");
            model.addAttribute("response", new String(viewResponse));
            model.addAttribute("channelId", param.get("inBoundChannelId"));
        }
        else {
            OutBoundChannelImpl channel = channelManager.getOutBoundChannel(param.get("outBoundChannelId"));
            OutBoundRequest outBoundRequest = new OutBoundRequest();
            outBoundRequest.setPayload(param.get("outRequest").getBytes());
            OutBoundResponse outBoundResponse = channel.sendAndReceive(outBoundRequest);
            byte[] response = outBoundResponse.getPayload();
            model.addAttribute("type", "아웃바운드채널(클라이언트)");
            model.addAttribute("outRequest", param.get("outRequest"));
            model.addAttribute("response", new String(response));
            model.addAttribute("channelId", param.get("outBoundChannelId"));
        }
        
    }
    
    @RequestMapping(value = "/test1", method = RequestMethod.GET)
    public void test1(@RequestParam Map<String, String> param, ModelMap model) {
        InBoundChannelImpl inBound = channelManager.getInBoundChannel("CH0019");
        Map<String, ChannelHandlerContext> mClients = inBound.getAllChannels();
        Iterator<String> it = mClients.keySet().iterator();
        while (it.hasNext()) {
            String clientId = it.next();
            ChannelHandlerContext ctx = mClients.get(clientId);
            System.out.println("clientId:" + clientId);
            byte[] rb = inBound.sendAndReceive(clientId, "000011test message".getBytes());
            System.out.println("rb[" + new String(rb) + "]");
        }
    }
}
