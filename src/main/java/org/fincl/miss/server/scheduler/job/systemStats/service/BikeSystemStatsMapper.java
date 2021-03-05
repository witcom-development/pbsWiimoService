/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.systemSta.service
 * @파일명          : BikeSystemStatsMapper.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.systemStats.service;

import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.systemStats.vo.BikeBatteryDDVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : BikeSystemStatsMapper.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */
@Mapper(value="bikeSystemStatsMapper")
public interface BikeSystemStatsMapper {


	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_BIKE_USE_MM
	 * @writeDay : 2015. 9. 1. 오후 7:16:41
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_BIKE_USE_MM();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_BIKE_USE_DD
	 * @writeDay : 2015. 9. 1. 오후 7:19:23
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_BIKE_USE_DD();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_BIKE_USE_HH
	 * @writeDay : 2015. 9. 1. 오후 7:19:18
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_BIKE_USE_HH();
	
	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_NEW_MB_MM
	 * @writeDay : 2015. 9. 1. 오후 7:16:47
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_NEW_MB_MM();
	

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_NEW_MB_DD
	 * @writeDay : 2015. 9. 1. 오후 7:19:28
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_NEW_MB_DD();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_STATION_GRP_MOVE_DD
	 * @writeDay : 2015. 9. 1. 오후 7:19:34
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_STATION_GRP_MOVE_DD();
	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_STATION_GRP_MOVE_MM
	 * @writeDay : 2015. 9. 1. 오후 7:16:55
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_STATION_GRP_MOVE_MM();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_STATION_RENT_DD
	 * @writeDay : 2015. 9. 1. 오후 7:19:39
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_STATION_RENT_DD();
	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_STATION_RENT_MM
	 * @writeDay : 2015. 9. 1. 오후 7:17:04
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_STATION_RENT_MM();
	
	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_FAULT_MM
	 * @writeDay : 2015. 9. 1. 오후 7:17:13
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_FAULT_MM();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_FAULT_DD
	 * @writeDay : 2015. 9. 1. 오후 7:19:44
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_FAULT_DD();
	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_REPAIR_MM
	 * @writeDay : 2015. 9. 1. 오후 7:17:18
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_REPAIR_MM();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_REPAIR_DD
	 * @writeDay : 2015. 9. 1. 오후 7:19:49
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_REPAIR_DD();
	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_PAYMENT_MM
	 * @writeDay : 2015. 9. 1. 오후 7:17:24
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_PAYMENT_MM();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_PAYMENT_DD
	 * @writeDay : 2015. 9. 1. 오후 7:19:59
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_PAYMENT_DD();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_FOREIGN_RENT_DD
	 * @writeDay : 2015. 9. 1. 오후 7:20:10
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_FOREIGN_RENT_DD();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.systemStats.service.BikeSystemStatsMapper.insertTB_STA_FOREIGN_RENT_MM
	 * @writeDay : 2015. 9. 1. 오후 7:16:34
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 1.   |   ymshin   |  최초작성
	 */ 
	int insertTB_STA_FOREIGN_RENT_MM();

	void insertTB_STA_RANK_MB_MM();
	
	/**
	 * 일별 최대 운영자전거 현황_20161201_JJH
	 */
	void insertOperationMaxBikeDD();
	
	String getOperationMaxBike();
	
	/**
	 * 
	 * 자전거 별 배터리정보 관리_20170116_JJH
	 */
	List getBikeList();
	
	void insertBikeBatteryDD(BikeBatteryDDVO bikeBatteryDDVo);
	
	List getBikeBatteryInfo();
	
	void updateBikeBatteryInfo(Map bikeBatteryInfo);

}
