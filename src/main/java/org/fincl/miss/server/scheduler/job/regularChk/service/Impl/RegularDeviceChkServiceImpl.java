/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.regularChk.service.Impl
 * @파일명          : RegularDeviceChkServiceImpl.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.regularChk.service.Impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper;
import org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @파일명          : RegularDeviceChkServiceImpl.java
 * @작성일          : 2015. 9. 1.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 1.   |   ymshin   |  최초작성
 */
@Service(value="regularDeviceChkService")
public class RegularDeviceChkServiceImpl implements RegularDeviceChkSerivce {

	@Autowired
	private BikeBatteryErrorMapper bikeBatteryErrorMapper;

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.regularChk.service.Impl.RegularDeviceChkSerivce.getTargetDeviceList
	 * @writeDay   : 2015. 9. 1. 오후 9:46:23
	 * @overridden : @see org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce#getTargetDeviceList(java.lang.String)
	 * @Todo       : 해당 데이터를 취득.
	 */ 
	@Override
	public List<String> getTargetDeviceList(String devType) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("DEV_TYPE", devType);
		return bikeBatteryErrorMapper.getTargetDeviceList(map);
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.regularChk.service.Impl.RegularDeviceChkSerivce.setAPErrorProc
	 * @writeDay   : 2015. 9. 3. 오전 10:12:03
	 * @overridden : @see org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce#setAPErrorProc(java.util.Map)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor={Exception.class, SQLException.class} )
	public int setAPErrorProc(Map<String, String> pMap) {
		int result = bikeBatteryErrorMapper.addDeviceErrFaultDetail(pMap);
		if(pMap.get("errorType").equals("new")) {
			result = bikeBatteryErrorMapper.setAPErrorProc(pMap);
		}
		return result;
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.regularChk.service.Impl.RegularDeviceChkSerivce.setRackErrorProc
	 * @writeDay   : 2015. 9. 3. 오전 10:12:03
	 * @overridden : @see org.fincl.miss.server.scheduler.job.regularChk.service.RegularDeviceChkSerivce#setRackErrorProc(java.util.Map)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor={Exception.class, SQLException.class} )
	public int setRackErrorProc(Map<String, String> pMap) {
		int result = bikeBatteryErrorMapper.addDeviceErrFaultDetail(pMap);
		if(pMap.get("errorType").equals("new")) {
			result = bikeBatteryErrorMapper.setRackErrorProc(pMap);
		}
		return result;
	}
}
