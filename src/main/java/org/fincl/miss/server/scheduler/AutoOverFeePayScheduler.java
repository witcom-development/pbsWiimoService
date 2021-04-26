/*
 * @Project Name : miss-service
 * @Package Name : org.fincl.miss.server.scheduler
 * @파일명          : AutoOverFeePayScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.scheduler;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.net.ssl.TrustManager;

import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.service.AutoOverFeePayService;
import org.fincl.miss.server.scheduler.job.overFeePayScheuler.vo.OverFeeVO;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.server.util.MainPayUtil;
import org.fincl.miss.server.util.StringUtil;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.mainpay.sdk.utils.ParseUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @파일명          : AutoOverFeePayScheduler.java
 * @작성일          : 2015. 8. 31.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 31.   |   ymshin   |  최초작성
 */
@Component
public class AutoOverFeePayScheduler  {
    
	@Resource(name="autoOverFeePayService")
	private AutoOverFeePayService autoOverFeePayService;
	private RestTemplate restTemplate;
	private RestTemplate restVrerifyTemplate;
	private static String TARGET_URL = "https://service.paygate.net/INTL/pgtlProcess3.jsp?output=json";
	private static String VERIFY_URL = "https://service.paygate.net/admin/settle/verifyReceived.jsp?tid=";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@SuppressWarnings("unchecked")
	@ClusterSynchronized( jobToken="exeAutoRentOverFeePay")
	public void exeAutoRentOverFeePay() throws JSONException, NoSuchAlgorithmException, KeyManagementException, IOException,URISyntaxException {
		
	    /*
	     *  fix for
	     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	     *       sun.security.validator.ValidatorException:
	     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
	     *               unable to find valid certification path to requested target
	     */
       logger.debug("******************************[초과요금 자동 결제 배치 START] ******************************");
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
		
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		restVrerifyTemplate = new RestTemplate();
		restVrerifyTemplate.getMessageConverters().add(new StringHttpMessageConverter());

		List<OverFeeVO> targetList = autoOverFeePayService.getOverFeeProcTarget();
		
		SmsMessageVO smsVo = null;
		int result = 0;
		if(targetList != null)
		{
			
			for(OverFeeVO fee : targetList) 
			{
				logger.debug("##### target in USR SEQ ==> " + fee.getUsrSeq());

				MainPayUtil MainPayutil = new MainPayUtil();
				try 
				{
		        	HashMap<String, String> parameters = new HashMap<String, String>();
		        	
		        	
		        	String billkey = fee.getBillingKey() ;
		    		if( billkey != null && !"".equals(billkey)) {	// 빌링키 없음 실패		
		    			parameters.put("billkey", billkey);	// 정기결제 인증 키
		    		}
		    		parameters.put("goodsId", "BIL_009");
		    		parameters.put("goodsName", "추가과금");
		    		parameters.put("amount", fee.getOverFee());
		        	
		        	
		    		String responseJson = MainPayutil.approve(parameters,"Y");
		    		
		    		
		    		
		    		Map responseMap = ParseUtils.fromJson(responseJson, Map.class);
					String resultCode = (String) responseMap.get("resultCode");
					String resultMessage = (String) responseMap.get("resultMessage");
				    
				    
					if(!"200".equals(resultCode)) {	// API 호출 실패
						
						fee.setResultCD(resultCode);
						fee.setProcessReasonDesc( resultMessage);
						result = autoOverFeePayService.addTicketPaymentFail(fee);
						logger.debug("count-->>"+fee.getPaymentAttCnt());
						logger.debug("******************************[초과요금 자동 결제 FAIL] USR_SEQ = " + fee.getUsrSeq());
					}
					else
					{	// API 호출 성공
						try 
						{
							fee.setPaymentMethodCd("BIM_001");
							fee.setResultCD("0000");
							fee.setPaymentStusCd("BIS_001");
							fee.setMb_serial_no(parameters.get("mbrRefNo"));
							
							
							Map dataMap = (Map)responseMap.get("data");
							
							fee.setPaymentConfmNo((String) dataMap.get("refNo"));
							fee.setTotAmt(fee.getOverFee());
							fee.setOrder_certify_key((String)responseMap.get("applNo"));
							
							//fee.setOrderCertifyKey(orderCertifyKey);
							fee.setProcessReasonDesc(resultMessage);
							Map<String, String> returnMap = new HashMap<String, String>();
							returnMap = autoOverFeePayService.getPaymentInfoExist(fee);
							logger.debug("check-->> " +returnMap.get("PAYMENT_INFO_EXIST"));
							if(returnMap.get("PAYMENT_INFO_EXIST").equals("N"))
							{
								logger.debug("##### 초과요금 결제정보가 없다. #####");
								result = autoOverFeePayService.addTicketPayment(fee);
							}else{
								logger.debug("##### 초과요금 결제정보가 이미 있다. #####");
							}
							
							
							if(fee.getUsrMpnNo() != null && !fee.getUsrMpnNo().equals("")) 
							{
								logger.debug("getUsrMpnNo-->>"+fee.getUsrMpnNo());
								smsVo = new SmsMessageVO();
								smsVo.setDestno(fee.getUsrMpnNo());
								smsVo.setMsg("과금되었습니다");
								SmsSender.sender(smsVo);
							}
							result = autoOverFeePayService.setOverFeePayComplete(fee);
							logger.debug("******************************[초과요금 자동 결제 SUCCESS] USR_SEQ = " + fee.getUsrSeq());
							
						} catch (Exception e) {
							// TODO: handle exception
							// TB_SVC_PAYMENT_FAIL_HIST 실패 추가
							//ticketVo.setErrMsg(e.getMessage());
							//ticketService.insertPaymentFail(ticketVo);
							fee.setProcessReasonDesc( e.getMessage());
							result = autoOverFeePayService.addTicketPaymentFail(fee);
							logger.debug("count-->>"+fee.getPaymentAttCnt());
							logger.debug("******************************[초과요금 자동 결제 FAIL] USR_SEQ = " + fee.getUsrSeq());
						}
					}
		            
		        } catch (Exception e) {
		            e.printStackTrace();
		            System.out.println(e.toString());
		        }
			}
		}
		logger.debug("******************************[초과요금 자동 결제 배치 END] ******************************");
	}
	public static void main(String[] args) {
		RestTemplate rt = new RestTemplate();
		RestTemplate restVrerifyTemplate = new RestTemplate();
		String param ="&mid=seoulbikekrtest"
				+"&mb_serial_no=BIL_901"
				+"&unitprice=1000"
  				+"&goodcurrency=WON"
                +"&profile_no=3f2eb8c969e37c5fd5df5e93d1dbe5a7d5bf4afed772986b0e5ca9768ebf6bb9";
        String tUrl = "https://service.paygate.net/INTL/pgtlProcess3.jsp?output=json"+param;
        
    	URI url = null;
		try {
			url = new URI(tUrl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String resultStr = rt.postForObject(url, null, String.class);
		Map<String,String> resultMap = StringUtil.makeResultMsgConvertMap(resultStr);
		String stusCd = resultMap.get("payresultcode").trim();
		if(stusCd.equals("0000") && !resultMap.get("tid").equals("")) {
			
			String urlStr = VERIFY_URL+resultMap.get("tid")+"&verifyNum=100";
			URI vUrl = null;
			try {
				vUrl = new URI(urlStr);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ResponseEntity<String> res = restVrerifyTemplate.getForEntity(vUrl, String.class);
			HttpStatus sc =  res.getStatusCode();
			int ss = sc.value();
			System.out.println("ss-->" + ss);
		}
		/*AutoOverFeePayScheduler cls = new AutoOverFeePayScheduler();
		String resultMsg = "https://service.paygate.net/INTL/pgtlProcess3.jsp?output=json&mid=seoulbikekr&mb_serial_no=BIL_009&unitprice=1000&goodcurrency=WON&receipttoname=hmh425&receipttoemail=ruller425 @naver.com&paymethod=801&goodname=BikeSeoul초과요금결제&exprebill=Y&hash_authkey=818a93fb6a90d7ece15142a91f5bb92f34990618fdd3c86ffaca7773990e17f4";
		resultMsg = resultMsg.replaceAll("\\s", "");
		try {
			URI url = new URI(resultMsg);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	//	Map<String,String > m = cls.makeResultMsgConvertMap(resultMsg);
	}
	
}
