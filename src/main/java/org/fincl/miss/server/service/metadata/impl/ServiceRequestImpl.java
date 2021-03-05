package org.fincl.miss.server.service.metadata.impl;

import org.fincl.miss.server.service.metadata.ServiceRequest;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class ServiceRequestImpl implements ServiceRequest {

	/**
	 * 
	 */
	private transient final String clientIp;

	/**
	 * 
	 */
	private transient final long executeTime;

	/**
	 * 
	 * @param clientIp
	 */
	public ServiceRequestImpl(final String clientIp) {

		this(clientIp, System.currentTimeMillis());
	}

	/**
	 * 
	 * @param clientIp
	 * @param executeTime
	 */
	public ServiceRequestImpl(final String clientIp, final long executeTime) {

		if (clientIp == null) {
			throw new IllegalArgumentException("ClientIp null");
		}

		this.clientIp = clientIp;
		this.executeTime = executeTime;
	}

	/**
	 * 
	 */
	@Override
	public String getClientIp() {

		return clientIp;
	}

	/**
	 * 
	 */
	@Override
	public long getExecuteTime() {

		return executeTime;
	}
}
