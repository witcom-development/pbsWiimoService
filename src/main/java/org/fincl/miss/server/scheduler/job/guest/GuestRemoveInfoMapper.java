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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

import org.fincl.miss.server.scheduler.job.guest.vo.*;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;

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

	List<HashMap<String, Object>> chkGuestVoucherStataus();
	
	List<HashMap<String, Object>> chkRentInvalidReturnOver();

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
	
	void addRefundHist(String paymentSeq);
	
	void addRefundHistFail(String paymentSeq);
	
	void setCancelInfo(String cancelSeq);
	
	void setVoucherUseComplete(String voucherSeq);
	
	//WIIMO BIZ ADD
	void insertParkingInfo(RentHistVo info);
	void updateBikeLocation(RentHistVo info);
	void insertBikeLocation(RentHistVo info);
	void updateBike(RentHistVo info);
	void updateBike_012(RentHistVo info);
	void updateBikeGPS(RentHistVo info);
	String getRentSeq(RentHistVo info);
	void insertRentHist(RentHistVo info);
	void deleteRentGPSDATA(RentHistVo HistVo) ;
	void deleteRentInfo_rserved(RentHistVo info);
	void insertRentOverFee(RentHistVo info);
	int setOverFeePayReset(OverFeeVO fee);
	int addTicketPayment(OverFeeVO fee);
	int setOverFeePayComplete(OverFeeVO fee);
	java.util.Map<String, String> getPaymentInfoExist(OverFeeVO fee);
	public OverFeeVO getOverFeeRETURN(String USR_SEQ);
	RentHistVo getForReturnUse(String bicycle_id);
	Map<String, Object> getBikeRETURN_GPS(String RENT_SEQ);
	public HashMap<String, Object> CheckStation_ForGPS(Map<String, String> GPS_DATA);
	Map<String, Object> getBikeMoveDist_COUNT(Map<String, String> GPS_DATA);
	double getBikeMoveDist_Last(Map<String, String> GPS_DATA);
	int getUserWeight(String usr_SEQ);
	Map<String, Object> getOverFeeMinPolicy(Map<String, Object> fee);
	void parkingInfoDelete(String bicycleId);
	void updateDeviceState(RentHistVo info);
	void deleteRentInfo(RentHistVo info);
	String getStationNo (String RETURN_RACK_ID);
	String getStationName (String RETURN_RACK_ID);
	int addENFRC_RETURN(RentHistVo info);
	//WIIMO BIZ ADD END

}
