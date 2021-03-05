package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class OutBoundSenderException extends CfoodException {
    
    private static final long serialVersionUID = 9174275672934138617L;
    
    public OutBoundSenderException(String code) {
        super(code);
    }
    
    public OutBoundSenderException(String code, String message) {
        super(code, message);
    }
    
    public OutBoundSenderException(String code, Throwable e) {
        super(code, e);
    }
    
    public OutBoundSenderException(String code, String message, Throwable e) {
        super(code, message, e);
    }
}
