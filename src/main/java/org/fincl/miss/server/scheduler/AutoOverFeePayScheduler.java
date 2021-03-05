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
       logger.debug("******************************[초과요금 자동 결제 배치 start]인증서 에러 방지 ******************************");
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
		if(targetList != null){
			//https://service.paygate.net/INTL/pgtlProcess3.jsp?mid=seoulbikekr&output=json&mb_serial_no=BIL_009&unitprice=1000&goodcurrency=WON&profile_no=3f2eb8c969e37c5fd5df5e93d1dbe5a7d5bf4afed772986b0e5ca9768ebf6bb9
			logger.debug("##### targetList in ==> " + targetList.get(0).getUsrSeq());
			for(OverFeeVO fee : targetList) {
				//자동결제
				//https://service.paygate.net/INTL/pgtlProcess3.jsp?profile_no=해당고객에profile_No&mid=해당상점에MID&unitprice=결제요청금액&goodcurrency=결제요청화폐
	
				/**
				 * 거래번호 생성(FORMAT : USRSEQ_BIL009_YYMMDDHI)_20160920_JJH_START
				 */
				DateFormat sdFormat = new SimpleDateFormat("YYMMddHH");
				Date nowDate = new Date();
				
				String tmpDate = sdFormat.format(nowDate);
				//String mId = "seoulbikekrtest";
				String mId = "seoulbikekr";
				String mb_serial_no = null;
				
				if(fee.getPayment_cls_cd().equals("BIL_008")){
					mb_serial_no = fee.getUsrSeq() + "_BIL009_" + fee.getRentHistSeq() + "_" + tmpDate;
				}else{
					mb_serial_no = fee.getUsrSeq() + "_BIL009_" + tmpDate;
				}
				
				fee.setMb_serial_no(mb_serial_no);
				
				logger.debug("##### 초과요금 거래번호 ==> " + mb_serial_no);
				/**
				 * 거래번호 생성(FORMAT : USRSEQ_BIL009_YYMMDDHI)_20160920_JJH_END
				 */
				
				if(fee.getPaymentMethodCd().equalsIgnoreCase("BIM_002")) {
					mId = "seoulbikeus";
				}
				String param ="&mid="+mId
					+"&mb_serial_no="+mb_serial_no
					+"&unitprice="+fee.getOverFee()
					+"&goodoption1="+fee.getUsrSeq()
					+"&receipttoname="+fee.getUsrSeq()
	  				+"&goodcurrency=WON";
				//다날
				//https://service.paygate.net/INTL/pgtlProcess3.jsp?mid=seoulbikekr&paymethod=801&unitprice=2000&goodcurrency=WON&goodname=bikeseoul&receipttoname=최종찬&receipttoemail=siyumy@nate.com&exprebill=Y&hash_authkey=76bc773909876022a6d5ad822100cbd078e2f220149c927e3d258c9afc96a5a2
				if(fee.getPaymentMethodCd().equalsIgnoreCase("BIM_003")) {
					//휴대폰
					param += "&receipttoname="+fee.getMbId()
						  +"&receipttoemail="+fee.getMbEmailName()
						  +"&paymethod=801"
						  +"&goodname=BikeSeoul초과요금결제"
						  +"&exprebill=Y"
						  +"&hash_authkey="+fee.getBillingKey().trim();
				} else {
					//카드
					param += "&profile_no="+fee.getBillingKey().trim();
				}
				try {
					//https://service.paygate.net/INTL/pgtlProcess3.jsp?&mid=seoulbikekr&output=json&unitprice=2000&goodcurrency=WON&profile_no=76bc773909876022a6d5ad822100cbd078e2f220149c927e3d258c9afc96a5a2
					logger.debug("*****프로파일 결제***");
					String tUrl = TARGET_URL + param;
					tUrl = tUrl.replaceAll("\\s", "");
					logger.debug("*****"+tUrl+"***");
					URI url = new URI(tUrl);
					String resultStr = restTemplate.postForObject(url, null, String.class);
					//logger.debug("****************RESULT******************");
					//logger.debug("*****"+resultStr+"***");
					if(resultStr != null){
						//TODO 성공하면 다음단계
						//성공코드에 
						logger.debug("*****프로파일 응답:");
						Map<String,String> resultMap = StringUtil.makeResultMsgConvertMap(resultStr);
						if(resultMap != null){
							smsVo = new SmsMessageVO();
							String stusCd = resultMap.get("payresultcode").trim();
							String desc = "초과 요금 비정기 결제 스케줄러";
							fee.setResultCD(stusCd);
							fee.setTotAmt(fee.getOverFee());
							logger.debug("*************결과 코드 :::::"+stusCd+"********************************");
							if(stusCd.equals("0000") && !resultMap.get("tid").equals("")) {
								fee.setPaymentStusCd("BIS_001");
								fee.setPaymentConfmNo(resultMap.get("tid"));
								fee.setProcessReasonDesc(desc);
								Map<String, String> returnMap = new HashMap<String, String>();
								returnMap = autoOverFeePayService.getPaymentInfoExist(fee);
								
								if(returnMap.get("PAYMENT_INFO_EXIST").equals("N"))
								{
									logger.debug("##### 초과요금 결제정보가 없다. #####");
									result = autoOverFeePayService.addTicketPayment(fee);
								}else{
									logger.debug("##### 초과요금 결제정보가 이미 있다. #####");
								}
								
								if(fee.getUsrMpnNo() != null && !fee.getUsrMpnNo().equals("")) {
									logger.debug("getUsrMpnNo-->>"+fee.getUsrMpnNo());
									smsVo.setDestno(fee.getUsrMpnNo());
									smsVo.setMsg(SendType.SMS_004, fee.getOverMi(),fee.getOverFee());
									SmsSender.sender(smsVo);
								}
								
							} else {
								//TODO 실패 메세지로 변경
								fee.setProcessReasonDesc( resultMap.get("payresultmsg"));
								result = autoOverFeePayService.addTicketPaymentFail(fee);
								logger.debug("count-->>"+fee.getPaymentAttCnt());
								if(Integer.parseInt(fee.getPaymentAttCnt()) == 1) {
									if(fee.getUsrMpnNo() != null && !fee.getUsrMpnNo().equals("")) {
										smsVo.setDestno(fee.getUsrMpnNo());
										smsVo.setMsg(SendType.SMS_005, fee.getOverMi(),fee.getOverFee());
										SmsSender.sender(smsVo);
									}
								}
							}
							result = autoOverFeePayService.setOverFeePayComplete(fee);
							logger.debug("*************자동결제 후 결제 응답코드 리턴 START********************************");
							if(stusCd.equals("0000") && !resultMap.get("tid").equals("")) {
								String urlStr = VERIFY_URL+resultMap.get("tid")+"&verifyNum=100";
								URI vUrl = new URI(urlStr);
								ResponseEntity<String> res = restVrerifyTemplate.getForEntity(vUrl, String.class);
								HttpStatus httpStatus =  res.getStatusCode();
								int statusCd = httpStatus.value();
								logger.debug("*************자동결제 후 결제 응답코드 리턴"+statusCd+"********************************");
								
							}
						}
					}
					
				} catch (URISyntaxException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}	
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
