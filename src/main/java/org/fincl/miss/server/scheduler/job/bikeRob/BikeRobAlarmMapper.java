package org.fincl.miss.server.scheduler.job.bikeRob;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("BikeRobAlarmMapper")
public interface BikeRobAlarmMapper {

	String getBikeRobSmsTarget();
	List<Map<String, String>> getBikeRobList();
	void addHistAlarm(Map<String, String> map);


}
