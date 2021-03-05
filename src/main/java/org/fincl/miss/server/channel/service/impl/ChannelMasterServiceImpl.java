package org.fincl.miss.server.channel.service.impl;

import java.util.List;

import org.fincl.miss.server.channel.service.ChannelMasterMapper;
import org.fincl.miss.server.channel.service.ChannelMasterService;
import org.fincl.miss.server.channel.service.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service(value="channelMasterService")
public class ChannelMasterServiceImpl extends EgovAbstractServiceImpl implements ChannelMasterService {
    
    @Autowired
    private ChannelMasterMapper channelMasterMapper;
    
    @Override
    public List<Channel> getChannelList() {
        return channelMasterMapper.getChannelList();
    }
    
    @Override
    public Channel getChannel(Channel channelVO) {
        return channelMasterMapper.getChannel(channelVO);
    }
    
    @Override
    public List<Channel> getMatchingChannel(Channel channelVO) {
        return channelMasterMapper.getMatchingChannel(channelVO);
    }
    
}
