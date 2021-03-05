package org.fincl.miss.server.scheduler.job.sms.service;

import java.util.List;

import org.fincl.miss.server.scheduler.job.sms.PartitionVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.scheduler.job.sms.TAPPMessageVO;
import org.fincl.miss.server.sms.vo.SmsBodyVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("smsMessageMapper")
public interface SmsMessageMapper {
	public List<SmsMessageVO> getFailSmsList();
	public void updateAutoSendResult(SmsMessageVO smsMessageVO);
	public int insertSmsMessage(SmsMessageVO smsMessageVO);
	public List<SmsBodyVO> getSmsMessageList();
	public SmsBodyVO getSmsBody(String code);
	public List<PartitionVO> getDropPartitionLit();
	public List<PartitionVO> getCreateDailyPartitionList();
	public List<PartitionVO> getCreateDailyPartitionListWithToDays();
	public List<PartitionVO> getCreateMonthPartitionList();
	public List<PartitionVO> getCreateMonthPartitionListWithToDays();
	public int execDropPartition(PartitionVO info);
	public int execCreatePartition(PartitionVO info);
	public int execCreatePartitionWithToDays(PartitionVO info);
	public int insertTAPPMessage(TAPPMessageVO smsMessageVO);
}
