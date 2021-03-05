package org.fincl.miss.server.message.parser.telegram.factory.db;

import java.io.Serializable;

 
public class TB_IFS_TLGM_IFVo implements Serializable{
 
    private static final long serialVersionUID = 5208623747118952891L;
    String ifId        ="";
    String sourceIoId        ="";
    String targetIoId        ="";
    String txType      ="";
    String txSystem    ="";
    String txWork      ="";
    String ifName      ="";
    String ifDesc      ="";
    String serviceId   ="";
    String ifStatusCd  ="";
    String mgrId       ="";
    String createId    ="";
    String createDate  ="";
    String modifyId    ="";
    String modifyDate  ="";
    
    String txTypeDesc      ="";
    String txSystemDesc    ="";
    String txWorkDesc      ="";
    
    String inBoundChId ="";
    String outBoundChId ="";
    
    
    public String getInBoundChId() {
        return inBoundChId;
    }
    public void setInBoundChId(String inBoundChId) {
        this.inBoundChId = inBoundChId;
    }
    public String getOutBoundChId() {
        return outBoundChId;
    }
    public void setOutBoundChId(String outBoundChId) {
        this.outBoundChId = outBoundChId;
    }
    public String getTxTypeDesc() {
        return txTypeDesc;
    }
    public void setTxTypeDesc(String txTypeDesc) {
        this.txTypeDesc = txTypeDesc;
    }
    public String getTxSystemDesc() {
        return txSystemDesc;
    }
    public void setTxSystemDesc(String txSystemDesc) {
        this.txSystemDesc = txSystemDesc;
    }
    public String getTxWorkDesc() {
        return txWorkDesc;
    }
    public void setTxWorkDesc(String txWorkDesc) {
        this.txWorkDesc = txWorkDesc;
    }
    public String getIfId() {
        return ifId;
    }
    public void setIfId(String ifId) {
        this.ifId = ifId;
    }
 
    public String getSourceIoId() {
        return sourceIoId;
    }
    public void setSourceIoId(String sourceIoId) {
        this.sourceIoId = sourceIoId;
    }
    public String getTargetIoId() {
        return targetIoId;
    }
    public void setTargetIoId(String targetIoId) {
        this.targetIoId = targetIoId;
    }
    public String getTxType() {
        return txType;
    }
    public void setTxType(String txType) {
        this.txType = txType;
    }
    public String getTxSystem() {
        return txSystem;
    }
    public void setTxSystem(String txSystem) {
        this.txSystem = txSystem;
    }
    public String getTxWork() {
        return txWork;
    }
    public void setTxWork(String txWork) {
        this.txWork = txWork;
    }
    public String getIfName() {
        return ifName;
    }
    public void setIfName(String ifName) {
        this.ifName = ifName;
    }
    public String getIfDesc() {
        return ifDesc;
    }
    public void setIfDesc(String ifDesc) {
        this.ifDesc = ifDesc;
    }
    public String getServiceId() {
        return serviceId;
    }
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    public String getIfStatusCd() {
        return ifStatusCd;
    }
    public void setIfStatusCd(String ifStatusCd) {
        this.ifStatusCd = ifStatusCd;
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
