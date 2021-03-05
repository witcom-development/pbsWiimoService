package org.fincl.miss.server.service.metadata;

import java.lang.reflect.Method;

import org.fincl.miss.core.service.Service;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public interface ServiceMetadata extends Service {

	/**
	 * 
	 * @return
	 */
	Class<?> getHandlerClass();

	/**
	 * 
	 * @return
	 */
	Method getHandlerMethod();

	/**
	 * 
	 * @return
	 */
	Class<?> getParamClass();

	/**
	 * 
	 * @return
	 */
	Class<?> getReturnClass();
}
