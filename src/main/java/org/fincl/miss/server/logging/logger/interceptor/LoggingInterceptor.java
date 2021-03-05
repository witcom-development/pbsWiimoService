package org.fincl.miss.server.logging.logger.interceptor;

import java.util.Locale;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.fincl.miss.server.logging.logger.Log;
import org.fincl.miss.server.logging.logger.LogRepository;

import com.dkitec.cfood.core.web.ConfiguredRequestMapping;
import com.dkitec.cfood.core.web.RequestMappingDetector;

public class LoggingInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private RequestMappingDetector mappingDetector;
	@Resource(name="exceptionProps")
	private Properties exceptionProps;
	@Resource(name="errorSource")
	private MessageSource errorSource;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

		Log log = new Log();
		log.setRequestTime(System.currentTimeMillis());
		log.setRequestUri(request.getRequestURI());

		if (handler instanceof HandlerMethod) {
			ConfiguredRequestMapping mapping = mappingDetector.getRequestMapping(
					((HandlerMethod)handler).getMethod());

			if (mapping != null) {
				log.setRequestCategory(mapping.getCategory());
				log.setRequestName(mapping.getName());
			}
		}

		LogRepository.pushLog(log);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView model) {

		String successCode = exceptionProps.getProperty("${success}");

		Log log = LogRepository.getLog();
		log.setErrorCode(successCode);
		log.setErrorMessage(errorSource.getMessage(successCode, null, Locale.getDefault()));
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {

		Log log = LogRepository.popLog();
		log.setResponseTime(System.currentTimeMillis());

		Logger serviceLogger = LoggerFactory.getLogger("service");
		if (serviceLogger.isInfoEnabled()) {
			serviceLogger.info("", log);
		}
	}
}
