/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler
 * @파일명          : InfoColectAgreeScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.infoAgree.service.InfoColectAgreeMapper;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @파일명          : InfoColectAgreeScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Component
public class InfoColectAgreeScheduler {
	@Autowired
	private InfoColectAgreeMapper infoAgreeMapper;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@ClusterSynchronized( jobToken="infoColectConfirmBatch")
	public void infoColectConfirmBatch() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
		logger.debug("******************************************[사용자 재동의 메일 발송 배치]*********************************************  ");
		/*
	     *  fix for
	     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	     *       sun.security.validator.ValidatorException:
	     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
	     *               unable to find valid certification path to requested target
	     */
	    TrustManager[] trustAllCerts = new TrustManager[] {
	       new X509TrustManager() {
	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	            return null;
	          }
	          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
	          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

	       }
	    };
	    SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	    // Create all-trusting host name verifier
	    HostnameVerifier allHostsValid = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	          return true;
	        }
	    };
	    // Install the all-trusting host verifier
	    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		
		List<HashMap<String, String>> targetList = infoAgreeMapper.getOverFeeProcTarget();
		Map<String, String> pMap = null;
		String mailId = infoAgreeMapper.getInfoColectMailId();
		if(targetList != null) {
			logger.debug("******************************************대상수*******************************************  ");
			logger.debug("***************************************"+targetList.size()+"******************************  ");
			logger.debug("******************************************대상수*******************************************  ");
			SmsMessageVO msgVo = null;
			for(HashMap<String, String> target : targetList) {
				//shade718%40naver.com&m_memo1=";
				//재동의 sms전송.
				if(target.get("mpnNo") != null && !target.get("mpnNo").equals("")) {
					msgVo = new SmsMessageVO();
					msgVo.setMsg(SendType.SMS_013);
					msgVo.setDestno(target.get("mpnNo"));
					SmsSender.sender(msgVo);
				}
				//email전송
				if( target.get("mbEmailName") != null) {
					String mailUrl = "http://www.bizmailer.co.kr/bizsmart/action/auto.do?biz_id=seoulbike2015&auth_key="+mailId
							+"&m_email="+target.get("mbEmailName").trim()
							+"&m_memo1="+target.get("mbId").trim();
					URI tuRL = new URI(mailUrl);
					
					logger.debug("*****************************************"+mailUrl+"******************************************  ");
					ResponseEntity<String> res = restTemplate.getForEntity(tuRL, String.class);
					HttpStatus httpStatus =  res.getStatusCode();
					int statusCd = httpStatus.value();
					logger.debug("*************메일 응답코드 리턴"+statusCd+"********************************");
				}
			}
		}
	}
}
