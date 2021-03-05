/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo
 * @파일명          : OverFeeVO.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo;

/**
 * @파일명          : OverFeeVO.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
public class OverFeeVO {
    
	private String usrSeq;
    private String paymentClsCd;
    private String rentHistSeq;
    private String billingKey;
    private String rentDttm;
    private String paymentAttCnt;
    private String paymentMethodCd;
    private String paymentStusCd;
    private String paymentFailHistSeq;
    private String paymentSeq;
    private String mpnNo;
    private String cardNo;
    private String overFee;
    private String mbId;
    private String mbEmailName;
    private String usrMpnNo;
    private String ReplyMsg;
    private String ReplyCode;
    private String paymentConfmNo;
    private String totAmt;
    private String mileagePaymentAmt;
    private String resultCD;
    private String processReasonDesc;
    private String overMi;
    private String mb_serial_no;
    private String payment_cls_cd;
    
    
	public String getPayment_cls_cd() {
		return payment_cls_cd;
	}
	public void setPayment_cls_cd(String payment_cls_cd) {
		this.payment_cls_cd = payment_cls_cd;
	}
	public String getMb_serial_no() {
		return mb_serial_no;
	}
	public void setMb_serial_no(String mb_serial_no) {
		this.mb_serial_no = mb_serial_no;
	}
	public String getOverMi() {
		return overMi;
	}
	public void setOverMi(String overMi) {
		this.overMi = overMi;
	}
	public String getProcessReasonDesc() {
		return processReasonDesc;
	}
	public void setProcessReasonDesc(String processReasonDesc) {
		this.processReasonDesc = processReasonDesc;
	}
	public String getPaymentFailHistSeq() {
		return paymentFailHistSeq;
	}
	public void setPaymentFailHistSeq(String paymentFailHistSeq) {
		this.paymentFailHistSeq = paymentFailHistSeq;
	}
	public String getPaymentSeq() {
		return paymentSeq;
	}
	public void setPaymentSeq(String paymentSeq) {
		this.paymentSeq = paymentSeq;
	}
	public String getResultCD() {
		return resultCD;
	}
	public void setResultCD(String resultCD) {
		this.resultCD = resultCD;
	}
	public String getPaymentStusCd() {
		return paymentStusCd;
	}
	public void setPaymentStusCd(String paymentStusCd) {
		this.paymentStusCd = paymentStusCd;
	}
	public String getPaymentConfmNo() {
		return paymentConfmNo;
	}
	public void setPaymentConfmNo(String paymentConfmNo) {
		this.paymentConfmNo = paymentConfmNo;
	}
	public String getTotAmt() {
		return totAmt;
	}
	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}
	public String getMileagePaymentAmt() {
		return mileagePaymentAmt;
	}
	public void setMileagePaymentAmt(String mileagePaymentAmt) {
		this.mileagePaymentAmt = mileagePaymentAmt;
	}
	public String getUsrMpnNo() {
		return usrMpnNo;
	}
	public void setUsrMpnNo(String usrMpnNo) {
		this.usrMpnNo = usrMpnNo;
	}
	public String getReplyMsg() {
		return ReplyMsg;
	}
	public void setReplyMsg(String replyMsg) {
		ReplyMsg = replyMsg;
	}
	public String getReplyCode() {
		return ReplyCode;
	}
	public void setReplyCode(String replyCode) {
		ReplyCode = replyCode;
	}
	public String getUsrSeq() {
		return usrSeq;
	}
	public void setUsrSeq(String usrSeq) {
		this.usrSeq = usrSeq;
	}
	public String getPaymentClsCd() {
		return paymentClsCd;
	}
	public void setPaymentClsCd(String paymentClsCd) {
		this.paymentClsCd = paymentClsCd;
	}
	public String getRentHistSeq() {
		return rentHistSeq;
	}
	public void setRentHistSeq(String rentHistSeq) {
		this.rentHistSeq = rentHistSeq;
	}
	public String getBillingKey() {
		return billingKey;
	}
	public void setBillingKey(String billingKey) {
		this.billingKey = billingKey;
	}
	public String getRentDttm() {
		return rentDttm;
	}
	public void setRentDttm(String rentDttm) {
		this.rentDttm = rentDttm;
	}
	public String getPaymentAttCnt() {
		return paymentAttCnt;
	}
	public void setPaymentAttCnt(String paymentAttCnt) {
		this.paymentAttCnt = paymentAttCnt;
	}
	public String getPaymentMethodCd() {
		return paymentMethodCd;
	}
	public void setPaymentMethodCd(String paymentMethodCd) {
		this.paymentMethodCd = paymentMethodCd;
	}
	public String getMpnNo() {
		return mpnNo;
	}
	public void setMpnNo(String mpnNo) {
		this.mpnNo = mpnNo;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getOverFee() {
		return overFee;
	}
	public void setOverFee(String overFee) {
		this.overFee = overFee;
	}
	public String getMbId() {
		return mbId;
	}
	public void setMbId(String mbId) {
		this.mbId = mbId;
	}
	public String getMbEmailName() {
		return mbEmailName;
	}
	public void setMbEmailName(String mbEmailName) {
		this.mbEmailName = mbEmailName;
	}
    
    
}
