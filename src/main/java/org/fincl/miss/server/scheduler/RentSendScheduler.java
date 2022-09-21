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
		int result = 0;
		List<HashMap<String, String>> msgTargetList = returnAlramMapper.getBikeRentSend();
        logger.debug("****************************** 급방전 자전거 문자 전송 스타트*["+msgTargetList.size()+"]*******************************************");
        if(msgTargetList != null && msgTargetList.size() > 0) 
        {
        	logger.debug("******************************전송 start ********************************************");
        	
            for(HashMap<String, String> target :msgTargetList) 
            {
            	if(target != null && target.get("BIKE_NO") != null)
            	{
            		String  bikeNo = target.get("BIKE_NO");
            		SmsMessageVO smsVo = new SmsMessageVO();
		 			smsVo.setDestno("01094184422");
		 			
		 			smsVo.setMsg("<위고> " + bikeNo +" 킥보드가 가 급방전 의심 대상입니다. 확인 부탁드리겠습니다.");
		 			SmsSender.sender(smsVo);
		 			result = returnAlramMapper.setBikeRentSendComplete(target.get("BIKE_NO"));
 				}
            }
            logger.debug("******************************sms 전송 complete*************************************");
        }
	}
	
}
