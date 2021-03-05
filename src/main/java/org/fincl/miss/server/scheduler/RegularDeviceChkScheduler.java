/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler
 * @파일명          : RegularDeviceChkScheduler.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService;
import org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce;
import org.springframework.stereotype.Component;

/**
 * @파일명          : RegularDeviceChkScheduler.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */
@Component
public class RegularDeviceChkScheduler {
	
	@Resource(name="regularDeviceChkService")
	private RegularDeviceChkSerivce regularDeviceChkService;
	@Resource(name="bikeBatteryErrorService")
	private BikeBatteryErrorService bikeBatteryErrorService;
	/**
	 * 장애처리 정기점검 자전거
	 * */
	@ClusterSynchronized( jobToken="BikeRegularChkProc")
	public void BikeRegularChkProc() {
		Map<String,String> pMap = new HashMap<String,String>();
		List<String> bikeList = regularDeviceChkService.getTargetDeviceList("DEV_001");
		pMap.put("clsCd"    ,  "ERB_001");
		pMap.put("devType"  ,  "DEV_001");
		int result = 0;
		for(String bike : bikeList){
			pMap.put("equipmentId", bike);
			String chkResult = bikeBatteryErrorService.chkExistMTCFaultInfo(pMap);
			if(!chkResult.equals("")) {
				//Y
				pMap.put("errorType", "old");
				pMap.put("faultSeq", chkResult);
				bikeBatteryErrorService.setBikeErrorProc(pMap);
			} else {
				//N
				pMap.put("errorType", "new");
				result = bikeBatteryErrorService.addNewBikeErrorProc(pMap);
				pMap.put("faultSeq", String.valueOf(result));
				result = bikeBatteryErrorService.setBikeErrorProc(pMap);
			}
			
		}
	}
	
	/**
	 * 장애처리 정기점검 AP
	 * */
	@ClusterSynchronized( jobToken="APRegularChkProc")
	public void APRegularChkProc() {
		List<String> apList = regularDeviceChkService.getTargetDeviceList("DEV_003");
		Map<String,String> pMap = new HashMap<String,String>();
		int result = 0;
		pMap.put("devType", "DEV_003");
		pMap.put("clsCd" ,  "ERB_001");
		for(String ap : apList){
			pMap.put("errorType", "new");
			pMap.put("equipmentId", ap);
			String chkResult = bikeBatteryErrorService.chkExistMTCFaultInfo(pMap);
			if(!chkResult.equals("")) {
				pMap.put("errorType", "old");
				pMap.put("faultSeq", chkResult);
				result = regularDeviceChkService.setAPErrorProc(pMap);
			} else {
				pMap.put("errorType", "new");
				result = bikeBatteryErrorService.addNewBikeErrorProc(pMap);
				pMap.put("faultSeq", String.valueOf(result));
				result = regularDeviceChkService.setAPErrorProc(pMap);
			}
		}
	}
	
	
	/**
	 * 장애처리 정기점검 거치대
	 * */
	@ClusterSynchronized( jobToken="RackRegularChkProc")
	public void RackRegularChkProc() {
		List<String> rackList = regularDeviceChkService.getTargetDeviceList("DEV_004");
		Map<String,String> pMap = new HashMap<String,String>();
		pMap.put("devType", "DEV_004");
		pMap.put("clsCd"    ,  "ERB_001");
		int result = 0;
		for(String rack : rackList){
			pMap.put("errorType", "new");
			pMap.put("equipmentId", rack);
			String chkResult = bikeBatteryErrorService.chkExistMTCFaultInfo(pMap);
			if(!chkResult.equals("")) {
				pMap.put("errorType", "old");
				pMap.put("faultSeq", chkResult);
				result = regularDeviceChkService.setRackErrorProc(pMap);
			} else {
				pMap.put("errorType", "new");
				result = bikeBatteryErrorService.addNewBikeErrorProc(pMap);
				pMap.put("faultSeq", String.valueOf(result));
				result = regularDeviceChkService.setRackErrorProc(pMap);
			}
				
		}
	}
	
	
}
