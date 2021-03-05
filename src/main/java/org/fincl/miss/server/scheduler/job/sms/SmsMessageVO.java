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
@Alias(value="smsMessageVO")
public class SmsMessageVO implements Serializable{
	/**
	 * 
	 */
	protected static Logger log = LoggerFactory.getLogger(SmsMessageVO.class);
	
	private static final long serialVersionUID = -6587931405948965873L;

	public SmsMessageVO(){

	}
	
	/**
	 * 인덱스 필드(키필드) ß
	 */
	int seqno;
	
	/**
	 *  전송대상 번호
	 */
	String destno ;
	
	/**
	 *  콜백 번호 
	 */
	String callback = "1599-0120";
	
	/**
	 *  메시지 내용 
	 */
	String msg;

	/**
	 *  메시지가 insert 된 시간
	 */
	String insdttm;

	/**
	 *  전송요청시간(date type) 
	 */
	String reqdttm;
	
	/**
	 *  실제 휴대폰에 전송한 시간 
	 */
	String rsltdttm;

	/**
	 *  서버에서 Agent로 메시지 수신한 시간 
	 */
	String repdttm;
	
	/**
	 *  상태 코드 
	 */
	int status;
	
	/**
	 *  결과 코드 
	 */
	int result;

	/**
	 *  전송결과 휴대폰 통신사 
	 */
	int telcode;
	
	/**
	 *  Bill ID 
	 */
	String billid;

	/**
	 *  메시지 타입
	 */
	String type = "S";

	/**
	 *  MMS(LMS) 제목
	 */
	String title;

	/**
	 *  첨부파일명1 
	 */
	String file1;
	
	/**
	 *  첨부파일명2 
	 */
	String file2;

	/**
	 *  첨부파일명3 
	 */
	String file3;

	/**
	 *  첨부파일명4 
	 */
	String file4;

	/**
	 *  첨부파일명5 
	 */
	String file5;
	
	/**
	 *  사용자 참조 필드 1.
	 *  
	 */
	String autoSendId ;
	
	/**
	 * 기본값 'N'
	 */
	String autoSendYn = "N";
	
	String etc3;
	
	/**
	 * 기본값 0
	 */
	int sendCount = 0;
	

	public int getSeqno() {
		return seqno;
	}

	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

	public String getDestno() {
		return destno;
	}

	public void setDestno(String destno) {
		this.destno = destno;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getInsdttm() {
		return insdttm;
	}

	public void setInsdttm(String insdttm) {
		this.insdttm = insdttm;
	}

	public String getReqdttm() {
		return reqdttm;
	}

	public void setReqdttm(String reqdttm) {
		this.reqdttm = reqdttm;
	}

	public String getRsltdttm() {
		return rsltdttm;
	}

	public void setRsltdttm(String rsltdttm) {
		this.rsltdttm = rsltdttm;
	}

	public String getRepdttm() {
		return repdttm;
	}

	public void setRepdtm(String repdttm) {
		this.repdttm = repdttm;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getTelcode() {
		return telcode;
	}

	public void setTelcode(int telcode) {
		this.telcode = telcode;
	}

	public String getBillid() {
		return billid;
	}

	public void setBillid(String billid) {
		this.billid = billid;
	}

	public String getType() {
		try{
			if(this.msg.getBytes("UTF-8").length > 90){
				type = "M";
			}
		}catch(Exception e){
			type = "S";
		}
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getFile2() {
		return file2;
	}

	public void setFile2(String file2) {
		this.file2 = file2;
	}

	public String getFile3() {
		return file3;
	}

	public void setFile3(String file3) {
		this.file3 = file3;
	}

	public String getFile4() {
		return file4;
	}

	public void setFile4(String file4) {
		this.file4 = file4;
	}

	public String getFile5() {
		return file5;
	}

	public void setFile5(String file5) {
		this.file5 = file5;
	}

	public String getEtc3() {
		return etc3;
	}

	public void setEtc3(String etc3) {
		this.etc3 = etc3;
	}

	public String getAutoSendId() {
		return autoSendId;
	}

	public void setAutoSendId(String autoSendId) {
		this.autoSendId = autoSendId;
	}

	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}

	public String getAutoSendYn() {
		return autoSendYn;
	}

	public void setAutoSendYn(String autoSendYn) {
		this.autoSendYn = autoSendYn;
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
