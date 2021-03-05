package org.fincl.miss.server.channel.service;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.Charset;
import org.fincl.miss.server.util.EnumCode.DataRawType;
import org.fincl.miss.server.util.EnumCode.InOutModeType;
import org.fincl.miss.server.util.EnumCode.ProtocolType;
import org.fincl.miss.server.util.EnumCode.RequestDataType;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.fincl.miss.server.util.EnumCode.SSLYn;
import org.fincl.miss.server.util.EnumCode.SingleYn;
import org.fincl.miss.server.util.EnumCode.SyncType;

public class Channel {
    
    @Required
    private String channelId; // 채널ID
    private String channelName; // 채널명
    private String channelDescription; // 채널상세
    private String inOutTypeCode; // 인/아웃 유형 코드
    private int port; // 포트
    private String protocolTypeCode; // 프로토콜 유형 코드
    private String requestDataTypeCode; // 요청 데이터 유형 코드
    private String requestDataOption; // 요청 데이터 유형 부가 정보
    private String responseDataTypeCode; // 응답 데이터 유형 코드
    private String responseDataOption; // 응답 데이터 유형 부가 정보
    private String singleYn; // 싱글여부
    private String sslYn; // SSL여부
    private String autoStartYn; // 자동시작 여부
    private String outBoundChannelHost; // 아웃바운드 채널 호스트
    private String outBoundHttpUri; // 아웃바운드 요청 URI
    private String createId; // 작성자
    private Date createDate; // 작성일
    private String modifyId; // 수정자
    private Date modifyDate; // 수정일
    
    private InOutModeType inOutTypeEnum;
    private ProtocolType protocolTypeEnum;
    private RequestDataType requestDataTypeEnum;
    private ResponseDataType responseDataTypeEnum;
    private SingleYn singleYnEnum;
    private SSLYn sslYnEnum;
    private AutoStartYn autoStartYnEnum;
    
    private DataRawType txRawDataTypeEnum;
    private String txRawDataTypeCode;
    private int headerLengthSize;
    private int stxSize;
    private String stxHex;
    private int etxSize;
    private String etxHex;
    private int eotSize;
    private String eotHex;
    
    private Charset charsetEnum;
    private String charsetCode;
    private int bufferSize;
    private int timeoutSeconds;
    private int threadCount;
    
    private String relationId;
    private String syncTypeCode;
    
    public SyncType getSyncTypeEnum() {
        return syncTypeEnum;
    }
    
    public void setSyncTypeEnum(SyncType syncTypeEnum) {
        this.syncTypeEnum = syncTypeEnum;
    }
    
    public String getSyncTypeCode() {
        return syncTypeCode;
    }
    
    public void setSyncTypeCode(String syncTypeCode) {
        this.syncTypeEnum = syncTypeEnum.getEnum(syncTypeCode);
        this.syncTypeCode = syncTypeCode;
    }
    
    private SyncType syncTypeEnum;
    
    public String getRelationId() {
        return relationId;
    }
    
    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }
    
    public Charset getCharsetEnum() {
        return charsetEnum;
    }
    
    public void setCharsetEnum(Charset charsetEnum) {
        this.charsetEnum = charsetEnum;
    }
    
    public String getCharsetCode() {
        return charsetCode;
    }
    
    public void setCharsetCode(String charsetCode) {
        this.charsetEnum = Charset.getEnum(charsetCode);
        this.charsetCode = charsetCode;
    }
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    
    public int getThreadCount() {
        return threadCount;
    }
    
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
    
    public DataRawType getTxRawDataTypeEnum() {
        return txRawDataTypeEnum;
    }
    
    public void setTxRawDataTypeEnum(DataRawType txRawDataTypeEnum) {
        this.txRawDataTypeEnum = txRawDataTypeEnum;
    }
    
    public String getTxRawDataTypeCode() {
        return txRawDataTypeCode;
    }
    
    public void setTxRawDataTypeCode(String txRawDataTypeCode) {
        this.txRawDataTypeEnum = DataRawType.getEnum(txRawDataTypeCode);
        this.txRawDataTypeCode = txRawDataTypeCode;
    }
    
    public int getHeaderLengthSize() {
        return headerLengthSize;
    }
    
    public void setHeaderLengthSize(int headerLengthSize) {
        this.headerLengthSize = headerLengthSize;
    }
    
    public int getStxSize() {
        return stxSize;
    }
    
    public void setStxSize(int stxSize) {
        this.stxSize = stxSize;
    }
    
    public String getStxHex() {
        return stxHex;
    }
    
    public void setStxHex(String stxHex) {
        this.stxHex = stxHex;
    }
    
    public int getEtxSize() {
        return etxSize;
    }
    
    public void setEtxSize(int etxSize) {
        this.etxSize = etxSize;
    }
    
    public String getEtxHex() {
        return etxHex;
    }
    
    public void setEtxHex(String etxHex) {
        this.etxHex = etxHex;
    }
    
    public int getEotSize() {
        return eotSize;
    }
    
    public void setEotSize(int eotSize) {
        this.eotSize = eotSize;
    }
    
    public String getEotHex() {
        return eotHex;
    }
    
    public void setEotHex(String eotHex) {
        this.eotHex = eotHex;
    }
    
    public InOutModeType getInOutTypeEnum() {
        return inOutTypeEnum;
    }
    
    public ProtocolType getProtocolTypeEnum() {
        return protocolTypeEnum;
    }
    
    public void setProtocolTypeEnum(ProtocolType protocolTypeEnum) {
        this.protocolTypeEnum = protocolTypeEnum;
    }
    
    public void setInOutTypeEnum(InOutModeType inOutTypeEnum) {
        this.inOutTypeEnum = inOutTypeEnum;
    }
    
    public RequestDataType getRequestDataTypeEnum() {
        return requestDataTypeEnum;
    }
    
    public void setRequestDataTypeEnum(RequestDataType requestDataTypeEnum) {
        this.requestDataTypeEnum = requestDataTypeEnum;
    }
    
    public ResponseDataType getResponseDataTypeEnum() {
        return responseDataTypeEnum;
    }
    
    public void setResponseDataTypeEnum(ResponseDataType responseDataTypeEnum) {
        this.responseDataTypeEnum = responseDataTypeEnum;
    }
    
    public SingleYn getSingleYnEnum() {
        return singleYnEnum;
    }
    
    public void setSingleYnEnum(SingleYn singleYnEnum) {
        this.singleYnEnum = singleYnEnum;
    }
    
    public SSLYn getSslYnEnum() {
        return sslYnEnum;
    }
    
    public void setSslYnEnum(SSLYn sslYnEnum) {
        this.sslYnEnum = sslYnEnum;
    }
    
    public AutoStartYn getAutoStartYnEnum() {
        return autoStartYnEnum;
    }
    
    public void setAutoStartYnEnum(AutoStartYn autoStartYnEnum) {
        this.autoStartYnEnum = autoStartYnEnum;
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    
    public String getChannelName() {
        return channelName;
    }
    
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
    
    public String getChannelDescription() {
        return channelDescription;
    }
    
    public void setChannelDescription(String channelDescription) {
        this.channelDescription = channelDescription;
    }
    
    public String getInOutTypeCode() {
        return inOutTypeCode;
    }
    
    public void setInOutTypeCode(String inOutTypeCode) {
        setInOutTypeEnum(InOutModeType.getEnum(inOutTypeCode));
        this.inOutTypeCode = inOutTypeCode;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getProtocolTypeCode() {
        return protocolTypeCode;
    }
    
    public void setProtocolTypeCode(String protocolTypeCode) {
        setProtocolTypeEnum(ProtocolType.getEnum(protocolTypeCode));
        this.protocolTypeCode = protocolTypeCode;
    }
    
    public String getRequestDataTypeCode() {
        return requestDataTypeCode;
    }
    
    public void setRequestDataTypeCode(String requestDataTypeCode) {
        setRequestDataTypeEnum(RequestDataType.getEnum(requestDataTypeCode));
        this.requestDataTypeCode = requestDataTypeCode;
    }
    
    public String getRequestDataOption() {
        return requestDataOption;
    }
    
    public void setRequestDataOption(String requestDataOption) {
        this.requestDataOption = requestDataOption;
    }
    
    public String getResponseDataTypeCode() {
        return responseDataTypeCode;
    }
    
    public void setResponseDataTypeCode(String responseDataTypeCode) {
        setResponseDataTypeEnum(ResponseDataType.getEnum(responseDataTypeCode));
        this.responseDataTypeCode = responseDataTypeCode;
    }
    
    public String getResponseDataOption() {
        return responseDataOption;
    }
    
    public void setResponseDataOption(String responseDataOption) {
        
        this.responseDataOption = responseDataOption;
    }
    
    public String getSingleYn() {
        return singleYn;
    }
    
    public void setSingleYn(String singleYn) {
        setSingleYnEnum(SingleYn.getEnum(singleYn));
        this.singleYn = singleYn;
    }
    
    public String getSslYn() {
        return sslYn;
    }
    
    public void setSslYn(String sslYn) {
        setSslYnEnum(SSLYn.getEnum(sslYn));
        this.sslYn = sslYn;
    }
    
    public String getAutoStartYn() {
        return autoStartYn;
    }
    
    public void setAutoStartYn(String autoStartYn) {
        setAutoStartYnEnum(AutoStartYn.getEnum(autoStartYn));
        this.autoStartYn = autoStartYn;
    }
    
    public String getOutBoundChannelHost() {
        return outBoundChannelHost;
    }
    
    public void setOutBoundChannelHost(String outBoundChannelHost) {
        this.outBoundChannelHost = outBoundChannelHost;
    }
    
    public String getOutBoundHttpUri() {
        return outBoundHttpUri;
    }
    
    public void setOutBoundHttpUri(String outBoundHttpUri) {
        this.outBoundHttpUri = outBoundHttpUri;
    }
    
    public String getCreateId() {
        return createId;
    }
    
    public void setCreateId(String createId) {
        this.createId = createId;
    }
    
    public Date getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    public String getModifyId() {
        return modifyId;
    }
    
    public void setModifyId(String modifyId) {
        this.modifyId = modifyId;
    }
    
    public Date getModifyDate() {
        return modifyDate;
    }
    
    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
