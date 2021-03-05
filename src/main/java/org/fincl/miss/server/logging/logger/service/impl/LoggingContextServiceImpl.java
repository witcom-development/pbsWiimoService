package org.fincl.miss.server.logging.logger.service.impl;

import java.util.Date;

import org.apache.logging.log4j.ThreadContext;
import org.fincl.miss.core.logging.UserLog;
import org.fincl.miss.server.logging.logger.service.LoggingContextService;
import org.fincl.miss.server.logging.logger.service.UserLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("loggingContextService")
public class LoggingContextServiceImpl implements LoggingContextService {
    
    @Autowired
    private UserLogService userLogService;
    
    @Override
    public void setContext(String serviceId, String clientId) {
        ThreadContext.put("server.service", serviceId);
        
        // clientId = "testId33"; // 테스트
        if (clientId != null && userLogService.containsKey(clientId)) {
            UserLog vo = userLogService.get(clientId);
            Date endDate = vo.getEndDate();
            
            // 시간이 지났으면
            if (endDate.compareTo(new Date()) < 0) {
                userLogService.remove(clientId);
                
                // 종료일시가 아직 지나지 않았을 경우 - 로그에 반영
            }
            else {
                ThreadContext.put("server.client", clientId);
            }
        }
    }
    
}
