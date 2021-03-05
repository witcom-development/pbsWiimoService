package org.fincl.miss.server.message.parser;

import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.service.ServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;

import com.dkitec.cfood.core.CfoodException;

public abstract class MessageParser {
    
    @Autowired
    protected ServiceRegister serviceRegister;
    
    public ServiceRegister getServiceRegister() {
        return serviceRegister;
    }
    
    public void setServiceRegister(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;
    }
    
    abstract public MessageInterfaceVO parse(BoundChannel extChannel, byte[] message) throws MessageParserException;
    
    abstract public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVo) throws MessageParserException;
    
    abstract public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex);
    
}
