package org.fincl.miss.server.scheduler.job.sms;

import java.io.Serializable;

import javax.annotation.Resource;

import org.apache.ibatis.type.Alias;
import org.fincl.miss.server.scheduler.job.sms.service.SmsMessageMapper;
import org.fincl.miss.server.scheduler.job.sms.service.SmsMessageService;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SMS 메시지
 * @author civan
 *
 */
@Alias(value="TAPPMessageVO")
public class TAPPMessageVO implements Serializable{
	/**
	 * 
	 */
	protected static Logger log = LoggerFactory.getLogger(SmsMessageVO.class);
	
	private static final long serialVersionUID = -6587931405948965873L;

	public TAPPMessageVO(){

	}
	
	/**
	 * 인덱스 필드(키필드) ß
	 */
	String notice_se;
	String usr_seq;
	String bike_no;
	String station_name;
	String over_mi;
	String over_fee;
	String msg;
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 *  사용자 참조 필드 1.
	 *  
	 */
	String autoSendId ;
	
	

	public String getNotice_se() {
		return notice_se;
	}

	public void setNotice_se(String notice_se) {
		this.notice_se = notice_se;
	}

	public String getUsr_seq() {
		return usr_seq;
	}

	public void setUsr_seq(String usr_seq) {
		this.usr_seq = usr_seq;
	}

	public String getBike_no() {
		return bike_no;
	}

	public void setBike_no(String bike_no) {
		this.bike_no = bike_no;
	}

	public String getStation_name() {
		return station_name;
	}

	public void setStation_name(String station_name) {
		this.station_name = station_name;
	}

	public String getOver_mi() {
		return over_mi;
	}

	public void setOver_mi(String over_mi) {
		this.over_mi = over_mi;
	}

	public String getOver_fee() {
		return over_fee;
	}

	public void setOver_fee(String over_fee) {
		this.over_fee = over_fee;
	}

	public String getAutoSendId() {
		return autoSendId;
	}

	public void setAutoSendId(String autoSendId) {
		this.autoSendId = autoSendId;
	}

	public boolean setMsg(SendType sendType, String ... args){
		boolean check = true;
		this.autoSendId = sendType.getCode();
		String str = sendType.toString();
		//String str = SmsSender.getMsg(this.autoSendId);

		int paramCount = paramCount(str);
		
		if(paramCount >0 && paramCount == args.length){
			StringBuffer sb = new StringBuffer();
			int i=0;
			for(String param:args){
				sb.setLength(0);
				sb.append("{").append(i).append("}");
				str = StringUtil.replace(str, sb.toString(), param);
				i++;
			}
		}else if(paramCount >0 && paramCount != args.length){
			check = false;
		}
		if(check){
			this.msg = str;
		}
		return check;
	}
	
	private int paramCount(String str){
		int start = str.lastIndexOf("{");
		int result = 0;
		if(start>0){
			int end = str.lastIndexOf("}");
			try{
				String count = str.substring(start+1, end);
				result = Integer.parseInt(count)+1;
			}catch(Exception e){
				result = 0;
			}
		}
		return result;
	}

}
