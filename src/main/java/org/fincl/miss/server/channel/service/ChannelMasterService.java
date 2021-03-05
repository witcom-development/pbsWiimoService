package org.fincl.miss.server.channel.service;

import java.util.List;

public interface ChannelMasterService {
    
    public List<Channel> getChannelList();
    
    public Channel getChannel(Channel channelVO);
    
    public List<Channel> getMatchingChannel(Channel channelVO);
}
