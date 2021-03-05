package org.fincl.miss.server.sms;

import java.util.List;

import javax.annotation.Resource;

import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;
import org.fincl.miss.server.scheduler.job.sms.service.SmsMessageService;
import org.fincl.miss.server.sms.vo.SmsBodyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;




@Component
public class SmsSender {

	private static SmsMessageService smsMessageService;
	
	@Autowired
	SmsSender(SmsMessageService smsMessageService){
		SmsSender.smsMessageService = smsMessageService;
	}
	
	public static String getMsg(String code){
		return smsMessageService.getSmsBody(code);
	}
	
	/**
	 * 단건 전송
	 * @param msg
	 */
	public static void sender(SmsMessageVO msg){
		System.out.println("SMS 등록");
		int ran = (int)(Math.random() * 100);
		String num = String.valueOf(ran);
		if(num.length() == 1)
		{
			num = "0" + num;
		}
		
		msg.setEtc3(num);

		smsMessageService.insertSmsMessage(msg);
	}
	
	public static void TAPPsender(TAPPMessageVO msg){
		System.out.println("TAPP Message 등록");
		smsMessageService.insertTAPPMessage(msg);
	}
	
	/**
	 * 다건 전송
	 * @param list
	 */
	public static void sender(List<SmsMessageVO> list){
		for(SmsMessageVO msg:list){
			sender(msg);
		}
	}
	
	/**
	 * 자동전송..
	 * 
	 * Sms에 직접 인자를 넣고..sender(sms)를 호출해도 된다.
	 * 
	 */
	public static void sender(SmsMessageVO msg, SendType sendType, String ... args){
		msg.setMsg(sendType, args);
		sender(msg);
	}
	
}
