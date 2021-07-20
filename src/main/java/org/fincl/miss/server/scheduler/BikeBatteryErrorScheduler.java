/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job
 * @파일명          : BikeBatteryErrorScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @파일명          : BikeBatteryErrorScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Component
public class BikeBatteryErrorScheduler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource(name="bikeBatteryErrorService")
	private BikeBatteryErrorService bikeBatteryErrorService;
	
	@ClusterSynchronized( jobToken="bikeBatterErrorBatchProc")
	public void bikeBatterErrorBatchProc() {
		logger.debug("***********************베터리*ERB_003********************************");
		
		List<String> bikeList = bikeBatteryErrorService.getBikeBatteryErrorList();
		Map<String,String> pMap = null;
		int result = 0;
		if(bikeList != null) {
			for(String bike : bikeList) {
				pMap = new HashMap<String, String>();
				pMap.put("errorType", "old");
				pMap.put("equipmentId"   ,  bike);
				pMap.put("clsCd"    ,  "ERB_003");
				pMap.put("devType"  ,  "DEV_001");
				String chkResult = bikeBatteryErrorService.chkExistMTCFaultInfo(pMap);
				if(chkResult == null || chkResult.equals("")) {
					pMap.put("errorType", "new");
					chkResult = bikeBatteryErrorService.chkExistMTCFaultInfo(pMap);
					if(chkResult == null || chkResult.equals("")) {
						result = bikeBatteryErrorService.addNewBikeErrorProc(pMap);
						result = bikeBatteryErrorService.setBikeErrorProc(pMap);
					} else {
						pMap.put("faultSeq", chkResult);
						pMap.put("errorType", "old");
						result = bikeBatteryErrorService.setBikeErrorProc(pMap);
					}
					
				} else {
					result = bikeBatteryErrorService.initBatteryCnt(pMap);
				}
			}
		}
	}
	@ClusterSynchronized( jobToken="iotBikeBatterErrorBatchProc")
	public void iotBikeBatterErrorBatchProc() {
		
		logger.debug("START ***********************통신장애_밧데리 *ERB_004********************************");
		
		List<HashMap<String,String>> bikeList = bikeBatteryErrorService.getIOTBikeErrorList();
		Map<String,String> pMap = null;
		int result = 0;
		if(bikeList != null){
			for(HashMap<String,String> map : bikeList) {
				pMap = new HashMap<String, String>();
				pMap.put("errorType", "old");
				pMap.put("equipmentId",  map.get("deviceId"));
				pMap.put("devType"  ,  "DEV_001");
				pMap.put("clsCd",  "ERB_004");
				
				String chkResult = bikeBatteryErrorService.chkExistMTCFaultInfo(pMap);
				/*
				if(chkResult == null || chkResult.equals("")) {
					pMap.put("errorType", "new");
					chkResult = bikeBatteryErrorService.chkExistMTCFaultInfo(pMap);
					if(chkResult == null || chkResult.equals("")) {
						result = bikeBatteryErrorService.addNewBikeErrorProc(pMap);
						result = bikeBatteryErrorService.setBikeErrorProc(pMap);
					} else {
						pMap.put("faultSeq", chkResult);
						pMap.put("errorType", "old");
						result = bikeBatteryErrorService.setBikeErrorProc(pMap);
					}
				} 
				*/
			}
		}
		logger.debug("END  ***********************통신장애_밧데리 *ERB_004********************************");
		
	}
	
}
