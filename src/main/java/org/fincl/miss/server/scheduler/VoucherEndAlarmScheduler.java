package org.fincl.miss.server.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.voucherEndChk.service.VoucherEndChkMapper;
import org.fincl.miss.server.scheduler.job.voucherEndChk.vo.VoucherEndChkVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VoucherEndAlarmScheduler {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private VoucherEndChkMapper voucherEndChkMapper;
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="voucherEndAlarm")
	public void voucherEndAlarm(){
		logger.debug("******************************정기권종료 이전안내 SMS 배치************************");
		List<SmsMessageVO> smsList = new ArrayList<SmsMessageVO>();
		List<VoucherEndChkVO> targetList = voucherEndChkMapper.getVoucherEndUserInfo();
		java.util.Map<String, String> map = new java.util.HashMap<String, String>();
		
		for(VoucherEndChkVO target : targetList){
			SmsMessageVO smsVo = new SmsMessageVO();
			smsVo.setDestno(target.getUsr_mpn_no());
			smsVo.setAutoSendId("SMS_019");
			smsVo.setMsg(SendType.SMS_019, "");
			smsList.add(smsVo);
		}
		SmsSender.sender(smsList);
     	logger.debug("******************************정기권종료 이전안내 SMS 전송***************************************");
	}

}
