package org.fincl.miss.server.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.bikeRob.BikeRobAlarmMapper;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BikeRobAlarmScheduler {
    
	@Autowired
	private BikeRobAlarmMapper bikeRobAlarmMapper;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="bikeRobSmsAlarm")
	public void bikeRobSmsAlarm(){
		logger.debug("******************************도난 알람sms 배치************************");
		List<SmsMessageVO> smsTarget = new ArrayList<SmsMessageVO>();
		List<Map<String, String>> targetList = bikeRobAlarmMapper.getBikeRobList();
		String[] smsMap = bikeRobAlarmMapper.getBikeRobSmsTarget().split(",");
        StringBuffer bikes = new StringBuffer();
		for(Map<String,String> map : targetList) {
			bikes.append(map.get("BIKE_NO")).append(",");
			
		}
		logger.debug("******************************"+bikes.toString()+"**********************");
		SmsMessageVO smsVo = null;
		if(targetList.size()> 0) {
			if(smsMap.length > 0) {
				for(int i=0 ;i <smsMap.length ; i++){
					smsVo = new SmsMessageVO();
					smsVo.setDestno(smsMap[i]);
					smsVo.setMsg(SendType.SMS_014, bikes.toString());
					smsTarget.add(smsVo);
				}
			}
			SmsSender.sender(smsTarget);
			for(Map<String,String> map : targetList) {
				bikeRobAlarmMapper.addHistAlarm(map);
				
			}
		}
	}
	
}
