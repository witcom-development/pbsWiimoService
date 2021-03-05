package org.fincl.miss.server.logging.db.service.impl;

import org.fincl.miss.server.logging.db.service.ServiceLog;
import org.fincl.miss.server.logging.db.service.ServiceLogMapper;
import org.fincl.miss.server.logging.db.service.ServiceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("serviceLogService")
public class ServiceLogServiceImpl implements ServiceLogService {
    
    @Autowired
    private ServiceLogMapper serviceLogMapper;
    
    @Override
    public void addServiceLog(ServiceLog serviceLog) {
        serviceLogMapper.addServiceLog(serviceLog);
    }
    
    @Override
    public void modifyServiceLog(ServiceLog serviceLog) {
        serviceLogMapper.modifyServiceLog(serviceLog);
    }
}
