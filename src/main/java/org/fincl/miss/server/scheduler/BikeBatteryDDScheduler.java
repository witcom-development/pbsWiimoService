package org.fincl.miss.server.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.systemStats.service.BikeBatteryDDService;
import org.fincl.miss.server.scheduler.job.systemStats.vo.BikeBatteryDDVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BikeBatteryDDScheduler {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	
	@Resource(name="bikeBatteryDDService")
	private BikeBatteryDDService bikeBatteryDDService;
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="bikeBatteryDDProc")
	public void bikeBatteryDDProc(){
		log.debug("자전거 별 배터리 통계정보 Batch 시작~!!");
		
		String curTime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String year = curTime.substring(0, 4);
		String month = curTime.substring(4, 6);
		String day = curTime.substring(6, 8);
		
		log.debug("Sysdate => " + year + ", " + month + ", " + day);
		
		// 1월 1일이면 윤년인지 확인
		boolean isYunYear = false;
		
		List<HashMap<String, String>> bikeBatteryInfo = new ArrayList<HashMap<String, String>>();
		List bikeBatteryList = new ArrayList();
		
		BikeBatteryDDVO bikeBatteryDDVo = new BikeBatteryDDVO();
		bikeBatteryDDVo.setBikeList(bikeBatteryDDService.getBikeList());
		bikeBatteryDDVo.setCOLUMN_NAME("DATE_" + month + day);
		
		if(Integer.parseInt(month) == 1 && Integer.parseInt(day) == 1){
			if(isYunYearYn(Integer.parseInt(year))){	// 윤년이닷~!!
				bikeBatteryDDVo.setLEAP_YEAR_YN("Y");
				log.debug("##### 1월 1일이고 윤년이다~!! #####");
			}else{
				bikeBatteryDDVo.setLEAP_YEAR_YN("N");
				log.debug("##### 1월 1일이고 윤년이 아니다~!! #####");
			}
			
			bikeBatteryDDVo.setREG_YEAR(String.valueOf(year));
			
			bikeBatteryDDService.insertBikeBatteryDD(bikeBatteryDDVo);
			
			log.debug("##### 1월 1일이므로 자전거 배터리 정보 삽입작업 완료~!! #####");
		}
		
		log.debug("##### 하루에 한번있는 배터리정보 Update를 시작해볼까?! #####");
		
		// 자전거 배터리 정보를 추출
		bikeBatteryInfo = bikeBatteryDDService.getBikeBatteryInfo();
		
		// 자전거 배터리 정보를 UPDATE 한다. 실시~!!
		bikeBatteryDDService.updateBikeBatteryInfo(bikeBatteryInfo);
		
		log.debug("##### 하루에 한번있는 배터리정보 Update 완료!! #####");
	}

	/**
	 * @param year
	 */
	private boolean isYunYearYn(int year) {
		boolean isYunYear;
		
		if((0 == (year % 4)) && 0 != (year % 100) || 0 == (year % 400)){
			isYunYear = true;
		}else{
			isYunYear = false;
		}
		
		return isYunYear;
	}

}
