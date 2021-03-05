/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.bookingCancel.vo
 * @파일명          : BookingCancelVO.java
 * @작성일          : 2015. 8. 19.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 19.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.bookingCancel.vo;

/**
 * @파일명          : BookingCancelVO.java
 * @작성일          : 2015. 8. 19.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 19.   |   ymshin   |  최초작성
 */
public class BookingCancelVO {
    
	private String usrMpnNo;
	private String stationName;
	private String stationEquipOrder;
	public String getUsrMpnNo() {
		return usrMpnNo;
	}
	public void setUsrMpnNo(String usrMpnNo) {
		this.usrMpnNo = usrMpnNo;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getStationEquipOrder() {
		return stationEquipOrder;
	}
	public void setStationEquipOrder(String stationEquipOrder) {
		this.stationEquipOrder = stationEquipOrder;
	}
	
}
