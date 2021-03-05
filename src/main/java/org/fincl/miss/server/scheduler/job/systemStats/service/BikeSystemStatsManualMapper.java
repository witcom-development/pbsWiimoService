/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.systemStats.service
 * @파일명          : BikeSystemStatsManualMapper.java
 * @작성일          : 2015. 9. 2.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 2.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.systemStats.service;

import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : BikeSystemStatsManualMapper.java
 * @작성일          : 2015. 9. 2.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 2.   |   ymshin   |  최초작성
 */
@Mapper("bikeSystemStatsManualMapper")
public interface BikeSystemStatsManualMapper {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsManualMapper.delManualStatsData
	 * @writeDay : 2015. 9. 3. 오전 11:26:48
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 3.   |   ymshin   |  최초작성
	 */ 
	int delManualStatsData(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsManualMapper.addManualStatsData
	 * @writeDay : 2015. 9. 3. 오전 11:27:44
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 3.   |   ymshin   |  최초작성
	 */ 
	int addManualStatsData(Map<String, String> pMap);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsManualMapper.addManualStatsData
	 * @writeDay : 2015. 9. 3. 오전 11:26:55
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 3.   |   ymshin   |  최초작성
	 */ 

}
