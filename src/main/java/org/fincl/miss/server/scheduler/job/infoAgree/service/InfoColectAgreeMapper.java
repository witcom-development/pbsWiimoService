/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.infoAgree.service
 * @파일명          : InfoColectAgreeMapper.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.infoAgree.service;

import java.util.HashMap;
import java.util.List;

import egovframework.rte.psl.dataaccess.mapper.Mapper;

/**
 * @파일명          : InfoColectAgreeMapper.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Mapper("infoAgreeMapper")
public interface InfoColectAgreeMapper {

	/**
	 * @location : org.fincl.miss.server.scheduler.job.infoAgree.service.InfoColectAgreeMapper.getOverFeeProcTarget
	 * @writeDay : 2015. 8. 31. 오후 2:38:18
	 * @return   : List<HashMap<String,String>>
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 8. 31.   |   ymshin   |  최초작성
	 */ 
	List<HashMap<String, String>> getOverFeeProcTarget();

	/**
	 * @location : org.fincl.miss.server.scheduler.job.infoAgree.service.InfoColectAgreeMapper.getInfoColectMailId
	 * @writeDay : 2015. 9. 4. 오후 10:58:29
	 * @return   : String
	 * @Todo     :
	 * -------------------------------------------------------------
	 *      수정일      |      수정자      |              수정내용
	 * -------------------------------------------------------------
	 *    2015. 9. 4.   |   ymshin   |  최초작성
	 */ 
	String getInfoColectMailId();

}
