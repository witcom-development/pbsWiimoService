/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.guest
 * @파일명          : GuestRemoveInfoMapper.java
 * @작성일          : 2015. 8. 27.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 27.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.guest;

import java.util.List;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : GuestRemoveInfoMapper.java
 * @작성일          : 2015. 8. 27.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 27.   |   ymshin   |  최초작성
 */
@Mapper("guestRemoveInfoMapper")
public interface GuestRemoveInfoMapper {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.guest.GuestRemoveInfoMapper.getGuestVoucherStataus
	 * @writeDay : 2015. 8. 27. 오후 2:48:23
	 * @return   : List<String>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 27.   |   ymshin   |  최초작성
	 */ 
	List<String> chkGuestVoucherStataus();

	/**
	 * @param seq 
	 * @location : org.fincl.miss.server.scheduler.job.guest.GuestRemoveInfoMapper.chkGuestOverFeeStatus
	 * @writeDay : 2015. 8. 27. 오후 3:13:53
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 27.   |   ymshin   |  최초작성
	 */ 
	int chkGuestOverFeeStatus(String seq);

	/**
	 * @param seq 
	 * @location : org.fincl.miss.server.scheduler.job.guest.GuestRemoveInfoMapper.setGuestRemoveInfo
	 * @writeDay : 2015. 8. 27. 오후 3:40:04
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 27.   |   ymshin   |  최초작성
	 */ 
	void setGuestRemoveInfo(String seq);

}
