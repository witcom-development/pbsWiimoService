package org.fincl.miss.server.scheduler;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.tmoney.TmoneyResultChkMapper;
import org.fincl.miss.server.scheduler.job.tmoney.vo.TmoneyStatusVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TmoneyResultChkScheduler {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TmoneyResultChkMapper tomResultChkMapper;
	
	private static String STATUS_CHK_URL ="http://pay.t-monet.com/TransPayCheck.action?cp_id=MK110269";
	private static String CANCEL_REQ_URL ="http://t-town.co.kr:8880/TransPayCancel.action?type=a";
	private RestTemplate restTemplate;
	private RestTemplate cancelTemplate;
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="procTmoneyResultStatusChk")
	public void procTmoneyResultStatusChk() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
	    /*
	     *  fix for
	     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	     *       sun.security.validator.ValidatorException:
	     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
	     *               unable to find valid certification path to requested target
	     */
       logger.debug("******************************티머니 결제 상태 조회 ******************************");
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
		
	    logger.debug("******************************결제 조회 targetList ******************************");
	    restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		TmoneyStatusVo pVo = new TmoneyStatusVo();
		/*String param = "&order_id=ORDER201511261626110733"
			         + "&req_amt=10";
		String tUrl = STATUS_CHK_URL + param;
		tUrl = tUrl.replaceAll("\\s", "");
		logger.debug("*****"+tUrl+"***");
		URI url = new URI(tUrl);
		http://pay.t-monet.com/TransPayCheck.action?cp_id=MK110269&order_id=ORDER20151126155212427&req_amt=1000"
		String resultStr = restTemplate.postForObject(url, null, String.class);*/
		
		pVo.setQueryType("S");
	    List<TmoneyStatusVo> statusList = tomResultChkMapper.getPaymentResultChkTargetList(pVo);
	    
	    if(statusList != null && statusList.size() > 0) {
	    	for(TmoneyStatusVo vo : statusList) {
	    		String param = "&order_id="+vo.getOid()
	    				+"&req_amt="+vo.getAmt();
	    		String tUrl = STATUS_CHK_URL + param;
	    		tUrl = tUrl.replaceAll("\\s", "");
	    		logger.debug("*****"+tUrl+"***");
	    		URI url = new URI(tUrl);
	    		String resultStr = restTemplate.postForObject(url, null, String.class);
	    		logger.debug("******************************거래내역 조회 결과:"+resultStr+"***************************");
	    		if(resultStr != null && !resultStr.equals("")) {
	    			String[] result = resultStr.trim().split("&");
	    			vo.setPaymentReadResCd(result[0].trim());
	    			vo.setResult(result[1].trim());
	    			tomResultChkMapper.setPaymentReadResCd(vo);
	    		}
	    	}
	    }
	    
	}
	
	
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="procTmoneyCancelReq")
	public void procTmoneyCancelReq() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
	    /*
	     *  fix for
	     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	     *       sun.security.validator.ValidatorException:
	     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
	     *               unable to find valid certification path to requested target
	     */
       logger.debug("******************************티머니 취소 배치 ******************************");
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
		
	    //http://t-town.co.kr:8880/TransPayCancel.action?type=a&order_id=ORDER201511031425295408&trans_id=PY201511031425355262&req_amt=10&cert=29fb503b
	    logger.debug("******************************결제 취소 targetList ******************************");
	    cancelTemplate = new RestTemplate();
	    cancelTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		TmoneyStatusVo pVo = new TmoneyStatusVo();
		pVo.setQueryType("C");
	    List<TmoneyStatusVo> statusList = tomResultChkMapper.getPaymentResultChkTargetList(pVo);
	    
	    if(statusList != null && statusList.size() > 0) {
	    	for(TmoneyStatusVo vo : statusList) {
	    		String param = "&order_id="+vo.getOid()
	    				+"&trans_id="+vo.getTid()
	    				+"&req_amt="+vo.getAmt()
	    				+"&cert="+vo.getCert();
	    		
	    		String tUrl = CANCEL_REQ_URL + param;
	    		tUrl = tUrl.replaceAll("\\s", "");
	    		logger.debug("*****"+tUrl+"***");
	    		URI url = new URI(tUrl);
	    		String resultStr = cancelTemplate.postForObject(url, null, String.class);
	    		logger.debug("******************************취소 요청 결과:"+resultStr+"***************************");
	    		
	    		if(resultStr != null && !resultStr.equals("")) {
	    			String[] result = resultStr.trim().split("&");
	    			vo.setType("a");
	    			vo.setResult(result[0].trim());
	    			tomResultChkMapper.addTmoneyPaymentCancelHist(vo);
	    		}
	    	}
	    	
	    }
	}
}
