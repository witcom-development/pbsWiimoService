package org.fincl.miss.server.scheduler.job.voucherEndChk.vo;

public class VoucherEndChkVO {
	private String payment_cls_cd;
	private String payment_cd;
	private String usr_seq;
	private String usr_mpn_no;
	private String telecom_cd;
	
	public String getPayment_cls_cd() {
		return payment_cls_cd;
	}
	public void setPayment_cls_cd(String payment_cls_cd) {
		this.payment_cls_cd = payment_cls_cd;
	}
	public String getPayment_cd() {
		return payment_cd;
	}
	public void setPayment_cd(String payment_cd) {
		this.payment_cd = payment_cd;
	}
	public String getUsr_seq() {
		return usr_seq;
	}
	public void setUsr_seq(String usr_seq) {
		this.usr_seq = usr_seq;
	}
	public String getUsr_mpn_no() {
		return usr_mpn_no;
	}
	public void setUsr_mpn_no(String usr_mpn_no) {
		this.usr_mpn_no = usr_mpn_no;
	}
	public String getTelecom_cd() {
		return telecom_cd;
	}
	public void setTelecom_cd(String telecom_cd) {
		this.telecom_cd = telecom_cd;
	}

}
