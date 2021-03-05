package org.fincl.miss.server.message.parser.telegram.factory.db;

 
/** 
 * - 전문을 나타내는 유일한 식별자(Key)를 가져오는 인터페이스 -
 * 
 *
 */

public class TB_IFS_TLGM_HEADER_RELVo {
    String relationId="";
    String relationName="";
   
    String createId="";
    String createDate="";
    String modifyId="";
    String modifyDate="";
    public String getRelationId() {
        return relationId;
    }
    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }
    public String getRelationName() {
        return relationName;
    }
    public void setRelationName(String relationName) {
        this.relationName = relationName;
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
