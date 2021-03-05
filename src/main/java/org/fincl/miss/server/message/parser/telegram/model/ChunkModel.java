package org.fincl.miss.server.message.parser.telegram.model;

import java.util.ArrayList;
import java.util.List;

import org.fincl.miss.server.exeption.MessageParserException;

 
/** 
 * - 메시지 전문 구조를 나타내는 모델 - 
 */

public abstract class ChunkModel implements Model   {

    
    
    protected String id = null;
    protected String name = null;
//    protected String src = null;
    protected ChunkModel next = null;
    protected String length_field_id = null;
    protected int length_gap = 0;
    protected String delim = null;
    protected String version = null;
    protected String writerName = null;

//    protected String txType = null;
//    protected String txSystem = null;
 
    
//    public String getTxType() {
//        return txType;
//    }
//
//    public void setTxType(String txType) {
//        this.txType = txType;
//    }
//
//    public String getTxSystem() {
//        return txSystem;
//    }
//
//    public void setTxSystem(String txSystem) {
//        this.txSystem = txSystem;
//    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDelim() {
        return delim;
    }

    public void setDelim(String delim) {
        this.delim = delim;
    }

    public void setLength_gap(int length_gap) {
        this.length_gap = length_gap;
    }

    public void reload() {
        fieldModels = null;
    }

    public int getLength_gap() {
        return length_gap;
    }

    public void setLength_gap(String length_gap) {
        if (length_gap == null) length_gap = "0";
        this.length_gap = Integer.parseInt(length_gap);
    }

    public String getLength_field_id() throws MessageParserException {
        /*
         * if(length_field_id == null) { fieldModels = null; makeFieldModel(); }
         */
        if( (length_field_id == null || "".equals(length_field_id))&& fieldModels == null) {
            makeFieldModel();
        }
        return length_field_id;
    }

    public void setLength_field_id(String length_field_id) {
        this.length_field_id = length_field_id;
    }

    protected List<FieldModel> fieldModels = null;

    public abstract void makeFieldModel() throws MessageParserException;

    public String getType() {
        return null;
    }

    public List<FieldModel> getFieldModels() throws MessageParserException {
        if (fieldModels == null) makeFieldModel();
        return fieldModels;
    }

    public void setFieldModels(ArrayList<FieldModel> fieldModels) {
        this.fieldModels = fieldModels;
    }

    public FieldModel getFieldModel(String fieldmodel_id) throws MessageParserException {
        List<FieldModel> field_models = getFieldModels();
        for (int i = 0; i < field_models.size(); i++) {
            FieldModel fieldModel = field_models.get(i);
            if (fieldModel.getId().equals(fieldmodel_id)) return fieldModel;
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getSrc() {
//        return src;
//    }
//
//    public void setSrc(String src) {
//        this.src = src;
//    }

    public ChunkModel getNext() {
        return next;
    }

    public void setNext(ChunkModel next) {
        this.next = next;
    }
}

