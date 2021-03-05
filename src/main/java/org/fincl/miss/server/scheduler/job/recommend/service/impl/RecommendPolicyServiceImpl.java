/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl
 * @파일명          : AutoOverFeePayServiceImpl.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.recommend.service.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.recommend.service.RecommendPolicyMapper;
import org.fincl.miss.server.scheduler.job.recommend.service.RecommendPolicyService;
import org.fincl.miss.server.scheduler.job.recommend.service.vo.RecommendWeeklyVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service(value="recommendPolicyService")
public class RecommendPolicyServiceImpl implements RecommendPolicyService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private RecommendPolicyMapper recommendPolicyMapper;
	
	@Override
	@Transactional(readOnly=true)
	public void insertRecommendTimePolicyInfo(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = new Date();
		
//		String dateToday = "20170308";
		List<Map<String, String>> stationList = new ArrayList<Map<String,String>>();
		stationList = recommendPolicyMapper.getStationInfo();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("REG_YMD", String.valueOf(sdf.format(today)));
//		paramMap.put("REG_YMD", dateToday);
		paramMap.put("REG_DTTM", String.valueOf(_sdf.format(today)));
		
		if(stationList != null){
			for(Map<String, String> map : stationList){
				paramMap.put("STATION_ID", String.valueOf(map.get("STATION_ID")));
				
				recommendPolicyMapper.insertRecommendTimePolicyInfo(paramMap);
			}
		}
	}
	
	@Override
	@Transactional(readOnly=true)
	public void insertRecommendWeeklyPolicyInfo(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = new Date();
		
//		recommendPolicyMapper.insertRecommendWeeklyPolicyInfo();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("REG_YMD", String.valueOf(sdf.format(today)));
		paramMap.put("REG_DTTM", String.valueOf(_sdf.format(today)));
		recommendPolicyMapper.insertRecommendWeeklyPolicyInfo(paramMap);
	}
	
}
