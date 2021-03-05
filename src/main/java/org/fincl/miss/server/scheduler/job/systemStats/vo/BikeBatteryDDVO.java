package org.fincl.miss.server.scheduler.job.systemStats.vo;

public class BikeBatteryDDVO {
	private String REG_YEAR;
	private String LEAP_YEAR_YN;
	private String DATE;
	private String BIKE_BATTERY_STATE;
	private String COLUMN_NAME;
	private java.util.List<java.util.Map<String, String>> bikeList;
	private String BIKE_ID;
	
	public String getREG_YEAR() {
		return REG_YEAR;
	}
	public void setREG_YEAR(String rEG_YEAR) {
		REG_YEAR = rEG_YEAR;
	}
	public String getLEAP_YEAR_YN() {
		return LEAP_YEAR_YN;
	}
	public void setLEAP_YEAR_YN(String lEAP_YEAR_YN) {
		LEAP_YEAR_YN = lEAP_YEAR_YN;
	}
	public String getDATE() {
		return DATE;
	}
	public void setDATE(String dATE) {
		DATE = dATE;
	}
	public String getBIKE_BATTERY_STATE() {
		return BIKE_BATTERY_STATE;
	}
	public void setBIKE_BATTERY_STATE(String bIKE_BATTERY_STATE) {
		BIKE_BATTERY_STATE = bIKE_BATTERY_STATE;
	}
	public java.util.List<java.util.Map<String, String>> getBikeList() {
		return bikeList;
	}
	public void setBikeList(java.util.List<java.util.Map<String, String>> bikeList) {
		this.bikeList = bikeList;
	}
	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}
	public void setCOLUMN_NAME(String cOLUMN_NAME) {
		COLUMN_NAME = cOLUMN_NAME;
	}
	public String getBIKE_ID() {
		return BIKE_ID;
	}
	public void setBIKE_ID(String bIKE_ID) {
		BIKE_ID = bIKE_ID;
	}
	
	@Override
	public String toString() {
		return "BikeBatteryDDVO [REG_YEAR=" + REG_YEAR + ", LEAP_YEAR_YN="
				+ LEAP_YEAR_YN + ", DATE=" + DATE + ", BIKE_BATTERY_STATE="
				+ BIKE_BATTERY_STATE + ", COLUMN_NAME=" + COLUMN_NAME
				+ ", bikeList=" + bikeList + ", BIKE_ID=" + BIKE_ID + "]";
	}
}
