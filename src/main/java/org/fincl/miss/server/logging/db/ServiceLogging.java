package org.fincl.miss.server.logging.db;

import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;

public class ServiceLogging {
    
    private MessageHeader messageHeader;
    
    private long startTime;
    private long endTime;
    
    private MessageInterfaceVO reqMessageInterfaceVO;
    private MessageInterfaceVO resMessageInterfaceVO;
    
    private BoundChannel extChannel;
    
    private byte[] reqMessage;
    private byte[] resMessage;
    
    private String resultCode;
    private String resultMessage;
    
    private String serviceId;
    private String interfaceId;
    private String guid;
    
    public String getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getGuid() {
        return guid;
    }
    
    public void setGuid(String guid) {
        this.guid = guid;
    }
    
    public String getResultCode() {
        return resultCode;
    }
    
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
    
    public String getResultMessage() {
        return resultMessage;
    }
    
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    public MessageInterfaceVO getReqMessageInterfaceVO() {
        return reqMessageInterfaceVO;
    }
    
    public void setReqMessageInterfaceVO(MessageInterfaceVO reqMessageInterfaceVO) {
        this.reqMessageInterfaceVO = reqMessageInterfaceVO;
    }
    
    public MessageInterfaceVO getResMessageInterfaceVO() {
        return resMessageInterfaceVO;
    }
    
    public void setResMessageInterfaceVO(MessageInterfaceVO resMessageInterfaceVO) {
        this.resMessageInterfaceVO = resMessageInterfaceVO;
    }
    
    public BoundChannel getExtChannel() {
        return extChannel;
    }
    
    public void setExtChannel(BoundChannel extChannel) {
        this.extChannel = extChannel;
    }
    
    public byte[] getReqMessage() {
        return reqMessage;
    }
    
    public void setReqMessage(byte[] reqMessage) {
        this.reqMessage = reqMessage;
    }
    
    public byte[] getResMessage() {
        return resMessage;
    }
    
    public void setResMessage(byte[] resMessage) {
        this.resMessage = resMessage;
    }
    
    public String getInterfaceId() {
        return interfaceId;
    }
    
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    
    public MessageHeader getMessageHeader() {
        return messageHeader;
    }
    
    public void setMessageHeader(MessageHeader messageHeader) {
        this.messageHeader = messageHeader;
    }
    
}
