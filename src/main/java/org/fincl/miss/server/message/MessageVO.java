package org.fincl.miss.server.message;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServiceHandlerException;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

abstract public class MessageVO implements MessageInterfaceVO, Serializable {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private static final long serialVersionUID = 5305615972821773336L;
    
    private String id;
    
    private String interfaceId;
    
    private String guid;
    
    private String serviceId;
    
    private String clientIp;
    
    private String clientId;
    
    private String resultCode = ErrorConstant.SUCCESS;
    
    private String resultMessage = "";
    
    private String reqMessage = "";
    
    private boolean invokable = true;
    
    private LinkedHashMap<String, Object> headerVoMap = null;
    
    private MessageHeader messageHeader;
    
    
    public MessageVO() {
        // this.messageHeader = new MessageHeader(null);
        this.messageHeader = ServiceMessageHeaderContext.getMessageHeader();
    }
    
    public MessageVO(Map<String, Object> headers) {
        this.messageHeader = new MessageHeader(headers);
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    @Override
    public void setMessageHeader(MessageHeader messageHeader) {
        if (messageHeader == null) {
            // this.messageHeader = new HashMap<String, Object>();
            this.messageHeader = new MessageHeader(new HashMap<String, Object>());
        }
        else {
            Map<String, Object> appendMessageHeader = new HashMap<String, Object>();
            if (StringUtils.isNotEmpty(getClientId())) {
                appendMessageHeader.put(MessageHeader.CLIENT_ID, getClientId());
            }
            if (StringUtils.isNotEmpty(getClientIp())) {
                appendMessageHeader.put(MessageHeader.CLIENT_IP, getClientIp());
            }
            if (StringUtils.isNotEmpty(getId())) {
                appendMessageHeader.put(MessageHeader.ID, getId());
                appendMessageHeader.put(MessageHeader.CORRELATION_ID, getId());
            }
            else {
                setId(messageHeader.getId());
            }
            if (StringUtils.isNotEmpty(getGuid())) {
                appendMessageHeader.put(MessageHeader.GUID, getGuid());
            }
            
            appendMessageHeader.put(MessageHeader.SERVICE_ID, getServiceId());
            messageHeader.putAll(appendMessageHeader);
            
            this.messageHeader = messageHeader;
        }
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    @Override
    public MessageHeader getMessageHeader() {
        if (getHeaderVoMap() != null) {
            Map<String, Object> mPayloadHeader = Maps.newHashMap();
            try {
                mPayloadHeader = PropertyUtils.describe(getHeaderVoMap());
            }
            catch (IllegalAccessException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                throw new ServiceHandlerException(ErrorConstant.MESSAGE_HEADER_ERROR, e);
            }
            catch (InvocationTargetException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                throw new ServiceHandlerException(ErrorConstant.MESSAGE_HEADER_ERROR, e);
            }
            catch (NoSuchMethodException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                throw new ServiceHandlerException(ErrorConstant.MESSAGE_HEADER_ERROR, e);
            }
            
            this.messageHeader.putAll(mPayloadHeader);
        }
        
        return this.messageHeader;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public LinkedHashMap<String, Object> getHeaderVoMap() {
        if (headerVoMap == null) headerVoMap = new LinkedHashMap<String, Object>();
        return headerVoMap;
    }
    
    public void setHeaderVoMap(LinkedHashMap<String, Object> headerVoMap) {
        this.headerVoMap = headerVoMap;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getInterfaceId() {
        return interfaceId;
    }
    
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public boolean isInvokable() {
        return invokable;
    }
    
    public void setInvokable(boolean invokable) {
        this.invokable = invokable;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getGuid() {
        return guid;
    }
    
    public void setGuid(String guid) {
        this.guid = guid;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getResultMessage() {
        return resultMessage;
    }
    
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
    
    @org.codehaus.jackson.annotate.JsonIgnore
    public String getResultCode() {
        return resultCode;
    }
    
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
    
    public String getReqMessage() {
		return reqMessage;
	}

	public void setReqMessage(String reqMessage) {
		this.reqMessage = reqMessage;
	}

	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
}
