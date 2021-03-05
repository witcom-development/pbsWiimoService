/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service
 * @파일명          : AutoOverFeePayService.java
 * @작성일          : 2017. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.serialNoProc.service;

import java.net.URISyntaxException;
import java.util.List;

import org.json.JSONException;


public interface SerialNoProcService {
	void resetMemberInfoProc() throws URISyntaxException, JSONException;
	

}
