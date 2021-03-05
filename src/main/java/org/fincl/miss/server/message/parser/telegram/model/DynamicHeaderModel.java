package org.fincl.miss.server.message.parser.telegram.model;

import java.io.Serializable;

import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramHeaderFactory;

 
public class DynamicHeaderModel extends ChunkModel implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7344332282433388920L;
    protected String cons_cd = null;    
    protected boolean useHeadModel = false;  
    
    
    public boolean isUseHeadModel() {
        return useHeadModel;
    }

    public void setUseHeadModel(boolean useHeadModel) {
        this.useHeadModel = useHeadModel;
    }
    public String getCons_cd() {
        return cons_cd;
    }

    public void setCons_cd(String cons_cd) {
        this.cons_cd = cons_cd;
    }

    @Override
    public void makeFieldModel() throws MessageParserException {
 
        // TODO Auto-generated method stub
        if(fieldModels == null) {  
            TelegramHeaderFactory factory = TelegramHeaderFactory.getInstance(this, 0);
            fieldModels = factory.makeFileds(this,0); 
        }
        
    }

    public String getKey() {
        // TODO Auto-generated method stub
        return id;
    }

    public int getLength() {
        // TODO Auto-generated method stub
        return 0;
    }
    public Model getParent() {
        return this;
    }
}
