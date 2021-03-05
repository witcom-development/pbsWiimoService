/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.bikeError
 * @파일명          : BikeBatteryErrorMapper.java
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

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : BikeBatteryErrorMapper.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Mapper(value="bikeBatteryErrorMapper")
public interface BikeBatteryErrorMapper {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.getBikeBatteryErrorList
	 * @writeDay : 2015. 8. 31. 오후 6:23:14
	 * @return   : List<String>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	List<String> getBikeBatteryErrorList();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.initBatteryCnt
	 * @writeDay : 2015. 8. 31. 오후 6:43:10
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	int initBatteryCnt(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.chkExistMTCFaultInfo
	 * @writeDay : 2015. 8. 31. 오후 6:43:17
	 * @return   : String
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	String chkExistMTCFaultInfo(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.addDeviceErrFaultDetail
	 * @writeDay : 2015. 9. 1. 오전 9:55:17
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int addDeviceErrFaultDetail(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.addDeviceErrReport
	 * @writeDay : 2015. 9. 1. 오전 10:04:23
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int addDeviceErrReport(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.getIOTBikeErrorList
	 * @writeDay : 2015. 9. 1. 오후 1:42:59
	 * @return   : List<Map<String,String>>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	List<HashMap<String, String>> getIOTBikeErrorList();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.getTargetDeviceList
	 * @writeDay : 2015. 9. 1. 오후 9:48:43
	 * @return   : List<String>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	List<String> getTargetDeviceList(Map<String, String> map);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.setAPErrorProc
	 * @writeDay : 2015. 9. 3. 오전 10:15:53
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 3.   |   ymshin   |  최초작성
	 */ 
	int setAPErrorProc(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper.setRackErrorProc
	 * @writeDay : 2015. 9. 3. 오전 10:16:00
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 3.   |   ymshin   |  최초작성
	 */ 
	int setRackErrorProc(Map<String, String> pMap);

}
