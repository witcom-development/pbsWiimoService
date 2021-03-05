package org.fincl.miss.server.exeption;

import com.dkitec.cfood.core.CfoodException;

public class BizServiceException extends CfoodException {
    
    private static final long serialVersionUID = 1739341852097948614L;
    
    public BizServiceException(String code) {
        super(code);
    }
    
    public BizServiceException(String code, String message) {
        super(code, message);
    }
    
    public BizServiceException(String code, Throwable e) {
        super(code, e);
    }
    
    public BizServiceException(String code, String message, Throwable e) {
        super(code, message, e);
    }
}
