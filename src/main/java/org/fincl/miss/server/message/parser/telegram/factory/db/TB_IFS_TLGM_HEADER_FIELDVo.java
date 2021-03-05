package org.fincl.miss.server.message.parser.telegram.factory.db;

 
/** 
 * - 전문을 나타내는 유일한 식별자(Key)를 가져오는 인터페이스 -
 * 
 *
 */

public class TB_IFS_TLGM_HEADER_FIELDVo {
    String check;
    int id;
    
    int headerVersion;
    public int getHeaderVersion() {
        return headerVersion;
    }

    public void setHeaderVersion(int headerVersion) {
        this.headerVersion = headerVersion;
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
    String headerId="";
    int headerOrder=0;
    int headerDepth=0;
    String headerField="";
    String headerFieldName="";
    String dataType="";
    int dataLength=0;
    int dataSize=0;
    String dataArrayField="";
    String essentialYn="";
    String dataValue=""; 
    String defaultDataValue="";
    String headerFieldDesc="";
    String parentHeaderField="";
    int parentHeaderOrder =0;
    
    
    public String getDataValue() {
        return dataValue;
    }
    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }
    public int getParentHeaderOrder() {
        return parentHeaderOrder;
    }
    public void setParentHeaderOrder(int parentHeaderOrder) {
        this.parentHeaderOrder = parentHeaderOrder;
    }
    public String getParentHeaderField() {
        return parentHeaderField;
    }
    public void setParentHeaderField(String parentHeaderField) {
        this.parentHeaderField = parentHeaderField;
    }
    public String getHeaderId() {
        return headerId;
    }
    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }
    public int getHeaderOrder() {
        return headerOrder;
    }
    public void setHeaderOrder(int headerOrder) {
        this.headerOrder = headerOrder;
    }
    public int getHeaderDepth() {
        return headerDepth;
    }
    public void setHeaderDepth(int headerDepth) {
        this.headerDepth = headerDepth;
    }
    public String getHeaderField() {
        return headerField;
    }
    public void setHeaderField(String headerField) {
        this.headerField = headerField;
    }
    public String getHeaderFieldName() {
        return headerFieldName;
    }
    public void setHeaderFieldName(String headerFieldName) {
        this.headerFieldName = headerFieldName;
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
    public String getDataArrayField() {
        return dataArrayField;
    }
    public void setDataArrayField(String dataArrayField) {
        this.dataArrayField = dataArrayField;
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
    public String getHeaderFieldDesc() {
        return headerFieldDesc;
    }
    public void setHeaderFieldDesc(String headerFieldDesc) {
        this.headerFieldDesc = headerFieldDesc;
    } 
}
