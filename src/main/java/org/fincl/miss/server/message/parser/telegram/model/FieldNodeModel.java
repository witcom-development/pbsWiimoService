package org.fincl.miss.server.message.parser.telegram.model;

import java.util.List;

import org.fincl.miss.server.message.parser.telegram.function.model.FunctionModel;

/** 
 * - 전문의 필드를 나타내는 모델 -
 * 
 */

public class FieldNodeModel extends FieldModel {

    protected String type = null;
    protected int len = 0;      // 정수 자리수
    protected int frac_len = 0; // 소수점 자리수
    protected String init_value = null;
    protected String reg_attr = null;
    protected String res_attr = null;
    protected FunctionModel function = null;
    
    public int getFrac_len() {
        return frac_len;
    }
    public void setFrac_len(int frac_len) {
        this.frac_len = frac_len;
    }
    public String getType() {
        return type.toUpperCase();
    }
    public void setType(String type) {
 
        this.type = type;
    }
    public int getLength() {
        /*
        int sum = 0;
        if(frac_len > 0)
            sum = len + frac_len + 1;
        else
            sum = len;
        */
        return len;
    }
    public void setLength(int len) {
        this.len = len;
    }
    public String getInit_value() {
        return init_value;
    }
    public void setInit_value(String init_value) {
        this.init_value = init_value;
    }
    public String getReg_attr() {
        return reg_attr;
    }
    public void setReg_attr(String reg_attr) {
        this.reg_attr = reg_attr;
    }
    public String getRes_attr() {
        return res_attr;
    }
    public void setRes_attr(String res_attr) {
        this.res_attr = res_attr;
    }
    public FunctionModel getFunction() {
        return function;
    }
    public void setFunction(FunctionModel function) {
        this.function = function;
    }
    @Override
    public List<FieldModel> getFieldModelList() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isGroup() {
        // TODO Auto-generated method stub
        return false;
    }
    public String getKey() {
        // TODO Auto-generated method stub
        Model parent = getParent();
        if(parent != null && parent.getKey() != null && !"".equals(parent.getKey()))
            return parent.getKey()+"."+id;
        else
            return id;
    }
}

