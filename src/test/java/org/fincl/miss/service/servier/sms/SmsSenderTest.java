package org.fincl.miss.service.servier.sms;

import static org.junit.Assert.*;

import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.junit.Test;




import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations={
		"file:src/main/resources/config/spring/servlet-dispatcher-webmvc.xml",
		"file:src/main/resources/config/spring/context-*.xml"
		})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SmsSenderTest {

	private static final Logger log = LoggerFactory.getLogger(SmsSenderTest.class);
	
	@Autowired
	WebApplicationContext context;
	
		
	@Before
	public void setUp(){
	
	}
	 
	@After
	public void clear(){

	}


	@Test
	public void test() {
		SendType sendType = SendType.SMS_006;
		log.debug(sendType.toString());
	
		/**
		 * SMS자동발송 문구가 생성되는지 확인하고, SMS 테이블에 등록.
		 * (직접 SMS 테이블에 등록하는 방법)
		 * ==================================================
		 * ADMIN_RETURN
		 * format : <<따릉이> 회원확인 문자입니다. 대여소 관리자에게 회원임을 확인바랍니다.
		 * 사용방법 : sms.setMsg(sendType)
		 *          추가적인 파라미터가 없으므로 sendType만 등록.
		 */
		SmsMessageVO sms = getDefaultSmsMessageVO();
		sms.setMsg(sendType);
		
		String str = SmsSender.getMsg(sendType.getCode());
		
		int start = str.lastIndexOf("{");
		assertEquals(start,-1);
		
		/**
		 * SMS 사용자 필드인 auto_send_id가 정상적으로 등록되었는지 확인.
		 * 
		 */
		assertEquals("SMS_006", sms.getAutoSendId());
//		SmsSender.sender(sms);
		
		/**
		 * FARE_PAYMENTS
		 * format : "<따릉이> 초과이용({0}분)에 따른 추가요금({1}원)이 자동결제 되었습니다."
		 * 사용방법 : sms.setMsg(sendType,"30","3,000")
		 *          param[]을 순차적으로 입력한다. 또는 배열을 그대로 인자로 전달해도 무관.
		 */
		sendType = SendType.SMS_004;
		sms = getDefaultSmsMessageVO();
		assertTrue(sms.setMsg(SendType.SMS_004,"30","3,000"));
		str = SmsSender.getMsg(sendType.getCode());
		
		assertEquals(paramCount(str),2);
		/**
		 * param값이 정상적으로 값이 매칭되었는지 확인한다.
		 */
		assertEquals("<따릉이> 초과이용(30분)에 따른 추가요금(3,000원)이 자동결제 되었습니다.",sms.getMsg() );
		/**
		 * SMS API를 통해 발송..
		 */
//		SmsSender.sender(sms);
	}
	
	/**
	 * 테스트용 SMS 전송객체 생성..전달받을 전화번호를 등록한다.
	 * @return
	 */
	private SmsMessageVO getDefaultSmsMessageVO(){
		SmsMessageVO sms = new SmsMessageVO();
		sms.setDestno("01000000001");
		return sms;
	}
	
	private int paramCount(String str){
		int start = str.lastIndexOf("{");
		int result = 0;
		if(start>0){
			int end = str.lastIndexOf("}");
			try{
				String count = str.substring(start+1, end);
				result = Integer.parseInt(count)+1;
			}catch(Exception e){
				result = 0;
			}
		}
		return result;
	}

}
