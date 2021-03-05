package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class ServiceHandlerException extends CfoodException {
    
    private static final long serialVersionUID = 3238974118826156223L;
    
    public ServiceHandlerException(String code, Throwable e) {
        super(code, e);
    }
}
