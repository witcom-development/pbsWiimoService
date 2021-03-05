package org.fincl.miss.server.channel.service;

import java.util.List;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

//@Mapper("ChannelMasterMapper")
@Mapper("channelMasterMapper")
public interface ChannelMasterMapper {
    
    public List<Channel> getChannelList();
    
    public Channel getChannel(Channel channelVO);
    
    public List<Channel> getMatchingChannel(Channel channelVO);
    
}
