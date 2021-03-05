/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.regularChk.service
 * @파일명          : RegularDeviceChkSerivce.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.regularChk.service;

import java.util.List;
import java.util.Map;

/**
 * @파일명          : RegularDeviceChkSerivce.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */
public interface RegularDeviceChkSerivce {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce.getTargetDeviceList
	 * @writeDay : 2015. 9. 1. 오후 9:45:43
	 * @return   : List<String>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	List<String> getTargetDeviceList(String string);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce.setAPErrorProc
	 * @writeDay : 2015. 9. 3. 오전 10:11:37
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 3.   |   ymshin   |  최초작성
	 */ 
	int setAPErrorProc(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce.setRackErrorProc
	 * @writeDay : 2015. 9. 3. 오전 10:11:43
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 3.   |   ymshin   |  최초작성
	 */ 
	int setRackErrorProc(Map<String, String> pMap);

}
