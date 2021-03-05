/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.serialNoProc
 * @파일명          : SerialNoProcScheduler.java
 * @작성일          : 2017. 5. 19.
 * @작성자          : jhjeong
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |        수정내용
 * -------------------------------------------------------------
 *    2017. 5. 19.  |      jhjeong     |        최초작성
 */ 

package org.fincl.miss.server.scheduler;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.serialNoProc.service.SerialNoProcService;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SerialNoProcScheduler {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name="serialNoProcService")
	private SerialNoProcService serialNoProcService;
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="resetMemberInfoProc")
	public void resetMemberInfoProc() throws JSONException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException{
		log.debug("##### 대여일련번호 회원정보 Re-set Proc 시작 #####");
		
		serialNoProcService.resetMemberInfoProc();
		
		log.debug("##### 대여일련번호 회원정보 Re-set Proc 종료 #####");
	}

}
