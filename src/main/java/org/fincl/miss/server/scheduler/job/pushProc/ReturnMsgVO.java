package org.fincl.miss.server.scheduler.job.pushProc;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ReturnMsgVO implements Serializable{

	private String usrSeq;
	private String usrDeviceId;
	private String usrDeviceType;
	private String usrMpnNo;
	private String bikeNo;
	private String baseRentTime;
	private String rentClsCd;
	private String rentBikeID;
	
	public String getRentBikeID() {
		return rentBikeID;
	}
	public void setRentBikeID(String rentBikeID) {
		this.rentBikeID = rentBikeID;
	}
	public String getUsrSeq() {
		return usrSeq;
	}
	public void setUsrSeq(String usrSeq) {
		this.usrSeq = usrSeq;
	}
	public String getUsrDeviceId() {
		return usrDeviceId;
	}
	public void setUsrDeviceId(String usrDeviceId) {
		this.usrDeviceId = usrDeviceId;
	}
	public String getUsrDeviceType() {
		return usrDeviceType;
	}
	public void setUsrDeviceType(String usrDeviceType) {
		this.usrDeviceType = usrDeviceType;
	}
	public String getUsrMpnNo() {
		return usrMpnNo;
	}
	public void setUsrMpnNo(String usrMpnNo) {
		this.usrMpnNo = usrMpnNo;
	}
	public String getBikeNo() {
		return bikeNo;
	}
	public void setBikeNo(String bikeNo) {
		this.bikeNo = bikeNo;
	}
	public String getBaseRentTime() {
		return baseRentTime;
	}
	public void setBaseRentTime(String baseRentTime) {
		this.baseRentTime = baseRentTime;
	}
	public String getRentClsCd() {
		return rentClsCd;
	}
	public void setRentClsCd(String rentClsCd) {
		this.rentClsCd = rentClsCd;
	}
}
