package org.fincl.miss.server.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService;
import org.fincl.miss.server.scheduler.job.period.service.PeriodTenMinuteErrorMapper;
import org.fincl.miss.server.scheduler.job.period.service.PeriodTenMinuteErrorService;
import org.fincl.miss.server.scheduler.job.period.vo.PeriodTenMinuteErrorVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PeriodTenMinuteErrorAlarmScheduler {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private PeriodTenMinuteErrorMapper periodTenMinuteErrorMapper;
	
	@Resource(name="periodTenMinuteErrorService")
	private PeriodTenMinuteErrorService periodTenMinuteErrorService;
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="periodTenMinuteError")
	public void periodTenMinuteError(){
		log.debug("******************************주기적인 상태보고 10분 ERROR 배치 시작************************");
		List<PeriodTenMinuteErrorVO> targetList = periodTenMinuteErrorService.getPeriodTenMinuteErrorList();
		
		
		for(PeriodTenMinuteErrorVO target : targetList){
			log.debug("##### 주기적인 상태보고 10분 ERROR ##### ==> " + target.toString());
			periodTenMinuteErrorService.insertPeriodTenMinuteErrorList(target);
		}

		log.debug("******************************주기적인 상태보고 10분 ERROR 배치 종료************************");
	}

}
