/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service
 * @파일명          : AutoOverFeePayMapper.java
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

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : AutoOverFeePayMapper.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Mapper(value="autoOverFeePayMapper")
public interface AutoOverFeePayMapper {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayMapper.getOverFeeProcTarget
	 * @writeDay : 2015. 8. 31. 오후 1:11:46
	 * @return   : List<OverFeeVO>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	List<OverFeeVO> getOverFeeProcTarget();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayMapper.addTicketPayment
	 * @writeDay : 2015. 9. 9. 오후 5:18:20
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 9.   |   ymshin   |  최초작성
	 */ 
	int addTicketPayment(OverFeeVO fee);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayMapper.addTicketPaymentFail
	 * @writeDay : 2015. 9. 9. 오후 5:18:34
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 9.   |   ymshin   |  최초작성
	 */ 
	int addTicketPaymentFail(OverFeeVO fee);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayMapper.setOverFeePayComplete
	 * @writeDay : 2015. 9. 9. 오후 5:18:49
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 9.   |   ymshin   |  최초작성
	 */ 
	int setOverFeePayComplete(OverFeeVO fee);
	
	/**
	 * SVR TO SVR 통신에 의한 결제정보 중복 삽입방지_20161006_JJH
	 * @param fee
	 * @return
	 */
	java.util.Map<String, String> getPaymentInfoExist(OverFeeVO fee);

}
