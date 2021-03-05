/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.bikeError
 * @파일명          : BikeBatteryErrorService.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.bikeError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @파일명          : BikeBatteryErrorService.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
public interface BikeBatteryErrorService {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService.getBikeBatteryErrorList
	 * @writeDay : 2015. 8. 31. 오후 6:20:09
	 * @return   : List<String>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	List<String> getBikeBatteryErrorList();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService.chkExistMTCFaultInfo
	 * @writeDay : 2015. 8. 31. 오후 6:41:46
	 * @return   : String
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	String chkExistMTCFaultInfo(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService.initBatteryCnt
	 * @writeDay : 2015. 8. 31. 오후 6:41:51
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	int initBatteryCnt(Map<String, String> pMap);

	/**
	 * @param pMap 
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService.setBikeErrorProc
	 * @writeDay : 2015. 9. 1. 오전 9:38:02
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int setBikeErrorProc(Map<String, String> pMap);

	/**
	 * @param pMap 
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService.addNewBikeErrorProc
	 * @writeDay : 2015. 9. 1. 오전 9:42:47
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int addNewBikeErrorProc(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService.getIOTBikeErrorList
	 * @writeDay : 2015. 9. 1. 오후 1:32:58
	 * @return   : List<Map<String,String>>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	List<HashMap<String, String>> getIOTBikeErrorList();
}
