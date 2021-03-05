package org.fincl.miss.server.logging.db;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.bind.DatatypeConverter;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.logging.db.service.ServiceLog;
import org.fincl.miss.server.logging.db.service.ServiceLogService;
import org.fincl.miss.server.util.EnumCode.DataRawType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class ServiceLoggingHandler {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ThreadPoolTaskExecutor serviceLoggingTaskExecutor;
    
    @Autowired
    private ServiceLogService serviceLogService;
    
    @Autowired
    private Properties serverProps;
    
    @Autowired
    private ChannelManagerImpl channelManager;
    
    public void addLog(ServiceLogging serviceLoggingVO) {
        
        Date dStartTime = new Date(serviceLoggingVO.getStartTime());
        Date dEndTime = new Date(serviceLoggingVO.getEndTime());
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String strStartTime = formatter.format(dStartTime);
        String strEndTime = formatter.format(dEndTime);
        
        ServiceLog serviceLog = new ServiceLog();
        serviceLog.setServerId(serverProps.getProperty("serverId"));
        serviceLog.setChannelId(serviceLoggingVO.getExtChannel().getChannelId());
     try{
     	serviceLog.setTransactionId(serviceLoggingVO.getMessageHeader().getId());
        serviceLog.setCorrelationTransactionId(serviceLoggingVO.getMessageHeader().getCorrelationId());
        serviceLog.setClientId(serviceLoggingVO.getMessageHeader().getClientId());
     
     	serviceLog.setServiceId(serviceLoggingVO.getServiceId());
        serviceLog.setInterfaceId(serviceLoggingVO.getInterfaceId());
        serviceLog.setGuid(serviceLoggingVO.getGuid());
        serviceLog.setStartDate(strStartTime);
        serviceLog.setEndDate(strEndTime);
        if (serviceLoggingVO.getResultCode() != null) serviceLog.setResultCode(serviceLoggingVO.getResultCode());
        
        serviceLog.setEstimateTime((int) (serviceLoggingVO.getEndTime() - serviceLoggingVO.getStartTime()));
        
        serviceLog.setClientIp(serviceLoggingVO.getMessageHeader().getClientIp());
        
        if (serviceLoggingVO.getResMessageInterfaceVO() != null) serviceLog.setResultCode(serviceLoggingVO.getResMessageInterfaceVO().getResultCode());
        if (serviceLoggingVO.getResMessageInterfaceVO() != null) serviceLog.setResultMessage(serviceLoggingVO.getResMessageInterfaceVO().getResultMessage());
        
        // try {
        if (serviceLoggingVO.getReqMessage() != null) {
            // byte[] bMessage = CharsetConvertUtil.convert(serviceLoggingVO.getReqMessage(), Charset.defaultCharset(), Charset.forName(serviceLoggingVO.getExtChannel().getCharsetEnum().toString()));
            // System.out.println("UTF-8 =>" + new String(serviceLoggingVO.getReqMessage(), "UTF-8"));
            // System.out.println("EUC-KR =>" + new String(serviceLoggingVO.getReqMessage(), "EUC-KR"));
            if (serviceLoggingVO.getExtChannel().getTxRawDataTypeEnum() == DataRawType.STRING) {
                serviceLog.setRequestData(new String(serviceLoggingVO.getReqMessage(), Charset.defaultCharset()));
            }
            else if (serviceLoggingVO.getExtChannel().getTxRawDataTypeEnum() == DataRawType.BYTE) {
                serviceLog.setRequestData(DatatypeConverter.printHexBinary(serviceLoggingVO.getReqMessage()));
               // serviceLog.setDevice_link_type_cd("DIG_0" + DatatypeConverter.printHexBinary(serviceLoggingVO.getReqMessage()).substring(10,12));
                serviceLog.setDevice_link_type_cd(DatatypeConverter.printHexBinary(serviceLoggingVO.getReqMessage()).substring(10,12));
                serviceLog.setDevice_id(DatatypeConverter.printHexBinary(serviceLoggingVO.getReqMessage()).substring(14,28));
                
                
            }
        }
        if (serviceLoggingVO.getResMessage() != null) {
            // byte[] rMessage = CharsetConvertUtil.convert(serviceLoggingVO.getResMessage(), Charset.forName(serviceLoggingVO.getExtChannel().getCharsetEnum().toString()), Charset.defaultCharset());
            // System.out.println("UTF-8 =>" + new String(serviceLoggingVO.getResMessage(), "UTF-8"));
            // System.out.println("EUC-KR =>" + new String(serviceLoggingVO.getResMessage(), "EUC-KR"));
            if (serviceLoggingVO.getExtChannel().getTxRawDataTypeEnum() == DataRawType.STRING) {
                serviceLog.setResponseData(new String(serviceLoggingVO.getResMessage(), Charset.defaultCharset()));
            }
            else if (serviceLoggingVO.getExtChannel().getTxRawDataTypeEnum() == DataRawType.BYTE) {
                serviceLog.setResponseData(DatatypeConverter.printHexBinary(serviceLoggingVO.getResMessage()));
            }
        }
        
        ServiceLoggingTask serviceLoggingTask = ApplicationContextSupport.getBean(ServiceLoggingTask.class, new Object[] { serviceLog, serviceLoggingVO });
        serviceLoggingTaskExecutor.execute(serviceLoggingTask, 5000L); // 5sec
     }catch(Exception e){} 
        // try {
        // serviceLogService.addServiceLog(serviceLog);
        //
        // if (serviceLoggingVO.getExtChannel().getInOutTypeEnum() == InOutModeType.SERVER && serviceLoggingVO.getExtChannel().getSyncTypeEnum() != SyncType.PUBSUB) {
        // StringBuilder sb = new StringBuilder();
        // sb.append("{");
        // sb.append("\"server\":").append("\"").append(serverProps.getProperty("serverId")).append("\",");
        // sb.append("\"id\":").append("\"").append(serviceLog.getTransactionId()).append("\",");
        // sb.append("\"channelId\":").append("\"").append(serviceLog.getChannelId()).append("\",");
        // sb.append("\"serviceId\":").append("\"").append(serviceLog.getServerId()).append("\",");
        // sb.append("\"elapse\":").append(serviceLog.getEstimateTime()).append(",");
        // sb.append("\"resultCode\":").append("\"").append(serviceLog.getResultCode()).append("\",");
        // sb.append("\"resultMessage\":").append("\"").append(serviceLog.getResultMessage()).append("\"");
        // sb.append("}");
        // channelManager.getInBoundTrafficMonitoringChannel().broadcastTrafficMessage(sb.toString().getBytes());
        // }
        //
        // }
        // catch (Exception ex) {
        // logger.error("{} {}", new ServerException(ErrorConstant.INTERNAL_SQL_ERROR, ex).getMessage(), ex.getMessage());
        // }
    }
}
