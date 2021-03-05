package org.fincl.miss.server.service.metadata;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public interface ServiceRequest {

	/**
	 * 
	 * @return
	 */
	String getClientIp();

	/**
	 * 
	 * @return
	 */
	long getExecuteTime();
}
