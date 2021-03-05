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
package org.fincl.miss.server.scheduler.job.period.vo;

import org.apache.ibatis.type.Alias;

/**
 * @파일명          : PeriodTenMinuteErrorVO.java
 * @작성일          : 2016. 11. 18.
 * @작성자          : JJH
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |        수정내용
 * -------------------------------------------------------------
 *    2016. 11. 18. |        JJH       |  최초작성
 */

public class PeriodTenMinuteErrorVO {
    public String error_seq;
    public String bike_id;
    public String bike_no;
    public String bike_stus_cd;
    public String bike_battery_stus_cd;
    public String now_locate_id;
    public String rack_id;
    public String cascade_yn;
    public String cascade_bike_id;
    public String last_conn_dttm;
    public String reg_dttm;
    
	public String getError_seq() {
		return error_seq;
	}
	public void setError_seq(String error_seq) {
		this.error_seq = error_seq;
	}
	public String getBike_id() {
		return bike_id;
	}
	public void setBike_id(String bike_id) {
		this.bike_id = bike_id;
	}
	public String getBike_no() {
		return bike_no;
	}
	public void setBike_no(String bike_no) {
		this.bike_no = bike_no;
	}
	public String getBike_stus_cd() {
		return bike_stus_cd;
	}
	public void setBike_stus_cd(String bike_stus_cd) {
		this.bike_stus_cd = bike_stus_cd;
	}
	public String getBike_battery_stus_cd() {
		return bike_battery_stus_cd;
	}
	public void setBike_battery_stus_cd(String bike_battery_stus_cd) {
		this.bike_battery_stus_cd = bike_battery_stus_cd;
	}
	public String getNow_locate_id() {
		return now_locate_id;
	}
	public void setNow_locate_id(String now_locate_id) {
		this.now_locate_id = now_locate_id;
	}
	public String getRack_id() {
		return rack_id;
	}
	public void setRack_id(String rack_id) {
		this.rack_id = rack_id;
	}
	public String getCascade_yn() {
		return cascade_yn;
	}
	public void setCascade_yn(String cascade_yn) {
		this.cascade_yn = cascade_yn;
	}
	public String getCascade_bike_id() {
		return cascade_bike_id;
	}
	public void setCascade_bike_id(String cascade_bike_id) {
		this.cascade_bike_id = cascade_bike_id;
	}
	public String getLast_conn_dttm() {
		return last_conn_dttm;
	}
	public void setLast_conn_dttm(String last_conn_dttm) {
		this.last_conn_dttm = last_conn_dttm;
	}
	public String getReg_dttm() {
		return reg_dttm;
	}
	public void setReg_dttm(String reg_dttm) {
		this.reg_dttm = reg_dttm;
	}
	
	@Override
	public String toString() {
		return "PeriodTenMinuteErrorVO [error_seq=" + error_seq + ", bike_id="
				+ bike_id + ", bike_no=" + bike_no + ", bike_stus_cd="
				+ bike_stus_cd + ", bike_battery_stus_cd="
				+ bike_battery_stus_cd + ", now_locate_id=" + now_locate_id
				+ ", rack_id=" + rack_id + ", cascade_yn=" + cascade_yn
				+ ", cascade_bike_id=" + cascade_bike_id + ", last_conn_dttm="
				+ last_conn_dttm + ", reg_dttm=" + reg_dttm + "]";
	}
 
}
