package org.fincl.miss.server.logging.logger.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("logSync/*")
public class LogSyncControllerTest {

	private static final Logger rootLogger = (Logger)LogManager.getRootLogger();
	private static final Logger tracing = (Logger)LogManager.getLogger("org.fincl.miss");
	
	private static final org.slf4j.Logger serviceSlf = LoggerFactory.getLogger("org.fincl.miss");
	
	@RequestMapping(value="logTest1", method=RequestMethod.GET)
	public @ResponseBody String logTest1(){
		//ThreadContext.put("METHOD_NM", "logTest1");
		ThreadContext.put("server.client","testClient1");
		System.out.println(ThreadContext.getContext());
		
		System.out.println("root trace!!!!!");
		rootLogger.trace("root log trace");
		System.out.println("root trace!!!!!");
		rootLogger.debug("root log debug");
		rootLogger.info("root log info");
		rootLogger.warn("root log warn");
		rootLogger.error("root log error");
		
		tracing.trace("tracing log trace");
		tracing.debug("tracing log debug");
		tracing.info("tracing log info");
		tracing.warn("tracing log warn");
		tracing.error("tracing log error");
	
		serviceSlf.info("sjf log info");
		
		return "logTest1";
	}
	
	@RequestMapping(value="logTest2", method=RequestMethod.GET)
	public @ResponseBody String logTest2(){
		ThreadContext.put("server.service", "FEPO00026827");
		ThreadContext.put("server.client", "testClient2");
		System.out.println(ThreadContext.getContext());
		
		rootLogger.trace("root log trace");
		rootLogger.debug("root log debug");
		rootLogger.info("root log info");
		rootLogger.warn("root log warn");
		rootLogger.error("root log error");
		
		tracing.trace("tracing log trace");
		tracing.debug("tracing log debug");
		tracing.info("tracing log info");
		tracing.warn("tracing log warn");
		tracing.error("tracing log error");
		
		serviceSlf.info("sjf log info");
		
		return "logTest2";
	}
	
	@RequestMapping(value="logTest3", method=RequestMethod.GET)
	public @ResponseBody String logTest3(){
		ThreadContext.put("server.service", "logTest3");
		System.out.println(ThreadContext.getContext());
		
		rootLogger.trace("root log trace");
		rootLogger.debug("root log debug");
		rootLogger.info("root log info");
		rootLogger.warn("root log warn");
		rootLogger.error("root log error");
		
		tracing.trace("tracing log trace");
		tracing.debug("tracing log debug");
		tracing.info("tracing log info");
		tracing.warn("tracing log warn");
		tracing.error("tracing log error");
		
		serviceSlf.info("sjf log info");
		
		return "logTest3";
	}
	
	@RequestMapping(value="logTest4", method=RequestMethod.GET)
	public @ResponseBody String logTest4(){
		ThreadContext.put("server.service", "logTest4");
		System.out.println(ThreadContext.getContext());
		
		rootLogger.trace("root log trace");
		rootLogger.debug("root log debug");
		rootLogger.info("root log info");
		rootLogger.warn("root log warn");
		rootLogger.error("root log error");
		
		tracing.trace("tracing log trace");
		tracing.debug("tracing log debug");
		tracing.info("tracing log info");
		tracing.warn("tracing log warn");
		tracing.error("tracing log error");
		
		serviceSlf.info("sjf log info");
		
		return "logTest4";
	}
}
