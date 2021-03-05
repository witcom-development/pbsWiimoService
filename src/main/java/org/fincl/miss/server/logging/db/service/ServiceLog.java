package org.fincl.miss.server.logging.db.service;

import org.fincl.miss.server.exeption.ErrorConstant;

public class ServiceLog {
    
    private String serverId;
    private String startDate;
    private String endDate;
    private String channelId;
    private String transactionId;
    private String correlationTransactionId;
    private int estimateTime;
    private String resultCode = ErrorConstant.SUCCESS;
    private String resultMessage = "";
    private String requestData;
    private String responseData;
    private String serviceId;
    private String clientId;
    private String clientIp;
    private String interfaceId;
    private String guid;
    
    private String device_id;
    private String device_link_type_cd;
    
    
    
    public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_link_type_cd() {
		return device_link_type_cd;
	}

	public void setDevice_link_type_cd(String device_link_type_cd) {
		this.device_link_type_cd = device_link_type_cd;
	}

	public String getServerId() {
        return serverId;
    }
    
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    
    public String getGuid() {
        return guid;
    }
    
    public void setGuid(String guid) {
        this.guid = guid;
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    
    public String getCorrelationTransactionId() {
        return correlationTransactionId;
    }
    
    public void setCorrelationTransactionId(String correlationTransactionId) {
        this.correlationTransactionId = correlationTransactionId;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getEndDate() {
        return endDate;
    }
    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getEstimateTime() {
        return estimateTime;
    }
    
    public void setEstimateTime(int estimateTime) {
        this.estimateTime = estimateTime;
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
    
    public String getRequestData() {
        return requestData;
    }
    
    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }
    
    public String getResponseData() {
        return responseData;
    }
    
    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
    
    public String getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientIp() {
        return clientIp;
    }
    
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }
    
    public String getInterfaceId() {
        return interfaceId;
    }
    
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }
    
}
