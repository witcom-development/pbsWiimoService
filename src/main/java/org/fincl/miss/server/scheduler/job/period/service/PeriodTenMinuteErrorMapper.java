/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.app.rent.bookingCancel.service
 * @파일명          : BookingCancel.java
 * @작성일          : 2015. 7. 15.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 15.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.period.service;

import java.util.List;

import org.fincl.miss.server.scheduler.job.period.vo.PeriodTenMinuteErrorVO;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : PeriodTenMinuteErrorMapper.java
 * @작성일          : 2015. 7. 15.
 * @작성자          : JJH
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |         수정내용
 * -------------------------------------------------------------
 *   2016. 11. 18   |        JJH       |         최초작성
 */
@Mapper(value="PeriodTenMinuteErrorMapper")
public interface PeriodTenMinuteErrorMapper {
	public List<PeriodTenMinuteErrorVO> getPeriodTenMinuteErrorList();		// 10분동안 주기적인 상태보고 안한 자전거 이력 가져오기_20161118_JJH
	
	public int insertPeriodTenMinuteErrorList(PeriodTenMinuteErrorVO periodtenMinuteErrorVo); 	// 10분동안 주기적인 상태보고 안한 자전거 이력 등록하기_20161118_JJH
   
}
