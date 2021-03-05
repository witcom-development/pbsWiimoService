package org.fincl.miss.server.scheduler;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
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
public class RentSendScheduler  {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ReturnAlarmMapper returnAlramMapper;
	private static final String TARGET_URL = "https://www.bikeseoul.com:446/app/rent/moveRecommendReturnStation.do";
	@ClusterSynchronized( jobToken="rentSendPushProc")
	public void rentSendPushProc() throws Exception {
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
		List<HashMap<String, String>> msgTargetList = returnAlramMapper.getBikeRentSend();
        logger.debug("******************************대여 전송 스타트*["+msgTargetList.size()+"]*******************************************");
        if(msgTargetList != null && msgTargetList.size() > 0) 
        {
        	logger.debug("******************************전송 start ********************************************");
        	
            for(HashMap<String, String> target :msgTargetList) 
            {
            	SmsMessageVO smsVo = null;
            	smsVo = new SmsMessageVO();
            	//smsVo.setDestno(target.getUsrMpnNo());
            	//smsVo.setMsg(SendType.SMS_003, target.getBikeNo(), target.getBaseRentTime());
            	//SmsSender.sender(smsVo);
            	result = returnAlramMapper.setBikeRentSendComplete(target.get("RENT_SEQ").toString());
            }
            logger.debug("******************************sms 전송 complete*************************************");
            
        }
	}
}
