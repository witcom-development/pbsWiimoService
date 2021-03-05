/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler.job
 * @파일명          : RefundUsrDepositScheduler.java
 * @작성일          : 2015. 9. 3.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 3.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
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
import org.fincl.miss.server.scheduler.job.refundDeposit.service.UsrRefundDepositMapper;
import org.fincl.miss.server.util.AES256anicar;
import org.fincl.miss.server.util.AesCtr;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @파일명          : RefundUsrDepositScheduler.java
 * @작성일          : 2015. 9. 3.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 9. 3.   |   ymshin   |  최초작성
 */
@Component
public class RefundUsrDepositScheduler {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private UsrRefundDepositMapper usrRefundDepositMapper;
    private static String cancelUrl = "https://service.paygate.net/service/cancelAPI.json?callback=callback";
    private static String apiSecretKey = "spb@"; 
	//https://stgsvc.paygate.net/service/cancelAPI.json?callback=callback&mid=domestic&tidEncrypted=AES256f//8LFwS8FXcBPJ9INKhVp0BDyxo3aveedPonPC2VTKxkgbc&amount=F
	@ClusterSynchronized(jobToken="getRefundUsrList")
	public void getRefundUsrList() throws MalformedURLException, JSONException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
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
	    logger.debug("**************외국인 보증금 자동 취소**************************");
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		//String url = cancelUrl;
		List<Map<String,String>> usrList = usrRefundDepositMapper.getRefundUsrList();
		//이용권을 사용한 이력이 없을경우..
		//초과요금을 체크할 이유가 없음.
		if(usrList != null){
			for(Map<String,String> map : usrList){
				//결제취소처리
                logger.debug("**************결제취소처리*************************");
                String m_id =  "seoulbikeus";
                String t_id =map.get("tId");//"domestic_2015-9-8.1727149546"
                String key256= AES256anicar.sha256(apiSecretKey);
                String tidEncrypted = "AES256" + new AesCtr().encrypt(t_id, key256, 256);
                String cUrl = cancelUrl + "&mid=" + m_id + "&tid=" + tidEncrypted + "&amount=F";
                cUrl = cUrl.replaceAll("\\s", "");
            	logger.debug("*************보증금 취소*["+cUrl+"]**********************");
                URI url = new URI(cUrl);
                String result = restTemplate.postForObject(url, null, String.class);
                String resultAll = result.substring(result.indexOf("(")+1, result.length()-1);
                logger.debug("**************["+resultAll+"]**********************");
                JSONObject jObject  = new JSONObject(resultAll); // json
               // JSONObject data = jObject.getJSONObject("content"); // get data object
                
                String rMsg = jObject.getString("replyCode");
                //TODO 성공시
                logger.debug("*********replyCode*****["+rMsg+"]**********************");
                map.put("errCd", rMsg);
				map.put("processReasonDesc", jObject.getString("replyMessage"));
				map.put("paymentAmt", "50000");
                if(rMsg.equals("0000")) {
                	/**
                	 * 2015-12-28 변경.dki
                	 * 이용권 사용이 없을경우 이용권도 결제취소로 변경.
                	 * 1)보증금 취소 상태 갱신
                	 * 2)결제 취소.
                	 * 3)결제취소로 상태 갱신.
                	 * 4)이용권 삭제.
                	 * */
                	
                	//보증금 취소상태 갱신. 
                	usrRefundDepositMapper.addRefundHist(map);
                	
                	logger.debug("*********이용권 취소 시작************");
                	Map<String,String> voucherMap = usrRefundDepositMapper.getVoucherInfo(map);
                	tidEncrypted = "AES256" + new AesCtr().encrypt(voucherMap.get("tId"), key256, 256);
                	String ccUrl = cancelUrl + "&mid=" + m_id + "&tid=" + tidEncrypted + "&amount=F";
                	ccUrl = ccUrl.replaceAll("\\s", "");
                  	logger.debug("*************이용권 취소*["+ccUrl+"]**********************");
                  	url = new URI(ccUrl);
                    result = restTemplate.postForObject(url, null, String.class);
                    resultAll = result.substring(result.indexOf("(")+1, result.length()-1);
                    logger.debug("**************["+resultAll+"]**********************");
                    JSONObject cObject  = new JSONObject(resultAll); // json
                    rMsg = cObject.getString("replyCode");
                  	
                    if(rMsg.equals("0000")) {
                    	//이용권취소 상태 갱신
                    	usrRefundDepositMapper.setSealedVoucherCancel(voucherMap);
                    	//이용권 삭제
                    	usrRefundDepositMapper.setVoucherUseComplete(voucherMap);
                    } else {
                    	//취소가 실패할경우.
                    	voucherMap.put("errCd", rMsg);
                    	voucherMap.put("processReasonDesc", jObject.getString("replyMessage"));
                    	usrRefundDepositMapper.addTicketPaymentFail(voucherMap);
                    }
                    
                } else {
                	usrRefundDepositMapper.addTicketPaymentFail(map);
                }
			}
		}
	}
	//
	@ClusterSynchronized(jobToken="getRefundUseVoucherList")
	public void getRefundUseVoucherList() throws JSONException, URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
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
	    logger.debug("**************외국인 보증금 자동 취소* getRefundUseVoucherList*************************");
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		
		List<Map<String,String>> usrList = usrRefundDepositMapper.getRefundUseVoucherList();
		if(usrList != null){
			for(Map<String,String> map : usrList){
				//이용권 사용 처리
				 logger.debug("**************결제취소처리 getRefundUseVoucherList*************************");
				String rent = usrRefundDepositMapper.getRentChk(map);
				int rentHist = usrRefundDepositMapper.getRentHistChk(map);
				//결제취소처리
				logger.debug("***rent:"+rent+"****rentHist:"+rentHist+"**tId**"+map.get("tId"));
				if(rent == null && rentHist == 0) {
					String m_id = "seoulbikeus";
					String t_id =map.get("tId");//"domestic_2015-9-8.1727149546"
					String key256= AES256anicar.sha256(apiSecretKey);
					String tidEncrypted = "AES256" + new AesCtr().encrypt(t_id, key256, 256);
					String cUrl = cancelUrl + "&mid=" + m_id + "&tid=" + tidEncrypted + "&amount=F";
                	cUrl = cUrl.replaceAll("\\s", "");
                	logger.debug("********취소 처리******["+cUrl+"]**********************");
					URI url = new URI(cUrl);
					String result = restTemplate.postForObject(url, null, String.class);
				    logger.debug("**************["+result+"]**********************");
					String resultAll = result.substring(result.indexOf("(")+1, result.length()-1);
					
					JSONObject jObject  = new JSONObject(resultAll); // json
					if(jObject.has("replyCode")) {
						String rMsg = jObject.getString("replyCode");
						//TODO 성공시
						map.put("errCd", rMsg);
						map.put("processReasonDesc", jObject.getString("replyMessage"));
						map.put("paymentAmt", "50000");
						logger.debug("************rMsg1:**["+rMsg+"]**********************");
						if(rMsg.equals("0000")) {
							logger.debug("************rMsg2:**["+rMsg+"]**********************");
							//이용권 사용 처리
							usrRefundDepositMapper.addRefundHist(map);
						} else {
							usrRefundDepositMapper.addTicketPaymentFail(map);
						}
						
					}
				}
				
			}
		}
	}
	
	public static void main(String[] args) {
		String org = "callback({'replyCode':'MID_INVALID','replyMessage':'MID_INVALID','content':{'object':'cancelAPI error: params pair mid and tid is not same:seoulbikeus tid: domestic_2015-9-8.1727149546'}})";
		String org1 = "{\"replyCode\":\"MID_INVALID\",\"replyMessage\":\"MID_INVALID\"}";
		String jsn = "{\"age\":\"32\",\"name\":\"steave\",\"job\":\"baker\"}";
		ObjectMapper mapper = new ObjectMapper();
		Map<String,String> jMap = new HashMap<String, String>();
		String resultAll = org.substring(org.indexOf("(")+1 , org.indexOf(")"));
		String all = resultAll.substring(0 , org.lastIndexOf("content")-11)+"}";
		try {
			//jMap = mapper.readValue(all, new TypeReference<HashMap<String, String>>() {});
			JSONObject jObject  = new JSONObject(resultAll); // json
			JSONObject data = jObject.getJSONObject("content"); // get data object
			String projectname = data.getString("object"); // get the name from data.
			String rMsg = jObject.getString("replyCode");
			System.out.println(projectname+",,"+rMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
