package org.fincl.miss.server.message.parser.telegram.valueobjects;

import org.fincl.miss.server.message.parser.telegram.Message;

/**
 * - 전문을 나타내는 유일한 식별자(Key)를 가져오는 인터페이스 -
 * 
 * 
 */

public interface InterfaceIdVo {
    public String getId(Message message);
    
    public void setId(String id, Message message);
    
    public String getServiceId(Message message);
    
    public void setServiceId(String id, Message message);
    
    public String getTrnmSysDcd(Message message);
    
    public String getRqstRspnDcd(Message message);
    
    public String getWhbnSttlSrn(Message message);
    
    // public String getSystemCode(Message message);
    // public String getTransactionDate(Message message);
    // public String getTransactionTime(Message message);
    // public String getHostName(Message message);
    public String getGlobalId(Message message);
    
    public String getInterfaceId(Message message);
    
    // public String getResult(Message message);
    // public String getErrorCode(Message message);
    // public String getErrorMessage(Message message);
    // public String getRequestResponseCode(Message message);
    // public String getMessageVersion(Message message);
    // public String getBodyVersion(Message message);
    
    // public void setSystemCode(String systemCode, Message message);
    public void setGlobalId(String globalID, Message message);
    
    public void setInterfaceId(String interfaceID, Message message);
    // public void setResult(String result, Message message);
    // public void setRequestResponseCode(String reqeustResponseCode, Message message);
    // public void setMessageVersion(String messageVersion, Message message);
    // public void setBodyVersion(String bodyVersion, Message message);
}
