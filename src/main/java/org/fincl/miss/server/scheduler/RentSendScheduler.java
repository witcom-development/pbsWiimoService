/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler
 * @파일명          : AutoOverFeePayScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.pushProc.service.ReturnAlarmMapper;
//import org.fincl.miss.server.scheduler.job.sms.PartitionVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.server.sms.vo.SendSMSVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
/**
 * @파일명          : AutoOverFeePayScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Component
public class RentSendScheduler  {
    
	@Autowired
	private ReturnAlarmMapper returnAlramMapper;
	//private RestTemplate restTemplate;
	//private RestTemplate restVrerifyTemplate;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@SuppressWarnings("unchecked")
	public @ResponseBody void rentSendPushProc() 
	{
	//@ClusterSynchronized( jobToken="rentSendPushProc")
	//public void rentSendPushProc() throws Exception 
	//{
		/*
       logger.debug("******************************[단말 문자 전송 start] ******************************");
	   TrustManager[] trustAllCerts = new TrustManager[] {
	       new X509TrustManager() {
	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	            return null;
	          }
	          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
	          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

	       }
	    };
	    SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	    // Create all-trusting host name verifier
	    HostnameVerifier allHostsValid = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	          return true;
	        }
	    };
	    // Install the all-trusting host verifier
	    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restVrerifyTemplate = new RestTemplate();
		restVrerifyTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		*/
		
		
		int result = 0;
		List<HashMap<String, String>> msgTargetList = returnAlramMapper.getBikeRentSend();
        logger.debug("******************************대여 전송 스타트*["+msgTargetList.size()+"]*******************************************");
        if(msgTargetList != null && msgTargetList.size() > 0) 
        {
        	logger.debug("******************************전송 start ********************************************");
        	
            for(HashMap<String, String> target :msgTargetList) 
            {
            	SmsMessageVO smsVo = null;
            	smsVo = new SmsMessageVO();
            	
            	if(target.get("DEVICE_MPN_NO") != null && !target.get("DEVICE_MPN_NO").equals(""))
 				{
 				
     				String destno = String.valueOf(target.get("DEVICE_MPN_NO"));
     				if(destno != null && !destno.equals(""))
     				{
     					
     					SendSMSVo sms2 = new SendSMSVo();
     					sms2.setCmd_id("3C");
     					sms2.setState("01");
     					sms2.setDev_state("02");
     					sms2.setDev_type("00");
     					sms2.setUser_type("01");
     					sms2.setUser_seq(String.valueOf(target.get("USR_SEQ")));
     					String Message = sms2.getCmd_id() + sms2.getState() + sms2.getDev_state() + sms2.getDev_type()
     							+ sms2.getUser_type() + sms2.getUser_seq();
     					logger.debug("[Message " + Message + "]");
     					smsVo.setDestno(destno);
     					smsVo.setMsg(Message);
     					SmsSender.sender(smsVo);
     				}
 				}
            	
            	result = returnAlramMapper.setBikeRentSendComplete(String.valueOf(target.get("RENT_SEQ")));
            }
            logger.debug("******************************sms 전송 complete*************************************");
        }
	}
	
}
