/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler
 * @파일명          : GuestRemoveInfoScheduler.java
 * @작성일          : 2015. 8. 27.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 27.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import java.sql.SQLException;
import java.util.List;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.guest.GuestRemoveInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @파일명          : GuestRemoveInfoScheduler.java
 * @작성일          : 2015. 8. 27.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 27.   |   ymshin   |  최초작성
 */
@Component
public class GuestRemoveInfoScheduler {

	@Autowired
	private GuestRemoveInfoMapper guestRemoveInfoMapper;
	
	
	@ClusterSynchronized( jobToken="guestExpiredUseProc")
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public void guestExpiredUseProc() {
		
		List<String> chk1 = guestRemoveInfoMapper.chkGuestVoucherStataus();
		if(chk1.size() > 0) {
			for(String seq : chk1){
				boolean chk2 = guestRemoveInfoMapper.chkGuestOverFeeStatus(seq) > 0 ? false : true;
				if(chk2) {
				    guestRemoveInfoMapper.setGuestRemoveInfo(seq);
				}
			}
		  
		}
		
		
	}
	
	
}
