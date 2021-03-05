package org.fincl.miss.server.message.parser.telegram.factory.db;

 
/** 
 * - 전문을 나타내는 유일한 식별자(Key)를 가져오는 인터페이스 -
 * 
 *
 */

public class TB_IFS_TLGM_HEADERVo {
    String headerId="";
    String headerVersion="";
    String headerName="";
    String delimiterCd="";
 
    String mgrId="";
    String createId="";
    String createDate="";
    String modifyId="";
    String modifyDate="";
    String relationId="";
    String dmcHeaderId="";
    
    public String getDmcHeaderId() {
        return dmcHeaderId;
    }
    public void setDmcHeaderId(String dmcHeaderId) {
        this.dmcHeaderId = dmcHeaderId;
    }
    public String getRelationId() {
        return relationId;
    }
    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }
    public String getHeaderId() {
        return headerId;
    }
    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
    public String getHeaderVersion() {
        return headerVersion;
    }
    public void setHeaderVersion(String headerVersion) {
        this.headerVersion = headerVersion;
    }
    public String getHeaderName() {
        return headerName;
    }
    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
    public String getDelimiterCd() {
        return delimiterCd;
    }
    public void setDelimiterCd(String delimiterCd) {
        this.delimiterCd = delimiterCd;
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
