package org.fincl.miss.server.logging.db.service;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("ServiceLogMapper")
public interface ServiceLogMapper {
    public void addServiceLog(ServiceLog serviceLog);
    
    public void modifyServiceLog(ServiceLog serviceLog);
}
