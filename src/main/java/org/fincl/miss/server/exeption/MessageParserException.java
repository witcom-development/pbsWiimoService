package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class MessageParserException extends CfoodException {
    
    private static final long serialVersionUID = 2739863216896881137L;
    
    public MessageParserException(String code) {
        super(code);
    }
    
    public MessageParserException(String code, String msg) {
        super(code, msg);
    }
    
    public MessageParserException(String code, Throwable e) {
        super(code, e);
    }
    
    public MessageParserException(String code, String msg, Throwable e) {
        super(code, msg, e);
    }
}
