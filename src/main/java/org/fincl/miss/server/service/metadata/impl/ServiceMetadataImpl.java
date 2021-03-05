package org.fincl.miss.server.service.metadata.impl;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.fincl.miss.core.logging.LogLevel;
import org.fincl.miss.core.service.Service;
import org.fincl.miss.core.service.ServiceTime;
import org.fincl.miss.server.service.metadata.ServiceMetadata;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class ServiceMetadataImpl implements ServiceMetadata, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4551115230283605234L;

	/**
	 * 
	 */
	private final Service service;

	/**
	 * 
	 */
	private final transient Class<?> handlerClass;

	/**
	 * 
	 */
	private final transient Method handlerMethod;

	/**
	 * 
	 */
	private final transient Class<?> paramClass;

	/**
	 * 
	 */
	private final transient Class<?> returnClass;

	/**
	 * 
	 * @param service
	 * @param handlerMethod
	 */
	ServiceMetadataImpl(final Service service, final Method handlerMethod) {

		System.out.println(service);
		System.out.println(handlerMethod);
		this.service = service;
		this.handlerClass = handlerMethod.getDeclaringClass();
		this.handlerMethod = handlerMethod;
		this.paramClass = handlerMethod.getParameterTypes()[0];
		this.returnClass = handlerMethod.getReturnType();
	}

	/**
	 * 
	 */
	@Override
	public String getServiceId() {

		return service.getServiceId();
	}

	/**
	 * 
	 */
	@Override
	public String getServiceName() {

		return service.getServiceName();
	}

	/**
	 * 
	 */
	@Override
	public String getServiceGroupName() {

		return service.getServiceGroupName();
	}

	/**
	 * 
	 */
	@Override
	public String getDescription() {

		return service.getDescription();
	}

	/**
	 * 
	 */
	@Override
	public String getParamDescription() {

		return service.getParamDescription();
	}

	/**
	 * 
	 */
	@Override
	public String getReturnDescription() {

		return service.getReturnDescription();
	}

	/**
	 * 
	 */
	@Override
	public String getExample() {

		return service.getExample();
	}

	/**
	 * 
	 */
	@Override
	public boolean isUsable() {

		return service.isUsable();
	}

	/**
	 * 
	 */
	@Override
	public ServiceTime[] getServiceTimes() {

		return service.getServiceTimes();
	}

	/**
	 * 
	 */
	@Override
	public LogLevel getLogLevel() {

		return null;
	}

	/**
	 * 
	 */
	@Override
	public String getHandlerMethodName() {

		return service.getHandlerMethodName();
	}

	/**
	 * 
	 */
	@Override
	public String getUserId() {

		return service.getUserId();
	}

	/**
	 * 
	 */
	@Override
	public Class<?> getHandlerClass() {

		return handlerClass;
	}

	/**
	 * 
	 */
	@Override
	public Method getHandlerMethod() {

		return handlerMethod;
	}

	/**
	 * 
	 */
	@Override
	public Class<?> getParamClass() {

		return paramClass;
	}

	/**
	 * 
	 */
	@Override
	public Class<?> getReturnClass() {

		return returnClass;
	}
}
