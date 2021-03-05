package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class ChannelControlException extends CfoodException {
    
    private static final long serialVersionUID = 3455760505570314323L;
    
    public ChannelControlException(String code) {
        super(code);
    }
    
    public ChannelControlException(String code, String message, Throwable e) {
        super(code, message, e);
    }
}
