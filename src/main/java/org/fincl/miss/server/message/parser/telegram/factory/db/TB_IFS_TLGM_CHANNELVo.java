package org.fincl.miss.server.message.parser.telegram.factory.db;

 
/** 
 * - 전문을 나타내는 유일한 식별자(Key)를 가져오는 인터페이스 -
 * 
 *
 */

public class TB_IFS_TLGM_CHANNELVo {
//    String headerId="";  
    String txType="";
    String txSystem="";
    String txName="";
    String chId="";
    String ifId="";
    String inOutTypeCd="";
    String relationId="";
    
    public String getRelationId() {
        return relationId;
    }
    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }
    public String getInOutTypeCd() {
        return inOutTypeCd;
    }
    public void setInOutTypeCd(String inOutTypeCd) {
        this.inOutTypeCd = inOutTypeCd;
    }
    public String getIfId() {
        return ifId;
    }
    public void setIfId(String ifId) {
        this.ifId = ifId;
    }
    public String getChId() {
        return chId;
    }
    public void setChId(String chId) {
        this.chId = chId;
    }
//    public String getHeaderId() {
//        return headerId;
//    }
//    public void setHeaderId(String headerId) {
//        this.headerId = headerId;
//    }
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
    public String getTxName() {
        return txName;
    }
    public void setTxName(String txName) {
        this.txName = txName;
    }
     
}
