package org.fincl.miss.server.scheduler;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.fincl.miss.server.push.PushSendProc;
import org.fincl.miss.server.push.PushVO;
import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.pushProc.ReturnMsgVO;
import org.fincl.miss.server.scheduler.job.pushProc.service.ReturnAlarmMapper;
import org.fincl.miss.server.scheduler.job.sms.SmsMessageVO;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.sms.SmsSender;
import org.fincl.miss.server.util.IConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReturnAlarmScheduler  {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ReturnAlarmMapper returnAlramMapper;
	private static final String TARGET_URL = "https://www.bikeseoul.com:446/app/rent/moveRecommendReturnStation.do";
	@ClusterSynchronized( jobToken="returnAlarmPushProc")
	public void returnAlarmPushProc() throws Exception {
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
		
		
		int result = 0;
        List<ReturnMsgVO> msgTargetList = returnAlramMapper.getBikeReturnAlarm();
        logger.debug("******************************반납 알람 배치 스타트*["+msgTargetList.size()+"]*******************************************");
        if(msgTargetList != null && msgTargetList.size() > 0) {
        	logger.debug("******************************이전 반납 ********************************************");
        	
        	List<String> apnsList = new ArrayList<String>();
        	List<String> gcmList = new ArrayList<String>();
            for(ReturnMsgVO target :msgTargetList) {
            	if(target.getUsrDeviceType() != null) {
            		if(target.getUsrDeviceType().equals(IConstants.PUSH_TYPE_APNS)){
            			apnsList.add(target.getUsrDeviceId());
            		} else if(target.getUsrDeviceType().equals(IConstants.PUSH_TYPE_GCM)) {
            			gcmList.add(target.getUsrDeviceId());
            		} 
            	}
            }
            logger.debug("******************************sms 전송*************************************");
            SmsMessageVO smsVo = null;
            List<SmsMessageVO> smsList = new ArrayList<SmsMessageVO>();
            for(ReturnMsgVO target :msgTargetList) {
                smsVo = new SmsMessageVO();
            	smsVo.setDestno(target.getUsrMpnNo());
            	smsVo.setMsg(SendType.SMS_003, target.getBikeNo(), target.getBaseRentTime());
            	smsList.add(smsVo);
            }
            SmsSender.sender(smsList);
         	// logger.debug("******************************푸시 메세지 전송***************************************");
            // String msg = "<따릉이> 자전거 반납 추천 대여소 주변에 추천 반납 대여소 확인하기.";
           
        	//안드로이드
            /*
            if(gcmList != null && gcmList.size() > 0) {
            	logger.debug("******************************안드로이드***************************************");
	            PushVO gcmVo = new PushVO();
	            gcmVo.setPushType(IConstants.PUSH_TYPE_GCM);
	            gcmVo.setMessage(msg);
	            gcmVo.setPushLinkUrl(TARGET_URL);
	            gcmVo.setTokenList(gcmList);
                
	            result = PushSendProc.exePush(gcmVo);
            }*/
            //아이폰
            /*  if(apnsList != null &&  apnsList.size() > 0) {
	            PushVO apnsVo = new PushVO();
	            apnsVo.setPushType(IConstants.PUSH_TYPE_APNS);
	            apnsVo.setMessage(msg);
	            apnsVo.setPushLinkUrl(TARGET_URL);
	            apnsVo.setTokenList(apnsList);
	            result = PushSendProc.exePush(apnsVo);
            }*/
            //sms 전송
        }
        
        //TODO :이미 보낸 푸시메세지, sms 는 전송완료로 표시.
        	for(ReturnMsgVO target :msgTargetList) {
        		result = returnAlramMapper.setReturnAlarmMsgComplete(target);
            }
        }
	}
