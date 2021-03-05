package org.fincl.miss.server.exeption;

import org.fincl.miss.server.channel.inbound.InBoundServerHandler;

import com.dkitec.cfood.core.CfoodException;

public class InBoundServletException extends CfoodException {
    
    private static final long serialVersionUID = 9174275672934138617L;
    
    private InBoundServerHandler inBoundServerHandler;
    
    private byte[] parserMessage;
    
    public byte[] getParserMessage() {
        return parserMessage;
    }
    
    public void setParserMessage(byte[] parserMessage) {
        this.parserMessage = parserMessage;
    }
    
    public InBoundServerHandler getInBoundServerHandler() {
        return inBoundServerHandler;
    }
    
    public void setInBoundServerHandler(InBoundServerHandler inBoundServerHandler) {
        this.inBoundServerHandler = inBoundServerHandler;
    }
    
    public InBoundServletException(InBoundServerHandler inBoundServerHandler, String code, Throwable e, byte[] parserMessage) {
        super(code, e);
        this.inBoundServerHandler = inBoundServerHandler;
        this.parserMessage = parserMessage;
    }
    
    public InBoundServletException(InBoundServerHandler inBoundServerHandler, String code, byte[] parserMessage) {
        super(code);
        this.inBoundServerHandler = inBoundServerHandler;
        this.parserMessage = parserMessage;
    }
    
    public InBoundServletException(String code) {
        super(code);
    }
    
    public InBoundServletException(String code, byte[] parserMessage) {
        super(code);
        this.parserMessage = parserMessage;
    }
    
    public InBoundServletException(String code, String message) {
        super(code, message);
    }
    
    public InBoundServletException(String code, Throwable e) {
        super(code, e);
    }
    
    public InBoundServletException(String code, String message, Throwable e) {
        super(code, message, e);
    }
}
