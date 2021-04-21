package org.fincl.miss.server.logging.db;

import java.util.Properties;

import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.logging.db.service.ServiceLog;
import org.fincl.miss.server.logging.db.service.ServiceLogService;
import org.fincl.miss.server.util.EnumCode.InOutModeType;
import org.fincl.miss.server.util.EnumCode.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("serviceLoggingTask")
public class ServiceLoggingTask implements Runnable {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ServiceLogService serviceLogService;
    
    @Autowired
    private ChannelManagerImpl channelManager;
    
    @Autowired
    private Properties serverProps;
    
    private ServiceLogging serviceLogging;
    
    private ServiceLog serviceLog;
    
    public ServiceLoggingTask() {
        
    }
    
    public ServiceLoggingTask(ServiceLog serviceLog, ServiceLogging serviceLogging) {
        this.serviceLog = serviceLog;
        this.serviceLogging = serviceLogging;
    }
    
    @Override
    public void run() {
        try {
     //      serviceLogService.addServiceLog(serviceLog);
            
        	/*
            if (serviceLogging.getExtChannel().getInOutTypeEnum() == InOutModeType.SERVER && serviceLogging.getExtChannel().getSyncTypeEnum() != SyncType.PUBSUB) {
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                sb.append("\"server\":").append("\"").append(serverProps.getProperty("serverId")).append("\",");
                sb.append("\"id\":").append("\"").append(serviceLog.getTransactionId()).append("\",");
                sb.append("\"channelId\":").append("\"").append(serviceLog.getChannelId()).append("\",");
                sb.append("\"serviceId\":").append("\"").append(serviceLog.getServiceId()).append("\",");
                sb.append("\"elapse\":").append(serviceLog.getEstimateTime()).append(",");
                sb.append("\"resultCode\":").append("\"").append(serviceLog.getResultCode()).append("\",");
                sb.append("\"resultMessage\":").append("\"").append(serviceLog.getResultMessage()).append("\"");
                sb.append("}");
                channelManager.getInBoundTrafficMonitoringChannel().broadcastTrafficMessage(sb.toString().getBytes());
            }
            */
            
        }
        catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                ex.printStackTrace();
            }
            logger.error("{} {}", new ServerException(ErrorConstant.INTERNAL_SQL_ERROR, ex).getMessage(), ex.getMessage());
        }
    }
}
