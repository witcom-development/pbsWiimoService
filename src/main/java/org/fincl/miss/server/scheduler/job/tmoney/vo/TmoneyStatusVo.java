package org.fincl.miss.server.scheduler.job.tmoney.vo;

import org.apache.ibatis.type.Alias;

@Alias("tmoneyStatusVo")
public class TmoneyStatusVo {
	private String oid;
	private String ret;
	private String tid;
	private String cert;
	private String state;
	private String paymentReadResCd;
	private String amt;
	private String paymentReqSeq;
	private String queryType;
	private String type;
	private String result;
	
	
	
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPaymentReqSeq() {
		return paymentReqSeq;
	}
	public void setPaymentReqSeq(String paymentReqSeq) {
		this.paymentReqSeq = paymentReqSeq;
	}
	public String getAmt() {
		return amt;
	}
	public void setAmt(String amt) {
		this.amt = amt;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getCert() {
		return cert;
	}
	public void setCert(String cert) {
		this.cert = cert;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPaymentReadResCd() {
		return paymentReadResCd;
	}
	public void setPaymentReadResCd(String paymentReadResCd) {
		this.paymentReadResCd = paymentReadResCd;
	}
	
}
