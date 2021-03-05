package org.fincl.miss.server.scheduler.job.sms.service;

import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;

public interface SmsMessageService
{

	public String getSmsBody(String code) ;

	public int insertSmsMessage(SmsMessageVO msg);
	
	public int insertTAPPMessage(TAPPMessageVO msg);

}
