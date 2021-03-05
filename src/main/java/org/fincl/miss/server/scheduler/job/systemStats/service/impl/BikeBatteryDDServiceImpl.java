package org.fincl.miss.server.scheduler.job.systemStats.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.systemStats.service.BikeBatteryDDService;
import org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper;
import org.fincl.miss.server.scheduler.job.systemStats.vo.BikeBatteryDDVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service(value="bikeBatteryDDService")
public class BikeBatteryDDServiceImpl implements BikeBatteryDDService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BikeSystemStatsMapper bikeSystemStatsMapper;
	
	
	public List getBikeList(){
		return bikeSystemStatsMapper.getBikeList();
	}
	
	@Transactional
	public void insertBikeBatteryDD(BikeBatteryDDVO bikeBatteryDDVo){
		log.debug("bikeBatteryDDVo.getBikeList().size() ==> " + bikeBatteryDDVo.getBikeList().size());
		
		for(int i = 0; i < bikeBatteryDDVo.getBikeList().size(); i++){
			log.debug("bikeBatteryDDVo.i ==> " + i);
			log.debug("bikeBatteryDDVo.getBikeID ==> " + String.valueOf(bikeBatteryDDVo.getBikeList().get(i).get("BIKE_ID")));
			log.debug("bikeBatteryDDVo.getBikeID ==> " + String.valueOf(bikeBatteryDDVo.getREG_YEAR()));
			log.debug("bikeBatteryDDVo.getBikeID ==> " + String.valueOf(bikeBatteryDDVo.getLEAP_YEAR_YN()));
			
			bikeBatteryDDVo.setBIKE_ID(String.valueOf(bikeBatteryDDVo.getBikeList().get(i).get("BIKE_ID")));
			bikeSystemStatsMapper.insertBikeBatteryDD(bikeBatteryDDVo);
		}
		log.debug("bikeBatteryDDVo.getBikeID ==> 나감~!!");
	}
	
	public List getBikeBatteryInfo(){
		return bikeSystemStatsMapper.getBikeBatteryInfo();
	}

	@Transactional
	public void updateBikeBatteryInfo(List<HashMap<String, String>> bikeBatteryInfo){
		Map<String, String> paramMap = new HashMap<String, String>();
		
		for(int i = 0; i < bikeBatteryInfo.size(); i++){
			paramMap.put("BIKE_ID", bikeBatteryInfo.get(i).get("BIKE_ID"));
			paramMap.put("BIKE_BATTERY_STUS_CD", bikeBatteryInfo.get(i).get("BIKE_BATTERY_STUS_CD"));
			paramMap.put("CUR_YEAR", bikeBatteryInfo.get(i).get("CUR_YEAR"));
			paramMap.put("COMUMN_NAME", bikeBatteryInfo.get(i).get("COMUMN_NAME"));
			bikeSystemStatsMapper.updateBikeBatteryInfo(paramMap);
		}
	}
}
