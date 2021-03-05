package org.fincl.miss.server.logging.logger.service;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.fincl.miss.core.service.Service;
import org.fincl.miss.server.logging.logger.ServiceLogLevelFilter;
import org.springframework.beans.factory.annotation.Value;

/**
 * 서비스 hazelCast map이 변경할 경우 ServiceEntryListener에 의해서 서비스별 로그 레벨을 조정하게 된다(어펜더 필터)
 */
public class ServiceLogLevelUpdater {
	
	// 서비스 레벨 관리 Logger 이름
	private String serviceLevelMof = "org.fincl.miss";
	
	// 서비스 레벨 관리 필터가 적용된 어펜더 이름
	private String serviceFilterName = "serviceMapping";
	
	// 서비스 서버의 식별자
	@Value("#{serverProps.serverId}")
	private String serverId;
	
	private ServiceLogLevelFilter serviceFilter = null;
	private AbstractAppender serviceAppender = null;
	private LoggerContext logContext = null;

	
	/**
	 * 서비스 로그 레벨을 조정하는 필터를 수정 및 적용한다
	 * @param Service : 변경 또는 추가된 서비스의 정보(서비스아이디, 레벨)
	 */
	public void modifyServiceLogLevelFilter(Service service) {
		if(serviceFilter!=null) {
			serviceFilter.addLogLevel(service.getServiceId(), service.getLogLevel()!=null?Level.getLevel(service.getLogLevel().name()):null);
			
		} else {
			KeyValuePair[] pairs = {new KeyValuePair(service.getServiceId(), service.getLogLevel()!=null?service.getLogLevel().name():null)};
			serviceFilter = ServiceLogLevelFilter.createFilter("server.service", pairs, null, Result.ACCEPT, Result.DENY);
			serviceAppender.addFilter(serviceFilter);
		}
		logContext.updateLoggers();
	}
	
	
	/**
	 * 서비스가 삭제된 경우 필터에서 해당 조건을 삭제한다.
	 * @param service
	 */
	public void removeFilterValues(Service service) {
		if(serviceFilter!=null) {
			serviceFilter.removeLevelMapMember(service.getServiceId());
		}
		logContext.updateLoggers();
	}
	
	
	/**
	 * @PostConstruct
	 * 로그 컨텍스트 주소와 필터 주소값을 미리 저장함
	 */
	@PostConstruct
	public void init() {
		logContext = (LoggerContext)LogManager.getContext(false);
		
		if(logContext.getConfiguration().getAppender(serviceFilterName)!=null) {
			serviceAppender = (AbstractAppender)logContext.getConfiguration().getAppender(serviceFilterName);
			if(serviceAppender.hasFilter()) {
				CompositeFilter comFilters = (CompositeFilter)serviceAppender.getFilter();
				serviceFilter = (ServiceLogLevelFilter)comFilters.getFilters().get(0);
			} 
		}
	}

	public String getServiceLevelMof() 						{	return serviceLevelMof;					 }
	public void setServiceLevelMof(String serviceLevelMof) 	{	this.serviceLevelMof = serviceLevelMof;	 }
	public String getServerId() 							{	return serverId;						 }
	public void setServerId(String serverId) 				{	this.serverId = serverId;				 }
	public String getServiceFilterName() 							{	return serviceFilterName;						 }
	public void setServiceFilterName(String serviceFilterName) 		{	this.serviceFilterName = serviceFilterName;		 }

	
}
