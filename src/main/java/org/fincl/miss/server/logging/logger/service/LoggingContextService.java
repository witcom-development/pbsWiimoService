package org.fincl.miss.server.logging.logger.service;

/**
 * 서비스 invoke시 서버의 식별자와 사용자의 식별자를 ThreadContext에 추가한다
 */
public interface LoggingContextService {

	/**
	 * 식별자를 ThreadContext에 set
	 * @param serviceId, clientId
	 */
	public void setContext(String serviceId, String clientId);
	
	
}
