package org.fincl.miss.server.message.parser.telegram.tluser.vo;


public class TlUserField {
    public static enum Type {
        HEADER, IO
    };

    private String id; //jq grid 사용 
    private String ioId; //상위 IO, Header ID
    private int depth;
    private int order;   //순번
    private int arrayOrder; // 배열 순번
    private String parentField;//따르는 배열 이름 
    private String field;
    private String fieldName;   
    private String dataType;
    private int length;  
    private int scale;  
    private String data;
    private String type;
    private String dataArrayField;
    

    
      
    
    public int getDepth() {
        return depth;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }
    public String getParentField() {
        return parentField;
    }
    public void setParentField(String parentField) {
        this.parentField = parentField;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIoId() {
        return ioId;
    }
    public void setIoId(String ioId) {
        this.ioId = ioId;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public int getArrayOrder() {
        return arrayOrder;
    }
    public void setArrayOrder(int arrayOrder) {
        this.arrayOrder = arrayOrder;
    }

    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getFieldName() {
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getScale() {
        return scale;
    }
    public void setScale(int scale) {
        this.scale = scale;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDataArrayField() {
        return dataArrayField;
    }
    public void setDataArrayField(String dataArrayField) {
        this.dataArrayField = dataArrayField;
    }
    
	
	
	
	
}
