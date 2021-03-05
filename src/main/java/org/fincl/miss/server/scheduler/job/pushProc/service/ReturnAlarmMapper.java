package org.fincl.miss.server.scheduler.job.pushProc.service;

import java.util.HashMap;
import java.util.List;

import org.fincl.miss.server.scheduler.job.pushProc.ReturnMsgVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

@Mapper("returnAlramMapper")
public interface ReturnAlarmMapper {

	List<ReturnMsgVO> getBikeReturnAlarm();
	public List<HashMap<String, String>> getBikeRentSend();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.pushProc.service.ReturnAlarmMapper.setReturnAlarmMsgComplete
	 * @writeDay : 2015. 8. 17. 오후 6:17:28
	 * @return   : int
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 17.   |   ymshin   |  최초작성
	 */ 
	int setReturnAlarmMsgComplete(ReturnMsgVO target);
	
	int setBikeRentSendComplete(String rentBikeID);


}
