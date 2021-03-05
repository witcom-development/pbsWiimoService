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
package org.fincl.miss.server.scheduler.job.serialNoProc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.period.vo.PeriodTenMinuteErrorVO;
import org.fincl.miss.server.scheduler.job.recommend.service.vo.RecommendWeeklyVO;

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
@Mapper(value="serialNoProcMapper")
public interface SerialNoProcMapper {
	
	public List<Map<String, String>> getSerialNoMemberInfo();
   
}
