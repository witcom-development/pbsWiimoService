/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl
 * @파일명          : AutoOverFeePayServiceImpl.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl;

import java.sql.SQLException;
import java.util.List;

import org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayMapper;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @파일명          : AutoOverFeePayServiceImpl.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Service(value="autoOverFeePayService")
public class AutoOverFeePayServiceImpl implements AutoOverFeePayService {
	@Autowired
	private AutoOverFeePayMapper autoOverFeePayMapper;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * @location   : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl.AutoOverFeePayService.getOverFeeProcTarget
	 * @writeDay   : 2015. 8. 31. 오전 11:45:54
	 * @overridden : @see org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService#getOverFeeProcTarget()
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=true)
	public List<OverFeeVO> getOverFeeProcTarget() {
		// TODO Auto-generated method stub
		return autoOverFeePayMapper.getOverFeeProcTarget();
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl.AutoOverFeePayService.setOverFeePayComplete
	 * @writeDay   : 2015. 8. 31. 오후 6:18:15
	 * @overridden : @see org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService#setOverFeePayComplete(org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false, propagation= Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int setOverFeePayComplete(OverFeeVO fee) {
		logger.debug("******setOverFeePayComplete*****");
		return autoOverFeePayMapper.setOverFeePayComplete(fee);
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl.AutoOverFeePayService.addTicketPayment
	 * @writeDay   : 2015. 9. 9. 오후 5:17:48
	 * @overridden : @see org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService#addTicketPayment(org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false, propagation= Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int addTicketPayment(OverFeeVO fee) {
		// TODO Auto-generated method stub
		return autoOverFeePayMapper.addTicketPayment(fee);
	}

	/**
	 * @location   : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl.AutoOverFeePayService.addTicketPaymentFail
	 * @writeDay   : 2015. 9. 9. 오후 5:17:48
	 * @overridden : @see org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService#addTicketPaymentFail(org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO)
	 * @Todo       :
	 */ 
	@Override
	@Transactional(readOnly=false, propagation= Propagation.REQUIRED, rollbackFor={Exception.class, SQLException.class})
	public int addTicketPaymentFail(OverFeeVO fee) {
		// TODO Auto-generated method stub
		logger.debug("AAAAAA");
		return autoOverFeePayMapper.addTicketPaymentFail(fee);
	}
	
	public java.util.Map<String, String> getPaymentInfoExist(OverFeeVO fee){
		return autoOverFeePayMapper.getPaymentInfoExist(fee);
	}
}
