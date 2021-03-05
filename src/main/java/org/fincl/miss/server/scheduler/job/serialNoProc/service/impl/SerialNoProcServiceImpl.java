/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.Impl
 * @파일명          : AutoOverFeePayServiceImpl.java
 * @작성일          : 2017. 5. 22.
 * @작성자          : jhjeong
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2017. 5. 22.  |   jhjeong   |  최초작성
 */ 
package org.fincl.miss.server.scheduler.job.serialNoProc.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fincl.miss.server.scheduler.job.guest.GuestRemoveInfoMapper;
import org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper;
import org.fincl.miss.server.scheduler.job.serialNoProc.service.SerialNoProcMapper;
import org.fincl.miss.server.scheduler.job.serialNoProc.service.SerialNoProcService;
import org.fincl.miss.server.util.AES256anicar;
import org.fincl.miss.server.util.AesCtr;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service(value="serialNoProcService")
public class SerialNoProcServiceImpl implements SerialNoProcService {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private static String cancelUrl = "https://service.paygate.net/service/cancelAPI.json?callback=callback";
	
	@Autowired
	private SerialNoProcMapper serialNoProcMapper;
	
	@Autowired
	private GuestRemoveInfoMapper guestRemoveInfoMapper;
	
	@Autowired
	private UsrRefundDepositMapper usrRefundDepositMapper;
	
	
	@Override
	public void resetMemberInfoProc() throws URISyntaxException, JSONException{
		List<Map<String, String>> serialNoList = new ArrayList<Map<String, String>>();
		
		serialNoList = serialNoProcMapper.getSerialNoMemberInfo();
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		
		if(serialNoList != null){
			for(Map<String, String> map : serialNoList){
				
				// 결제취소 요청
				JSONObject jObject = paymentCancelInfoSet(restTemplate, map);
				

				String rMsg = jObject.getString("replyCode");
				
				if(rMsg != null){
					log.debug("##### rMsg ==> " + rMsg);
				}else{
					log.debug("##### rMsg is null #####");
				}
				
				map.put("resultCD", rMsg);
				map.put("processReasonDesc", jObject.getString("replyMessage"));
				
				
				if(rMsg.equals("0000")){
					// 결제정보 취소처리
					log.debug("##### 대여일련번호 Proc : 결제정보 취소처리 시작##### => " + String.valueOf(map.get("voucherSeq")));
					usrRefundDepositMapper.serialNoPaymentCancel(map);
					
					// 이용권 정보 초기화
					log.debug("##### 대여일련번호 Proc : 이용권 삭제 시작#####");
					usrRefundDepositMapper.setVoucherUseComplete(map);
					
					// 비회원 정보 초기화
					log.debug("##### 대여일련번호 Proc : 비회원 정보 초기화 시작#####");
					guestRemoveInfoMapper.setGuestRemoveInfo(String.valueOf(map.get("usrSeq")));
				}else{
					log.debug("##### 대여일련번호 Proc : 결제취소 비정상 처리 #####");
					usrRefundDepositMapper.addTicketPaymentFail(map);
				}

			}
		}	
		
	}


	/**
	 * @param restTemplate
	 * @param map
	 * @return
	 * @throws URISyntaxException
	 * @throws JSONException
	 */
	private JSONObject paymentCancelInfoSet(RestTemplate restTemplate,
			Map<String, String> map) throws URISyntaxException, JSONException {
		log.debug("##### 대여일련번호 Proc : 결제취소처리 시작 #####");
		String m_id = String.valueOf(map.get("m_id"));
		String t_id = String.valueOf(map.get("tid"));
		log.debug("##### t_id ##### => " + t_id);
		String key256 = AES256anicar.sha256(String.valueOf(map.get("spiSecretKey")));
		String tidEncrypted = "AES256" + new AesCtr().encrypt(t_id, key256, 256);
		String cUrl = cancelUrl + "&mid=" + m_id + "&tid=" + tidEncrypted + "&amount=F";
		cUrl = cUrl.replace("\\s", "");
		log.debug("##### 대여일련번호 Proc : 취소 URL ##### ==> " + cUrl);
		
		URI url = new URI(cUrl);
		String result = restTemplate.postForObject(url, null, String.class);
		log.debug("##### 대여일련번호 Proc : 취소 result ##### ==> " + result);
		String resultAll = result.substring(result.indexOf("(")+1, result.length()-1);
		log.debug("**************["+resultAll+"]**********************");
		
		
		JSONObject jObject = new JSONObject(resultAll);
		return jObject;
	}

}
