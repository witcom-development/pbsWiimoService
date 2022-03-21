/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.bikeError.Impl
 * @파일명          : BikeBatteryErrorServiceImpl.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.bikeError.Impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorMapper;
import org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @파일명          : BikeBatteryErrorServiceImpl.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Service(value="bikeBatteryErrorService")
public class BikeBatteryErrorServiceImpl implements BikeBatteryErrorService {
	
	@Autowired
	private BikeBatteryErrorMapper bikeBatteryErrorMapper;

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.bikeError.Impl.BikeBatteryErrorService.getBikeBatteryErrorList
	 * @writeDay   : 2015. 8. 31. 오후 6:20:48
	 * @overridden : @see org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService#getBikeBatteryErrorList()
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=true)
	public List<String> getBikeBatteryErrorList() {
		// TODO Auto-generated method stub
		return bikeBatteryErrorMapper.getBikeBatteryErrorList();
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.bikeError.Impl.BikeBatteryErrorService.initBatteryCnt
	 * @writeDay   : 2015. 8. 31. 오후 6:42:00
	 * @overridden : @see org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService#initBatteryCnt(java.util.Map)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int initBatteryCnt(Map<String, String> pMap) {
		return bikeBatteryErrorMapper.initBatteryCnt(pMap);
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.bikeError.Impl.BikeBatteryErrorService.chkExistMTCFaultInfo
	 * @writeDay   : 2015. 9. 1. 오전 9:43:36
	 * @overridden : @see org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService#chkExistMTCFaultInfo(java.util.Map)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=true)
	public String chkExistMTCFaultInfo(Map<String, String> pMap) {
		// TODO Auto-generated method stub
		return bikeBatteryErrorMapper.chkExistMTCFaultInfo(pMap);
	}
	

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.bikeError.Impl.BikeBatteryErrorService.setBikeErrorProc
	 * @writeDay   : 2015. 9. 1. 오전 9:43:36
	 * @overridden : @see org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService#setBikeErrorProc(java.util.Map)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int setBikeErrorProc(Map<String, String> pMap) {
		int result = bikeBatteryErrorMapper.addDeviceErrFaultDetail(pMap);
		if(pMap.get("clsCd").equalsIgnoreCase("ERB_003") 
				|| (pMap.get("errorType").equals("new") && ( pMap.get("clsCd").equalsIgnoreCase("ERB_001") || pMap.get("clsCd").equalsIgnoreCase("ERB_004") ||  pMap.get("clsCd").equalsIgnoreCase("ERB_007")))) {
			result = bikeBatteryErrorMapper.initBatteryCnt(pMap);
		}
		return result;
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.bikeError.Impl.BikeBatteryErrorService.addNewBikeErrorProc
	 * @writeDay   : 2015. 9. 1. 오전 9:43:36
	 * @overridden : @see org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService#addNewBikeErrorProc(java.util.Map)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int addNewBikeErrorProc(Map<String, String> pMap) {
		return bikeBatteryErrorMapper.addDeviceErrReport(pMap);
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.bikeError.Impl.BikeBatteryErrorService.getIOTBikeErrorList
	 * @writeDay   : 2015. 9. 1. 오후 1:35:09
	 * @overridden : @see org.fincl.miss.server.scheduler.job.bikeError.BikeBatteryErrorService#getIOTBikeErrorList()
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=true)
	public List<HashMap<String, String>> getIOTBikeErrorList() {
		// TODO Auto-generated method stub
		return bikeBatteryErrorMapper.getIOTBikeErrorList();
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public List<HashMap<String, String>> getIOTBikeGPSErrorList() {
		// TODO Auto-generated method stub
		return bikeBatteryErrorMapper.getIOTBikeGPSErrorList();
	}
	
	
}
