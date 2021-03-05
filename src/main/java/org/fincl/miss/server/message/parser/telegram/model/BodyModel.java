package org.fincl.miss.server.message.parser.telegram.model;

import java.io.Serializable;

import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramBodyFactory;


/** 
 * - 메시지 개별부를 나타내는 모델 -
 * 
 */

public class BodyModel extends ChunkModel  implements Serializable{ 
    /**
     * 
     */
    private static final long serialVersionUID = 8107094143932235942L;
    private String mode = null;
    private String className = null;
    private int messageType = 0; // source_in source_out target_in target_out
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    
    public void initialize() {
    }
    
    @Override
    public void makeFieldModel() throws MessageParserException {
        // TODO Auto-generated method stub
        if(fieldModels == null) { 
            TelegramBodyFactory factory = TelegramBodyFactory.getInstance(this, messageType);
            fieldModels = factory.makeFileds(this,messageType);
        }
    }
    
    public String getKey() {
        // TODO Auto-generated method stub
        // BodyModel의 경우 키는 ID를 붙이지 않는다.
        return "";
    }
    public int getLength() {
        // TODO Auto-generated method stub
        return 0;
    }
    public Model getParent() {
        return this;
    }
    public int getMessageType() {
        return messageType;
    }
    /**
     * @param messageType
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
