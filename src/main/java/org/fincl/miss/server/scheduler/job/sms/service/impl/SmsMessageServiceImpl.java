package org.fincl.miss.server.scheduler.job.sms.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;
import org.fincl.miss.server.scheduler.job.sms.service.SmsMessageMapper;
import org.fincl.miss.server.scheduler.job.sms.service.SmsMessageService;
import org.fincl.miss.server.sms.vo.SmsBodyVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service("smsMessageService")
public class SmsMessageServiceImpl implements SmsMessageService {

	@Autowired
	private SmsMessageMapper smsMessageMapper;
	
	protected static Logger log = LoggerFactory.getLogger(SmsMessageServiceImpl.class);

	@Autowired
	CacheManager cacheManager;
	
	@PostConstruct
	public void init(){
	//	System.out.println("=====init smsMessage========");
		List<SmsBodyVO> list =  smsMessageMapper.getSmsMessageList();
		for(SmsBodyVO vo: list){
			if(vo.getAutoSendID() != null && vo.getOrignlMsg() != null){
				cacheManager.getCache("smsMessage").put(vo.getAutoSendID(), vo.getOrignlMsg());
			}
		}
	}
	
	@Override
	//@Cacheable(value="smsMessage" , key="#code", unless = "#result == null")
	public String getSmsBody(String code) {
		log.debug("======DB call start======");
		String result = null;
		
		SmsBodyVO smsVo = smsMessageMapper.getSmsBody(code);
		if(smsVo != null){
			result = smsVo.getOrignlMsg();
		}
		log.debug("======DB call start======");
		
		return result;
	}

	@Override
	public int insertSmsMessage(SmsMessageVO smsMessageVO) {
		System.out.println("SMS 등록..."+ smsMessageVO.toString());
		
		return smsMessageMapper.insertSmsMessage(smsMessageVO);

	}
	
	@Override
	public int insertTAPPMessage(TAPPMessageVO smsMessageVO) {
		System.out.println("SMS 등록..."+ smsMessageVO.toString());
		
		return smsMessageMapper.insertTAPPMessage(smsMessageVO);

	}

}
