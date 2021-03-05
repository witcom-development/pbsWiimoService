package org.fincl.miss.server.scheduler.job.systemStats.service;

import java.util.HashMap;
import java.util.List;

import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.server.scheduler.job.period.vo.PeriodTenMinuteErrorVO;
import org.fincl.miss.server.scheduler.job.systemStats.vo.BikeBatteryDDVO;


public interface BikeBatteryDDService {
	
	public List getBikeList();
	
	public void insertBikeBatteryDD(BikeBatteryDDVO bikeBatteryDDVo);
	
	public List getBikeBatteryInfo();
	
	public void updateBikeBatteryInfo(List<HashMap<String, String>> bikeBatteryInfo);

}
