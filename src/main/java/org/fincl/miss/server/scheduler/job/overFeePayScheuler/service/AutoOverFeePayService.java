/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service
 * @파일명          : AutoOverFeePayService.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.overFeePayScheuler.service;

import java.util.List;

import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;

/**
 * @파일명          : AutoOverFeePayService.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
public interface AutoOverFeePayService {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService.getOverFeeProcTarget
	 * @writeDay : 2015. 8. 31. 오전 11:45:41
	 * @return   : List<OverFeeVO>
	 * @Todo     :초과요금 자동 결제 리스트
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	List<OverFeeVO> getOverFeeProcTarget();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService.setOverFeePayComplete
	 * @writeDay : 2015. 8. 31. 오후 3:57:01
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	int setOverFeePayComplete(OverFeeVO fee);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService.addTicketPayment
	 * @writeDay : 2015. 9. 9. 오후 4:45:34
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 9.   |   ymshin   |  최초작성
	 */ 
	int addTicketPayment(OverFeeVO fee);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService.addTicketPaymentFail
	 * @writeDay : 2015. 9. 9. 오후 5:07:26
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 9.   |   ymshin   |  최초작성
	 */ 
	int addTicketPaymentFail(OverFeeVO fee);
	
	/**
	 * SVR TO SVR 통신에 의한 결제정보 중복 삽입방지_20161006_JJH
	 * @param fee
	 * @return
	 */
	java.util.Map<String, String> getPaymentInfoExist(OverFeeVO fee);

}
