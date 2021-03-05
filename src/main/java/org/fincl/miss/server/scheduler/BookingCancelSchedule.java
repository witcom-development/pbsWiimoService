/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.app.rent.bookingCancel.schedule
 * @파일명          : BookingCancelService.java
 * @작성일          : 2015. 7. 15.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 15.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.bookingCancel.service.BookingCancelMapper;
import org.fincl.miss.server.scheduler.job.bookingCancel.vo.BookingCancelVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @파일명          : BookingCancelService.java
 * @작성일          : 2015. 7. 15.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 15.   |   ymshin   |  최초작성
 */
@Component
public class BookingCancelSchedule {

	@Autowired
	private BookingCancelMapper bookingCancelMapper;
	
	@ClusterSynchronized( jobToken="bookingRentScheduler")
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public void bookingRentScheduler() {
		bookingCancelMapper.setOverTimeBookingRack();
	}
	@ClusterSynchronized( jobToken="bookingRentCancelScheduler")
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public void bookingRentCancelScheduler() {
		List<BookingCancelVO> cancelList = bookingCancelMapper.getBookingCancelMsgList();
		int result = 0;
		if(cancelList.size() > 0) {
			result = bookingCancelMapper.setOverTimeRentRack();
			result = bookingCancelMapper.delOverTimeBookingRent();
			/*if(result > 0){
				List<SmsMessageVO> msgList = new ArrayList<SmsMessageVO>();
				SmsMessageVO msgVo = null;
				for(BookingCancelVO cancel : cancelList) {
					msgVo = new SmsMessageVO();
					msgVo.setDestno(cancel.getUsrMpnNo().replace("-",""));
					msgVo.setMsg(SendType.SMS_009, cancel.getStationName(), cancel.getStationEquipOrder());
					msgList.add(msgVo);
				}
				SmsSender.sender(msgList);
			}
			*/
		}
		
	}
}
