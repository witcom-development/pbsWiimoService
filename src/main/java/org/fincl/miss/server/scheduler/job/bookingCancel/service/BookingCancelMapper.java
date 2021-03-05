/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.app.rent.bookingCancel.service
 * @파일명          : BookingCancel.java
 * @작성일          : 2015. 7. 15.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 15.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.bookingCancel.service;

import java.util.List;

import org.fincl.miss.server.scheduler.job.bookingCancel.vo.BookingCancelVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : BookingCancel.java
 * @작성일          : 2015. 7. 15.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 15.   |   ymshin   |  최초작성
 */
@Mapper(value="bookingCancelMapper")
public interface BookingCancelMapper {

   /**
	 * @location : com.dkitec.spb.app.rent.bookingCancel.service.BookingCancelMapper.setOverTimeBookingRack
	 * @writeDay : 2015. 7. 13. 오후 5:06:27
	 * @return   : void
	 * @Todo     : 예약된 대여 데이터 중에 거치대 임시예약 한도 시간이 지난것들을 찿아서 거치대를 예약->정상으로 상태 변경
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 7. 13.   |   ymshin   |  최초작성
	*/ 
	void setOverTimeBookingRack();

	/**
	 * @location : com.dkitec.spb.app.rent.bookingCancel.service.BookingCancelMapper.delOverTimeBookingRent
	 * @writeDay : 2015. 7. 13. 오후 5:06:27
	 * @return   : void
	 * @Todo     :예약된 대여 데이터 중에 거치대 임시예약 한도 시간이 지난것들을 찿아서 대여 테이블 정보 삭제.
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 7. 13.   |   ymshin   |  최초작성
	 */ 
	int delOverTimeBookingRent();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bookingCancel.service.BookingCancelMapper.setOverTimeRentRack
	 * @writeDay : 2015. 7. 22. 오후 2:00:57
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 7. 22.   |   ymshin   |  최초작성
	 */ 
	int setOverTimeRentRack();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.bookingCancel.service.BookingCancelMapper.getBookingCancelMsgList
	 * @writeDay : 2015. 8. 19. 오전 11:20:10
	 * @return   : List<BookingCancelVO>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 19.   |   ymshin   |  최초작성
	 */ 
	List<BookingCancelVO> getBookingCancelMsgList();
}
