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
import org.fincl.miss.server.scheduler.job.recommend.service.RecommendPolicyMapper;
import org.fincl.miss.server.scheduler.job.recommend.service.RecommendPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecommendPolicyScheduler {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name="recommendPolicyService")
	private RecommendPolicyService recommendPolicyService;
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="recommendTimePolicyProc")
	public void recommendTimePolicyProc(){
		log.debug("******************************추천반납대여소 정오 대여소 별 테이블(TB_SYS_RENT_MAX_CNT_HIST) Row 생성 시작************************");
		
		recommendPolicyService.insertRecommendTimePolicyInfo();
		
		log.debug("******************************추천반납대여소 정오 대여소 별 테이블(TB_SYS_RENT_MAX_CNT_HIST) Row 생성 완료************************");
	}
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="recommendWeeklyPolicyProc")
	public void recommendWeeklyPolicyProc(){
		log.debug("******************************추천반납대여소 주간정책 테이블(TB_SYS_RECOMMEND_STATION_POLICY) Row 생성 시작************************");
		
		recommendPolicyService.insertRecommendWeeklyPolicyInfo();
		
		log.debug("******************************추천반납대여소 주간정책 테이블(TB_SYS_RECOMMEND_STATION_POLICY) Row 생성 완료************************");
	}

}
