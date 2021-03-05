package org.fincl.miss.server.scheduler;

import java.util.List;
import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.blackUsr.BlackListMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlackListScheduler {

	@Autowired
	private BlackListMapper blackListMapper;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="exeBlackListAutoRemove")
	public void exeBlackListAutoRemove() {
		
		logger.debug("###############블랙리스트 해지자동 배치 start##################");
		List<String> blackTargetList = blackListMapper.getBlackTargetList();
		
		if(blackTargetList.size() > 0) {
			logger.debug("###############aaaa블랙리스트aaa##################");
			logger.debug("#########"+blackTargetList.size() +"####");
			for(String usrSeq : blackTargetList) {
				logger.debug("###############"+usrSeq+"#################");
				blackListMapper.delBlackList(usrSeq);
				blackListMapper.setPenaltyStatus(usrSeq);
				
			}
			
		}
		
	}
}
