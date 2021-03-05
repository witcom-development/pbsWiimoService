package org.fincl.miss.server.channel;

import io.netty.channel.ChannelException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.server.channel.ChannelManager;
import org.fincl.miss.server.channel.inbound.FlashPolicyInBoundChannel;
import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.channel.inbound.ServletInBoundChannel;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.channel.service.Channel;
import org.fincl.miss.server.channel.service.ChannelMasterService;
import org.fincl.miss.server.exeption.ChannelControlException;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.Charset;
import org.fincl.miss.server.util.EnumCode.InOutModeType;
import org.fincl.miss.server.util.EnumCode.ProtocolType;
import org.fincl.miss.server.util.EnumCode.RequestDataType;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("channelManager")
public class ChannelManagerImpl implements ChannelManager {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ChannelMasterService channelMasterService;
    
    @Autowired
    private ServiceHandler serviceHandler;
    
    @Autowired
    private Properties serverProps;
    
    private Map<String, OutBoundChannelImpl> outBoundChannel = new LinkedHashMap<String, OutBoundChannelImpl>();
    private Map<String, InBoundChannelImpl> inBoundChannel = new LinkedHashMap<String, InBoundChannelImpl>();
    
    private ListenInBoundChannel inBoundTrafficMonitoringChannel = null;
    
    private FlashPolicyInBoundChannel flashPolicyInBoundChannel = null;
    
    @PostConstruct
    private void init() throws ServerException {
        
        // Flash Policy socket
        flashPolicyInBoundChannel = new FlashPolicyInBoundChannel(null, null);
        flashPolicyInBoundChannel.startup();
        
        // Traffic Monitoring WebSocket Channel Open
        openTrafficMonitoringWebSocket();
        
        List<Channel> listChannel = channelMasterService.getChannelList();
        
        for (Channel channel : listChannel) {
            
            InOutModeType inOutModeType = InOutModeType.getEnum(channel.getInOutTypeCode());
            
            if (inOutModeType == InOutModeType.CLIENT) {
                logger.debug("load outBound(Client) channel - {}", channel);
                addOutBoundChannel(channel);
            }
            else if (inOutModeType == InOutModeType.SERVER) {
                logger.debug("load inBound(Server) channel - {}", channel);
                addInBoundChannel(channel);
            }
            else {
                logger.info("unknown IN/OUT Type {}", channel);
            }
        }
    }
    
    @PreDestroy
    private void end() {
        
        if (inBoundTrafficMonitoringChannel != null) {
            inBoundTrafficMonitoringChannel.shutdown();
        }
        
        if (flashPolicyInBoundChannel != null) {
            flashPolicyInBoundChannel.shutdown();
        }
        
        logger.debug("outbound manager end start");
        Iterator<String> itOutBound = this.outBoundChannel.keySet().iterator();
        while (itOutBound.hasNext()) {
            String sKey = itOutBound.next();
            OutBoundChannelImpl outBoundChannel = this.outBoundChannel.get(sKey);
            // if (outboundChannel.getChannel() != null && outboundChannel.getChannel().isActive()) {
            outBoundChannel.shutdown();
            // }
            // this.htChannel.remove(sKey); // 왼쪽과 같이 할 경우 여러 데이터가 있을때, java.util.ConcurrentModificationException 오류가 발생함.
            itOutBound.remove();
        }
        logger.debug("outbound manager end end");
        
        logger.debug("inbound manager end start");
        Iterator<String> itInBound = this.inBoundChannel.keySet().iterator();
        while (itInBound.hasNext()) {
            String sKey = itInBound.next();
            InBoundChannelImpl inBoundChannel = this.inBoundChannel.get(sKey);
            // if (inBoundChannel.getChannel() != null && inBoundChannel.getChannel().isActive()) {
            inBoundChannel.shutdown();
            // }
            // this.htChannel.remove(sKey); //왼쪽과 같이 할 경우 여러 데이터가 있을때, java.util.ConcurrentModificationException 오류가 발생함.
            itInBound.remove();
        }
        logger.debug("outbound manager end end");
        
    }
    
    private void openTrafficMonitoringWebSocket() {
        Channel channel = new Channel();
        channel.setPort(Integer.parseInt(serverProps.getProperty("monitoring.traffic.port", "3333")));
        channel.setAutoStartYn("Y");
        channel.setChannelId("MONTOR");
        channel.setChannelName("TrafficMonitoring");
        channel.setCharsetCode(Charset.UTF_8.toString());
        channel.setInOutTypeCode(InOutModeType.SERVER.getValue());
        channel.setProtocolTypeCode(ProtocolType.WEBSOCKET.getValue());
        channel.setRequestDataTypeCode(RequestDataType.JSON.getValue());
        channel.setResponseDataTypeCode(ResponseDataType.JSON.getValue());
        
        inBoundTrafficMonitoringChannel = ApplicationContextSupport.getBean(ListenInBoundChannel.class, new Object[] { serviceHandler, channel });
    }
    
    public List<OutBoundChannelImpl> getOutBoundChannelList() {
        List<OutBoundChannelImpl> list = new ArrayList<OutBoundChannelImpl>();
        Iterator<String> it = this.outBoundChannel.keySet().iterator();
        while (it.hasNext()) {
            String sKey = it.next();
            OutBoundChannelImpl outBoundChannel = this.outBoundChannel.get(sKey);
            list.add(outBoundChannel);
        }
        return list;
    }
    
    public List<InBoundChannelImpl> getInBoundChannelList() {
        List<InBoundChannelImpl> list = new ArrayList<InBoundChannelImpl>();
        Iterator<String> it = this.inBoundChannel.keySet().iterator();
        while (it.hasNext()) {
            String sKey = it.next();
            InBoundChannelImpl inBoundChannel = this.inBoundChannel.get(sKey);
            list.add(inBoundChannel);
        }
        return list;
    }
    
    public OutBoundChannelImpl getOutBoundChannel(String channelId) {
        OutBoundChannelImpl outBoundChannel = this.outBoundChannel.get(channelId);
        return outBoundChannel;
    }
    
    public InBoundChannelImpl getInBoundChannel(String channelId) {
        InBoundChannelImpl inBoundChannel = this.inBoundChannel.get(channelId);
        return inBoundChannel;
    }
    
    public boolean loadOutBoundChannel(String channelId) {
        Channel paramChannel = new Channel();
        paramChannel.setChannelId(channelId);
        paramChannel.setInOutTypeCode(InOutModeType.CLIENT.getValue());
        Channel channel = channelMasterService.getChannel(paramChannel);
        if (channel == null) {
            throw new ChannelControlException(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR);
        }
        else {
            boolean bAddResult = addOutBoundChannel(channel);
            if (bAddResult) {
                return bAddResult;
            }
            else {
                return false;
            }
        }
    }
    
    public boolean loadInBoundChannel(String channelId) {
        Channel paramChannel = new Channel();
        paramChannel.setChannelId(channelId);
        paramChannel.setInOutTypeCode(InOutModeType.SERVER.getValue());
        Channel channel = channelMasterService.getChannel(paramChannel);
        if (channel == null) {
            throw new ChannelControlException(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR);
        }
        else {
            boolean bAddResult = addInBoundChannel(channel);
            if (bAddResult) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    
    public boolean unloadInBoundchannel(String channelId) {
        Channel paramChannel = new Channel();
        paramChannel.setChannelId(channelId);
        paramChannel.setInOutTypeCode(InOutModeType.SERVER.getValue());
        Channel channel = channelMasterService.getChannel(paramChannel);
        if (channel == null) {
            throw new ChannelControlException(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR);
        }
        else {
            boolean bRemoveResult = removeInBoundChannel(channelId);
            if (bRemoveResult) {
                return bRemoveResult;
            }
            else {
                return false;
            }
        }
    }
    
    public boolean unloadOutBoundchannel(String channelId) {
        Channel paramChannel = new Channel();
        paramChannel.setChannelId(channelId);
        paramChannel.setInOutTypeCode(InOutModeType.CLIENT.getValue());
        Channel channel = channelMasterService.getChannel(paramChannel);
        if (channel == null) {
            throw new ChannelControlException(ErrorConstant.CHANNEL_CONTROL_NOT_EXIST_ERROR);
        }
        else {
            boolean bRemoveResult = removeOutBoundChannel(channelId);
            if (bRemoveResult) {
                return bRemoveResult;
            }
            else {
                return false;
            }
        }
    }
    
    public boolean addOutBoundChannel(Channel channel) throws ChannelException {
        boolean bResult = false;
        // OutBoundChannel outBoundChannel = new OutBoundChannel(serviceHandler, channelVO);
        // ApplicationContextSupport.getRootBean(beanName, clazz)
        OutBoundChannelImpl outBoundChannel = ApplicationContextSupport.getBean(OutBoundChannelImpl.class, new Object[] { serviceHandler, channel });
        System.out.println("GGG444:" + outBoundChannel.getChannelId() + " " + outBoundChannel.getAutoStartYn());
        this.outBoundChannel.put(channel.getChannelId(), outBoundChannel);
        bResult = true;
        return bResult;
    }
    
    public boolean addInBoundChannel(Channel channel) throws ChannelException {
        boolean bResult = false;
        
        InBoundChannelImpl inBoundChannel = null;
        if (channel.getProtocolTypeEnum() == ProtocolType.SERVLET) {
            inBoundChannel = ApplicationContextSupport.getBean(ServletInBoundChannel.class, new Object[] { serviceHandler, channel });
        }
        else {
            // new ExtListenInBoundChannel(serviceHandler, channelVO);
            inBoundChannel = ApplicationContextSupport.getBean(ListenInBoundChannel.class, new Object[] { serviceHandler, channel });
        }
        this.inBoundChannel.put(channel.getChannelId(), inBoundChannel);
        bResult = true;
        return bResult;
    }
    
    public boolean removeOutBoundChannel(String channelId) throws ChannelException {
        boolean bResult = false;
        OutBoundChannelImpl outboundChannel = this.outBoundChannel.get(channelId);
        if (outboundChannel != null) {
            outboundChannel.shutdown();
            this.outBoundChannel.remove(channelId);
            bResult = true;
        }
        else {
            bResult = false;
        }
        return bResult;
    }
    
    public boolean removeInBoundChannel(String channelId) throws ChannelException {
        boolean bResult = false;
        BoundChannel inboundChannel = this.inBoundChannel.get(channelId);
        if (inboundChannel != null) {
            inboundChannel.shutdown();
            this.inBoundChannel.remove(channelId);
            bResult = true;
        }
        else {
            bResult = false;
        }
        return bResult;
    }
    
    public ListenInBoundChannel getInBoundTrafficMonitoringChannel() {
        return inBoundTrafficMonitoringChannel;
    }
    
    public void setInBoundTrafficMonitoringChannel(ListenInBoundChannel inBoundTrafficMonitoringChannel) {
        this.inBoundTrafficMonitoringChannel = inBoundTrafficMonitoringChannel;
    }
}
