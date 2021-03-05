package org.fincl.miss.server.message.parser.telegram.model;

import java.util.ArrayList;
import java.util.List;

/** 
 * 
 * - 전문 필드를 나타내는 클래스 - 
 */

public class FieldGroupModel extends FieldModel {

    protected boolean isFixedLength = false;
    protected int length = 0;
    protected int max = 0;
    protected String length_filed_id = null;
    protected List<FieldModel> fieldModelList = null;
    
    public FieldGroupModel() {
    }
    
    public void addFieldModel(FieldModel child) {
        if(fieldModelList == null)
            fieldModelList = new ArrayList<FieldModel>();
        fieldModelList.add(child);
    }       
    
    public List<FieldModel> getFieldModelList() {
        return fieldModelList;
    }

    public void setFieldModelList(List<FieldModel> fieldModelList) {
        this.fieldModelList = fieldModelList;
    }

    public boolean isFixedLength() {
        return isFixedLength;
    }
    public void setFixedLength(boolean isFixedLength) {
        this.isFixedLength = isFixedLength;
    }
    public int getLength() {
        return length;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public int getMax() {
        return max;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public String getLength_filed_id() {
        return length_filed_id;
    }
    public void setLength_filed_id(String length_filed_id) {
        this.length_filed_id = length_filed_id;
    }

    @Override
    public boolean isGroup() {
        // TODO Auto-generated method stub
        return true;
    }

    public String getKey() {
        // TODO Auto-generated method stub
        Model parent = getParent();
        if(parent !=null && parent.getKey() != null && !"".equals(parent.getKey()))
            return parent.getKey()+"."+id;
        else
            return id;
    }

    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }
}

