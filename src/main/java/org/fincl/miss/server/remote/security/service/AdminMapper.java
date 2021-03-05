package org.fincl.miss.server.remote.security.service;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("AdminMapper")
public interface AdminMapper {
    
    public List getLoginUserDetail(Map param);
    
    public List getAuthList(Map param);
    
}
