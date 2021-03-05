package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class ServiceInvokeException extends CfoodException {
    
    private static final long serialVersionUID = -1935619040848953770L;
    
    public ServiceInvokeException(String code, Throwable e) {
        super(code, e);
    }
}
