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
package org.fincl.miss.server.scheduler.job.period.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayMapper;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.server.scheduler.job.period.service.PeriodTenMinuteErrorMapper;
import org.fincl.miss.server.scheduler.job.period.service.PeriodTenMinuteErrorService;
import org.fincl.miss.server.scheduler.job.period.vo.PeriodTenMinuteErrorVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service(value="periodTenMinuteErrorService")
public class PeriodTenMinuteErrorServiceImpl implements PeriodTenMinuteErrorService {
	
	@Autowired
	private PeriodTenMinuteErrorMapper periodTenMinuteErrorMapper;
	
	@Override
	@Transactional(readOnly=true)
	public List<PeriodTenMinuteErrorVO> getPeriodTenMinuteErrorList(){
		return periodTenMinuteErrorMapper.getPeriodTenMinuteErrorList();
	}
	
	@Override
	@Transactional(readOnly=true)
	public int insertPeriodTenMinuteErrorList(PeriodTenMinuteErrorVO periodTenMinuteErrorVO){
		return periodTenMinuteErrorMapper.insertPeriodTenMinuteErrorList(periodTenMinuteErrorVO);
	}
}
