package org.fincl.miss.server.channel;

import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.fincl.miss.server.channel.service.Channel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.service.ServiceHandler;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;

abstract public class BoundChannel extends Channel {
    
    public int backlog = 128;
    public boolean keepAlive = false;
    public boolean reUseAddress = true;
    public boolean tcpNoDelay = true;
    public int sndBuf = 65536;
    public int rcvBuf = 65536;
    public int wbHigh = 65536;
    public int wbLow = 1024;
    
    protected ChannelStatus channelStatus = ChannelStatus.DEAD;
    
    private Map<String, ChannelHandlerContext> allChannels = new ConcurrentHashMap<String, ChannelHandlerContext>();
    
    protected ServiceHandler serviceHandler;
    
    public BoundChannel() {
        
    }
    
    public BoundChannel(final ServiceHandler serviceHandler, final Channel channel) {
        
        this.serviceHandler = serviceHandler;
        
        BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
        
        if (channel != null) {
            try {
                BeanUtils.copyProperties(this, channel);
            }
            catch (IllegalAccessException e) {
                throw new ChannelException(ErrorConstant.CHANNEL_ERROR, e);
            }
            catch (InvocationTargetException e) {
                throw new ChannelException(ErrorConstant.CHANNEL_ERROR, e);
            }
        }
        if (AutoStartYn.getEnum(getAutoStartYn()) == AutoStartYn.YES) {
            startup();
        }
    }
    
    public Map<String, ChannelHandlerContext> getAllChannels() {
        return allChannels;
    }
    
    public void setAllChannels(Map<String, ChannelHandlerContext> allChannels) {
        this.allChannels = allChannels;
    }
    
    abstract public boolean startup() throws ServerException;
    
    abstract public boolean shutdown() throws ServerException;
    
    abstract public ChannelStatus getStatus() throws ServerException;
    
}
