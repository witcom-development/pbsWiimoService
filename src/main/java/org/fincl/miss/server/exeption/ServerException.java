package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class ServerException extends CfoodException {
    
    private static final long serialVersionUID = 9174275672934138617L;
    
    private byte[] parserMessage;
    
    public byte[] getParserMessage() {
        return parserMessage;
    }
    
    public void setParserMessage(byte[] parserMessage) {
        this.parserMessage = parserMessage;
    }
    
    public ServerException(String code, byte[] parserMessage) {
        super(code);
        this.parserMessage = parserMessage;
    }
    
    public ServerException(String code) {
        super(code);
    }
    
    public ServerException(String code, String message) {
        super(code, message);
    }
    
    public ServerException(String code, Throwable e) {
        super(code, e);
    }
    
    public ServerException(String code, Throwable e, byte[] parserMessage) {
        super(code, e);
        this.parserMessage = parserMessage;
    }
    
    public ServerException(String code, String message, Throwable e) {
        super(code, message, e);
    }
}
