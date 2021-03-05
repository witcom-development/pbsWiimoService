package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class ServiceRegisterException extends CfoodException {
    
    private static final long serialVersionUID = 5667358350647221029L;
    
    public ServiceRegisterException(String code, Throwable e) {
        super(code, e);
    }
}
