package org.fincl.miss.server.message.parser.telegram.model;

import java.util.List;

/** 
 * - 전문 필드를 나타내는 클래스 -
 * 
 */

public abstract class FieldModel implements Model{
    protected String teleType = null;//Header/Body구분
    
    protected String headerId = null;//headerID

    protected String id = null;
    protected String name = null;
    protected Model parent = null;
    protected String delim = null;
    
    protected String essentialYn = null;
    
    
    public String getEssentialYn() {
        return essentialYn;
    }

    public void setEssentialYn(String essentialYn) {
        this.essentialYn = essentialYn;
    }
 

    public String getHeaderId() {
        return headerId;
    }

    public void setHeaderId(String headerId) {
        this.headerId = headerId;
    }

    public String getTeleType() {
        return teleType;
    }

    public void setTeleType(String teleType) {
        this.teleType = teleType;
    }

    public String getDelim() {
        return delim;
    }
    
    public void setDelim(String delim) {
        this.delim = delim;
    }
    
    public Model getParent() {
        return parent;
    }
    public void setParent(Model parent) {
        this.parent = parent;
    }
    public abstract boolean isGroup();
    public abstract List<FieldModel> getFieldModelList();
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

