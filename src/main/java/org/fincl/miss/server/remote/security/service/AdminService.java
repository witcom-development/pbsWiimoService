package org.fincl.miss.server.remote.security.service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    
    public List getLoginUserDetail(Map param);
    
    public List getAuthList(Map param);
    
}
