package org.fincl.miss.server.message;

import java.util.LinkedHashMap;

public interface MessageInterfaceVO {
    
    public void setMessageHeader(MessageHeader messageHeader);
    
    public String getId();
    
    public String getInterfaceId();
    
    public String getGuid();
    
    public String getServiceId();
    
    public String getClientIp();
    
    public String getClientId();
    
    public String getResultCode();
    
    public String getResultMessage();
    
    public boolean isInvokable();
    
    public MessageHeader getMessageHeader();
    
    public LinkedHashMap<String, Object> getHeaderVoMap();
    
}
