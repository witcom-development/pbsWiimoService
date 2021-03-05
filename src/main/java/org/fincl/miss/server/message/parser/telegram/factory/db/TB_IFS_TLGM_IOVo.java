package org.fincl.miss.server.message.parser.telegram.factory.db;

 
/** 
 * - 전문을 나타내는 유일한 식별자(Key)를 가져오는 인터페이스 -
 * 
 *
 */

public class TB_IFS_TLGM_IOVo {
    String ifId;
    String ioId="";
    String ioVersion="";
    String ioName="";
    String delimiterCd="";
    String ioStatus="";
    String mgrId="";
    String createId="";
    String createDate="";

    String modifyId="";
    String modifyDate="";
    
    int messageType =0;
    
    
    //    String txSystem="";
//    String txType="";
    
    
    public int getMessageType() {
        return messageType;
    }
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    //    public String getTxSystem() {
//        return txSystem;
//    }
//    public void setTxSystem(String txSystem) {
//        this.txSystem = txSystem;
//    }
//    public String getTxType() {
//        return txType;
//    }
//    public void setTxType(String txType) {
//        this.txType = txType;
//    }
    public String getIfId() {
        return ifId;
    }
    public void setIfId(String ifId) {
        this.ifId = ifId;
    } 
    
    public String getIoId() {
        return ioId;
    }
    public void setIoId(String ioId) {
        this.ioId = ioId;
    }
    public String getIoVersion() {
        return ioVersion;
    }
    public void setIoVersion(String ioVersion) {
        this.ioVersion = ioVersion;
    }
    public String getIoName() {
        return ioName;
    }
    public void setIoName(String ioName) {
        this.ioName = ioName;
    }
    public String getDelimiterCd() {
        return delimiterCd;
    }
    public void setDelimiterCd(String delimiterCd) {
        this.delimiterCd = delimiterCd;
    }
    public String getIoStatus() {
        return ioStatus;
    }
    public void setIoStatus(String ioStatus) {
        this.ioStatus = ioStatus;
    }
    public String getMgrId() {
        return mgrId;
    }
    public void setMgrId(String mgrId) {
        this.mgrId = mgrId;
    }
    public String getCreateId() {
        return createId;
    }
    public void setCreateId(String createId) {
        this.createId = createId;
    }
    public String getCreateDate() {
        return createDate;
    }
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    public String getModifyId() {
        return modifyId;
    }
    public void setModifyId(String modifyId) {
        this.modifyId = modifyId;
    }
    public String getModifyDate() {
        return modifyDate;
    }
    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }
}
