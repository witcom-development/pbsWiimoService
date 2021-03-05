package org.fincl.miss.server.sms.vo;

import javax.validation.GroupSequence;

import org.apache.ibatis.type.Alias;

@Alias("smsBodyVO")
public class SmsBodyVO implements java.io.Serializable {
	
	private static final long serialVersionUID = -5351574270118880859L;
	
	private long smsSeq;
	private String sendClsName;
	private String autoSendYN;
	private String autoSendID;
	private String orignlMsg;
	private String noteDesc;
	private String regID;
	private java.util.Date regDttm;
	
	private String adminID;
	private String adminName;
	
	
	public SmsBodyVO(){
		this.autoSendYN = "N";
	}
	
	
	public long getSmsSeq() {
		return smsSeq;
	}

	public void setSmsSeq(long smsSeq) {
		this.smsSeq = smsSeq;
	}

	public String getSendClsName() {
		return sendClsName;
	}

	public void setSendClsName(String sendClsName) {
		this.sendClsName = sendClsName;
	}

	public String getAutoSendYN() {
		return autoSendYN;
	}

	public void setAutoSendYN(String autoSendYN) {
		this.autoSendYN = autoSendYN;
	}

	public String getAutoSendID() {
		return autoSendID;
	}

	public void setAutoSendID(String autoSendID) {
		this.autoSendID = autoSendID;
	}

	public String getOrignlMsg() {
		return orignlMsg;
	}

	public void setOrignlMsg(String orignlMsg) {
		this.orignlMsg = orignlMsg;
	}

	public String getNoteDesc() {
		return noteDesc;
	}

	public void setNoteDesc(String noteDesc) {
		this.noteDesc = noteDesc;
	}

	public String getRegID() {
		return regID;
	}

	public void setRegID(String regID) {
		this.regID = regID;
	}

	public java.util.Date getRegDttm() {
		return regDttm;
	}

	public void setRegDttm(java.util.Date regDttm) {
		this.regDttm = regDttm;
	}

	public String getAdminID() {
		return adminID;
	}

	public void setAdminID(String adminID) {
		this.adminID = adminID;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	
	@Override
	public String toString() {
		return "SmsBodyVO [smsSeq=" + smsSeq + ", sendClsName=" + sendClsName
				+ ", autoSendYN=" + autoSendYN + ", autoSendID=" + autoSendID
				+ ", orignlMsg=" + orignlMsg + ", noteDesc=" + noteDesc
				+ ", regID=" + regID + ", regDttm=" + regDttm + ", adminID="
				+ adminID + ", adminName=" + adminName
				+ ", toString()=" + super.toString() + ", getMode()="
				+ ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + "]";
	}
	
	private interface SmsList{}
	private interface SmsEdit{}

	@GroupSequence({SmsList.class})
	public static interface SmsListVal{}
	
	@GroupSequence({SmsEdit.class})
	public static interface SmsEditVal{}
	
}
