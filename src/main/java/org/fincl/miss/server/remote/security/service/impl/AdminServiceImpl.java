package org.fincl.miss.server.remote.security.service.impl;

import java.util.List;
import java.util.Map;

import org.fincl.miss.server.remote.security.service.AdminMapper;
import org.fincl.miss.server.remote.security.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

@Service("adminService")
public class AdminServiceImpl extends EgovAbstractServiceImpl implements AdminService {
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Override
    public List getLoginUserDetail(Map param) {
        return adminMapper.getLoginUserDetail(param);
    }
    
    @Override
    public List getAuthList(Map param) {
        return adminMapper.getAuthList(param);
    }
    
}
