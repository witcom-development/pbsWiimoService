/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.refundDeposit.service
 * @파일명          : UsrRefundDepositMapper.java
 * @작성일          : 2015. 9. 4.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 4.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.refundDeposit.service;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : UsrRefundDepositMapper.java
 * @작성일          : 2015. 9. 4.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 4.   |   ymshin   |  최초작성
 */
@Mapper("usrRefundDepositMapper")
public interface UsrRefundDepositMapper {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper.getRefundUsrList
	 * @writeDay : 2015. 9. 4. 오후 9:42:48
	 * @return   : List<String>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 4.   |   ymshin   |  최초작성
	 */ 
	List<Map<String,String>> getRefundUsrList();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper.setVoucherUseComplete
	 * @writeDay : 2015. 9. 6. 오후 3:19:54
	 * @return   : void
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 6.   |   ymshin   |  최초작성
	 */ 
	void setVoucherUseComplete(Map<String, String> map);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper.getRefundUseVoucherList
	 * @writeDay : 2015. 9. 6. 오후 6:20:49
	 * @return   : List<Map<String,String>>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 6.   |   ymshin   |  최초작성
	 */ 
	List<Map<String, String>> getRefundUseVoucherList();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper.getRentChk
	 * @writeDay : 2015. 9. 6. 오후 6:38:05
	 * @return   : String
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 6.   |   ymshin   |  최초작성
	 */ 
	String getRentChk(Map<String, String> map);

	/**
	 * @location : org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper.getRentHistChk
	 * @writeDay : 2015. 9. 6. 오후 6:47:20
	 * @return   : String
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 6.   |   ymshin   |  최초작성
	 */ 
	int getRentHistChk(Map<String, String> map);

	int chkIsOverFeeUsr(String string);

	void addRefundHist(Map<String, String> map);

	Map<String, String> getVoucherInfo(Map<String, String> map);

	void addTicketPaymentFail(Map<String, String> map);

	void setSealedVoucherCancel(Map<String, String> voucherMap);
	
	void serialNoPaymentCancel(Map<String, String> paramMap);

}
