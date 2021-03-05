package org.fincl.miss.server.logging.logger.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.SocketAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.fincl.miss.core.logging.LogInfo;
import org.fincl.miss.core.logging.service.LogInfoService;
import org.fincl.miss.server.logging.logger.ServiceLogLevelFilter;
import org.fincl.miss.server.logging.logger.service.LogSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dkitec.cfood.core.logging.DirectRollingFileAppender;

public class LogSyncServiceImpl implements LogSyncService {

	private Map<String, Map<String, LogInfo>> logInfos;
	
	@Value("#{serverProps.serverId}")
	private String serverId;
	
	// 서비스 레벨 관리 Logger 이름
	private String serviceLevelMof = "org.fincl.miss";
	
	// 서비스 레벨 관리 필터가 적용된 어펜더 이름
	private String serviceFilter = "serviceMapping";
	
	@Autowired
	private LogInfoService logInfoService;


	// bean등록 시 map초기화 작업
	@PostConstruct
	private void initLogInfoMap() {
		Map<String, LogInfo> logInfoMap = new HashMap<String, LogInfo>();
		
		List<LogInfo> dbLogInfo = logInfoService.getLogInfoList(serverId);
		
		// 로그context 조회
		LoggerContext logContext = (LoggerContext)LogManager.getContext(false);
		
		if(logContext != null) {
			Map<String, LoggerConfig> loggerMap = logContext.getConfiguration().getLoggers();
			Iterator<String> loggerIt = loggerMap.keySet().iterator();
			
			while(loggerIt.hasNext()) {
				String logName = loggerIt.next();
				LoggerConfig loggerConfig = loggerMap.get(logName);
				
				LogInfo logInfoForMap = new LogInfo();
				
				// 서비스 관리 로거인 경우 : 따로 세팅(filter때문)
				if(/*this.serviceLevelMof.equals(logName) && */loggerConfig.getAppenders().containsKey(serviceFilter)) {
					logInfoForMap.setLogName(logName);
					AbstractAppender serviceAppender = (AbstractAppender)logContext.getConfiguration().getAppender(serviceFilter);
					if(serviceAppender.hasFilter()) {
						CompositeFilter comFilters = (CompositeFilter)serviceAppender.getFilter();
						ServiceLogLevelFilter serviceFilter = (ServiceLogLevelFilter)comFilters.getFilters().get(0);
						logInfoForMap.setLogLevel(serviceFilter.getDefaultThreshold().name());
					}
					logInfoForMap.setAddit(loggerConfig.isAdditive()?"Y":"N");
					logInfoForMap.setServerId(serverId);
					logInfoForMap.setAppenderName("서비스 및 사용자 관리용");
					logInfoForMap.setLogPath("");
					logInfoMap.put(logInfoForMap.getLogName(), logInfoForMap);
					continue;
				}
				
				logInfoForMap.setLogName("".equals(logName)?"ROOT":logName);
				
				logInfoForMap.setLogLevel(loggerConfig.getLevel().toString());	
				logInfoForMap.setAddit(loggerConfig.isAdditive()?"Y":"N");
				logInfoForMap.setServerId(serverId);
				
				// 어펜더 정보 : delimiter("|") 사용
				Map<String, Appender> appenderMap = loggerConfig.getAppenders();
				Iterator<String> appenderIt = appenderMap.keySet().iterator();
				String appenders = "";
				String logPaths = "";
				while(appenderIt.hasNext()) {
					Appender appender = appenderMap.get(appenderIt.next());
					String appenderName = appender.getClass().getSimpleName();
					String logPath = "";
					
					if(appenderName.toLowerCase().contains("console")){
						logPath = "SYSTEM_OUT";
						
					} else if(appenderName.toLowerCase().contains("direct")) {
						DirectRollingFileAppender rollingAppender = (DirectRollingFileAppender)appender;
						logPath = rollingAppender.getFilePattern();
						
					} else if(appenderName.toLowerCase().contains("socket")) {
						SocketAppender socketAppender = (SocketAppender)appender;
						logPath = socketAppender.getManager().getContentFormat().get("address")+
								":"+socketAppender.getManager().getContentFormat().get("port");
						
					} else if(appenderName.toLowerCase().contains("rolling")) {
						RollingFileAppender rollingAppender = (RollingFileAppender)appender;
						logPath = rollingAppender.getFilePattern();
						
					} else if(appenderName.toLowerCase().contains("file")) {
						FileAppender fileAppender = (FileAppender)appender;
						logPath = fileAppender.getFileName();
						
					} else {
						logPath = "APPENDER NAME : "+ appender.toString();
					}
					
					appenders+=appenderName+"|";
					logPaths+=logPath+"|";
					
				}
				logInfoForMap.setAppenderName(appenders);
				logInfoForMap.setLogPath(logPaths);
				
				logInfoMap.put(logInfoForMap.getLogName(), logInfoForMap);
			}
		}
		
		// DB에서 정보 최신화
		for(LogInfo logInfo: dbLogInfo) {
			String logName = logInfo.getLogName();
			
			// DB에 정보가 있는 경우 : DB의 정보로 대체
			if(logInfoMap.containsKey(logName)) {
				LogInfo newInfo = logInfoMap.get(logName);
				newInfo.setProps(logInfo.getLogLevel(), logInfo.getAddit());
				logInfoMap.put(logName, newInfo);
				
			// DB에 정보가 없는 경우 : map에 추가
			} /*else {
				logInfoMap.put(logName, logInfo);
			}*/
		}
		
		logInfos.put(serverId, logInfoMap);
	}
	
	public void setLogInfoSync(Map<String, Map<String, LogInfo>> logInfos)  {		this.logInfos = logInfos;	}
	public Map<String, Map<String, LogInfo>> getLogInfoSync() 				{		return logInfos;			}
	public String getServerId() 			 								{		return serverId;			}
	public void setServerId(String serverId) 								{		this.serverId = serverId;	}
	public String getServiceLevelMof() 										{		return serviceLevelMof;		}
	public void setServiceLevelMof(String serviceLevelMof) 					{		this.serviceLevelMof = serviceLevelMof;		}
	public String getServiceFilter() 										{		return serviceFilter;		}
	public void setServiceFilter(String serviceFilter) 						{		this.serviceFilter = serviceFilter;			}
	
	
}
