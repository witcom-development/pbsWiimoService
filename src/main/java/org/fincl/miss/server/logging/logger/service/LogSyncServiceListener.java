package org.fincl.miss.server.logging.logger.service;

import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.fincl.miss.core.logging.LogInfo;
import org.fincl.miss.core.logging.LogLevel;
import org.fincl.miss.server.logging.logger.ServiceLogLevelFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;

/**
 * hazelCast 로그정보 맵의 변경 시 동작하는 리스너
 */
public class LogSyncServiceListener implements EntryListener<String, Map<String, LogInfo>> {
	
	@Autowired
	ApplicationContext context;
	
	@Override
	public void entryAdded(EntryEvent<String, Map<String, LogInfo>> event) {
		LogSyncService service = context.getBean("logSyncService", LogSyncService.class);
		String serverId = service.getServerId();
		if(event.getKey().equals(serverId) && event.getValue()!=null) applyLogConfig(event.getValue());
	}

	@Override
	public void entryRemoved(EntryEvent<String, Map<String, LogInfo>> event) { /* Do Nothing */ }

	@Override
	public void entryUpdated(EntryEvent<String, Map<String, LogInfo>> event) {
		LogSyncService service = context.getBean("logSyncService", LogSyncService.class);
		String serverId = service.getServerId();
		if(event.getKey().equals(serverId) && event.getValue()!=null) applyLogConfig(event.getValue());
	}

	@Override
	public void entryEvicted(EntryEvent<String, Map<String, LogInfo>> event) { /* Do Nothing */ }

	@Override
	public void mapEvicted(MapEvent event) { /* Do Nothing */ }

	@Override
	public void mapCleared(MapEvent event) { /* Do Nothing */ }

	/**
	 * 로그정보가 변경되었을 때 변경된 정보를 loggerContext에 적용함
	 * @param logInfos : 변경된 log정보 map
	 */
	public void applyLogConfig(Map<String, LogInfo> logInfos) {
		LoggerContext logContext = (LoggerContext)LogManager.getContext(false);
		Map<String, LoggerConfig> configMap = logContext.getConfiguration().getLoggers();
		Iterator<String> it = logInfos.keySet().iterator();
		
		while(it.hasNext()) {
			String logName = it.next();
			
			if(configMap.containsKey(logName) || "ROOT".equals(logName)) {
				LogInfo vo = logInfos.get(logName);
				LoggerConfig logger = configMap.get(logName);
				if("ROOT".equals(logName)) logger = configMap.get("");
				
				logger.setAdditive("Y".equals(vo.getAddit())?true:false);
				String logLevel = vo.getLogLevel();
				
				// 서비스 로그 레벨의 경우 필터의 defaultThreshold레벨을 변경해줌
				if(logger.getAppenders().containsKey(context.getBean("logSyncService", LogSyncService.class).getServiceFilter())
						/*logName.equals(context.getBean("logSyncService", LogSyncService.class).getServiceLevelMof())*/) {
					AbstractAppender serviceAppender = 
							(AbstractAppender)logContext.getConfiguration().getAppender(context.getBean("logSyncService", LogSyncService.class).getServiceFilter());
					if(serviceAppender!=null && serviceAppender.hasFilter()) {
						CompositeFilter comFilters = (CompositeFilter)serviceAppender.getFilter();
						ServiceLogLevelFilter serviceFilter = (ServiceLogLevelFilter)comFilters.getFilters().get(0);
						serviceFilter.setDefaultThreshold(
								Level.getLevel(logLevel)==null?Level.getLevel(LogLevel.getName(logLevel)):Level.getLevel(logLevel));
					}
					
				} else {
					if(logLevel.equals(LogLevel.TRACE.toString())) 		logger.setLevel(Level.TRACE);
					else if(logLevel.equals(LogLevel.DEBUG.toString())) logger.setLevel(Level.DEBUG);
					else if(logLevel.equals(LogLevel.INFO.toString()))  logger.setLevel(Level.INFO);
					else if(logLevel.equals(LogLevel.WARN.toString()))  logger.setLevel(Level.WARN);
					else if(logLevel.equals(LogLevel.ERROR.toString())) logger.setLevel(Level.ERROR);
					else if(logLevel.equals(LogLevel.FATAL.toString())) logger.setLevel(Level.FATAL);	
				}
			}
		}
		
		logContext.updateLoggers();
	}
	
}

