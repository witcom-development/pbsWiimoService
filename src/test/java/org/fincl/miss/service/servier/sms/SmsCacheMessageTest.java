package org.fincl.miss.service.servier.sms;

import static org.junit.Assert.*;

import org.fincl.miss.server.scheduler.job.sms.service.SmsMessageService;
import org.fincl.miss.server.sms.SendType;
import org.junit.Test;



import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={
		"file:src/main/resources/config/spring/servlet-dispatcher-webmvc.xml",
		"file:src/main/resources/config/spring/context-*.xml"
		})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SmsCacheMessageTest {

	private static final Logger log = LoggerFactory.getLogger(SmsCacheMessageTest.class);
	
	@Autowired
	WebApplicationContext context;
	
	@Autowired
	SmsMessageService smsMessageService;
	
	@Autowired
	CacheManager cacheManager;
	
	@Before
	public void setUp(){
	
	}
	 
	@After
	public void clear(){

	}


	@Test
	public void test() {
		SendType sendType = SendType.SMS_004;
		log.debug(sendType.toString());
		
		assertNotNull(smsMessageService);
		Cache cache = cacheManager.getCache("smsMessage");
		
		assertNotNull(cache);
		assertEquals(cache.get("SMS_004").get(),sendType.toString());
		
	}
	
	

}
