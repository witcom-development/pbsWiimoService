package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class MessageHandlerException extends CfoodException {
    
    private static final long serialVersionUID = -2999220392024600991L;
    
    public MessageHandlerException(String code, Throwable e) {
        super(code, e);
    }
}
