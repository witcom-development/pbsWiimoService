package org.fincl.miss.server.message.parser.telegram.factory.db;

 
/** 
 * - 전문을 나타내는 유일한 식별자(Key)를 가져오는 인터페이스 -
 * 
 *
 */

public class TB_IFS_TLGM_IO_FIELDVo {
    String check;
    int id; 
    
    int ioVersion;
    public int getIoVersion() {
        return ioVersion;
    }

    public void setIoVersion(int ioVersion) {
        this.ioVersion = ioVersion;
    }
    public String getCheck() {
        return check;
    }
    public void setCheck(String check) {
        this.check = check;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
     




    String ifId;
  
    int parentIoOrder=0;
   
    String ioId;
    int ioType;
    int ioOrder;
    String ioField;
    String ioFieldName="";
    String parentIoField;
    String dataArrayField="";
    int ioDepth=0;
    String dataType="";
    int dataLength=0;
    int dataSize=0;
    String essentialYn="";
    String defaultDataValue="";
    String ioFieldDesc="";
    String createId="";
    String createDate="";
    String modifyId="";
    String modifyDate="";
    String dataValue=""; 
    
    public String getDataValue() {
        return dataValue;
    }
    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
    public String getIfId() {
        return ifId;
    }
    public void setIfId(String ifId) {
        this.ifId = ifId;
    }
    public int getParentIoOrder() {
        return parentIoOrder;
    }
    public void setParentIoOrder(int parentIoOrder) {
        this.parentIoOrder = parentIoOrder;
    }
    public String getDataArrayField() {
        return dataArrayField;
    }
    public void setDataArrayField(String dataArrayField) {
        this.dataArrayField = dataArrayField;
    }

    
    public String getParentIoField() {
        return parentIoField;
    }
    public void setParentIoField(String parentIoField) {
        this.parentIoField = parentIoField;
    }
    public String getIoId() {
        return ioId;
    }
    public void setIoId(String ioId) {
        this.ioId = ioId;
    }
    public int getIoType() {
        return ioType;
    }
    public void setIoType(int ioType) {
        this.ioType = ioType;
    }
    public int getIoOrder() {
        return ioOrder;
    }
    public void setIoOrder(int ioOrder) {
        this.ioOrder = ioOrder;
    }
    public String getIoField() {
        return ioField;
    }
    public void setIoField(String ioField) {
        this.ioField = ioField;
    }
    public String getIoFieldName() {
        return ioFieldName;
    }
    public void setIoFieldName(String ioFieldName) {
        this.ioFieldName = ioFieldName;
    }
    public int getIoDepth() {
        return ioDepth;
    }
    public void setIoDepth(int ioDepth) {
        this.ioDepth = ioDepth;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public int getDataLength() {
        return dataLength;
    }
    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }
    public int getDataSize() {
        return dataSize;
    }
    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
    public String getEssentialYn() {
        return essentialYn;
    }
    public void setEssentialYn(String essentialYn) {
        this.essentialYn = essentialYn;
    }
    public String getDefaultDataValue() {
        return defaultDataValue;
    }
    public void setDefaultDataValue(String defaultDataValue) {
        this.defaultDataValue = defaultDataValue;
    }
    public String getIoFieldDesc() {
        return ioFieldDesc;
    }
    public void setIoFieldDesc(String ioFieldDesc) {
        this.ioFieldDesc = ioFieldDesc;
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
