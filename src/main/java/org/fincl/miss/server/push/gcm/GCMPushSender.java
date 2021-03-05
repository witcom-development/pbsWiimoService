/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.util.push.gcm
 * @파일명          : GCMPushServerController.java
 * @작성일          : 2015. 7. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 20.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.push.gcm;

import java.util.HashMap;
import java.util.List;

import org.fincl.miss.server.push.PushVO;
import org.fincl.miss.server.push.Send;
import org.fincl.miss.server.util.IConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.android.gcm.server.*;
/**
 * @파일명          : GCMPushServerController.java
 * @작성일          : 2015. 7. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 20.   |   ymshin   |  최초작성
 */
public class GCMPushSender implements Send{

	protected static Logger log = LoggerFactory.getLogger(GCMPushSender.class);
	private volatile static GCMPushSender instance = new GCMPushSender();	
	//private static String AUTH_TOKEN = "AIzaSyBVdNOyuxlaETqvFCZUrEindfBeaK1TfRM";//test
	private static String AUTH_TOKEN = "AIzaSyDadCNif0CmKoaLR0aXirdfWq08HKGxU_g"; //서울시
	private GCMPushSender() {
		// TODO Auto-generated constructor stub
	}
	public static GCMPushSender getInstance() {
		return instance;
	}
	
	/**
	 * @location   : com.dkitec.spb.util.push.gcm.Sender.send
	 * @writeDay   : 2015. 7. 21. 오후 1:31:10
	 * @overridden : @see com.dkitec.spb.util.push.Sender#send(java.util.HashMap, java.lang.Object)
	 * @Todo       :단건.
	 */ 
	@Override
	public void send(PushVO pushVo, Object obj) throws Exception {
		HashMap<String, String> entity = new HashMap<String, String>();
        try {
			// Input Parameter Check
        	log.debug("**********************푸시 단건 전송 **************************");
        	log.debug("**********************GCM:["+AUTH_TOKEN+"]************************");
			Message _payload = (Message)obj;
			String _regId = pushVo.getTokenId();
						
			if(_payload == null || AUTH_TOKEN == null || "".equals(_payload.toString()) || "".equals(AUTH_TOKEN)) {
				log.debug("GCM Parameter Invalid");
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_PARAM_NOTFOUND);
				return;
			}
			
			Sender sender = new Sender(AUTH_TOKEN);		
			Result result = sender.sendNoRetry(_payload, _regId);
		
			String error = result.getErrorCodeName();
			log.debug("GCM error-->>" + error);			
			if (error == null) {
                log.debug(">>> GCM Push Result Send Success");
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                	// same device has more than on registration ID: update database
                	log.debug("canonicalRegId="+canonicalRegId+" : same device has more than on registration ID: update database");
                } 
            } else {            	
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                	// application has been removed from device - unregister database
                	log.debug("error="+error +" : application has been removed from device - unregister database");                	
                }else{
                	log.debug(">>> GCM Push Result Send Error : " + error);
                }
            }
			
			entity.put(IConstants.RESULT_CODE, getRtCode(error));
			//entity.put("ID", sendId);
			entity.put("ERROR", error);
		
		} catch ( InvalidRequestException ire )	{
			int httpResult = ire.getHttpStatusCode();
			String msg = "";
			if(httpResult == 401) {
				msg = "GCM AUTH TOKEN ERROR ["+pushVo.getAppId()+"]";
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_AUTH_TOKEN_ERROR);
			} else if (httpResult == 503) {
				msg = "GCM SEND TIMEOUT";
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_SEND_TIMEOUT);
			} else {
				msg = ire.getMessage();
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_SEND_EXCEPTION);
			}
			log.error(">>> GCM Push Result Send Error : " + msg);
		} catch ( Exception e )	{
			log.error("Exception has occurred in ", e);
			entity.put(IConstants.RESULT_CODE, IConstants.GCM_SEND_EXCEPTION);
		}
		
	}
	/**
	 * @location   : com.dkitec.spb.util.push.gcm.Sender.send
	 * @writeDay   : 2015. 7. 21. 오후 2:41:44
	 * @overridden : @see com.dkitec.spb.util.push.Sender#send(java.util.HashMap, java.util.List, java.lang.Object)
	 * @Todo       :복수건
	 */ 
	@Override
	public void send(PushVO pushVo, List<String> regIds, Object obj) throws Exception {
		HashMap<String, String> entity = new HashMap<String, String>();
		try {
			log.debug("**********************푸시 복수건 전송 **************************");
        	log.debug("**********************GCM:["+AUTH_TOKEN+"]************************");
			// Input Parameter Check
			Message _payload = (Message)obj;
			if(_payload == null || AUTH_TOKEN == null || "".equals(_payload.toString()) || "".equals(AUTH_TOKEN)) {
				log.debug("GCM Parameter Invalid");
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_PARAM_NOTFOUND);
				return;
			}
			
			Sender sender = new Sender(AUTH_TOKEN);		
			MulticastResult result = sender.send(_payload, regIds, 5);
			
			String error = result.getResults().get(0).getErrorCodeName();
						
			if (error == null) {
                log.debug(">>> GCM Push Result Send Success");
                String canonicalRegId = result.getResults().get(0).getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                	// same device has more than on registration ID: update database
                	log.debug("canonicalRegId="+canonicalRegId+" : same device has more than on registration ID: update database");
                } 
            } else {            	
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                	// application has been removed from device - unregister database
                	log.debug("error="+error +" : application has been removed from device - unregister database");                	
                }else{
                	log.debug(">>> GCM Push Result Send Error : " + error);
                }
            }
			
			entity.put(IConstants.RESULT_CODE, getRtCode(error));
			//entity.put("ID", sendId);
			entity.put("ERROR", error);
		
		} catch ( InvalidRequestException ire )	{
			int httpResult = ire.getHttpStatusCode();
			String msg = "";
			if(httpResult == 401) {
				msg = "GCM AUTH TOKEN ERROR ["+pushVo.getAppId()+"]";
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_AUTH_TOKEN_ERROR);
			} else if (httpResult == 503) {
				msg = "GCM SEND TIMEOUT";
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_SEND_TIMEOUT);
			} else {
				msg = ire.getMessage();
				entity.put(IConstants.RESULT_CODE, IConstants.GCM_SEND_EXCEPTION);
			}
			log.error(">>> GCM Push Result Send Error : " + msg);
		} catch ( Exception e )	{
			log.error("Exception has occurred in ", e);
			entity.put(IConstants.RESULT_CODE, IConstants.GCM_SEND_EXCEPTION);
		}
		
	}
	
	
	private String getRtCode(String errorMsg) {
		String rtCode = IConstants.GCM_SEND_EXCEPTION;
		try{			
			if      ( errorMsg == null || "".equals(errorMsg) )           rtCode = IConstants.RT_SUCCESS;
			else if ( errorMsg.equalsIgnoreCase("QuotaExceeded")        ) rtCode = IConstants.GCM_QUOTAEXCEEDED;
			else if ( errorMsg.equalsIgnoreCase("DeviceQuotaExceeded")  ) rtCode = IConstants.GCM_DEVICEQUOTAEXCEEDED;
			else if ( errorMsg.equalsIgnoreCase("InvalidRegistration")  ) rtCode = IConstants.GCM_INVALIDREGISTRATION;
			else if ( errorMsg.equalsIgnoreCase("NotRegistered")        ) rtCode = IConstants.GCM_NOTREGISTERED;
			else if ( errorMsg.equalsIgnoreCase("MessageTooBig")        ) rtCode = IConstants.GCM_MESSAGETOOBIG;
			else if ( errorMsg.equalsIgnoreCase("MissingCollapseKey")   ) rtCode = IConstants.GCM_MISSINGCOLLAPSEKEY;
			else rtCode = IConstants.GCM_SEND_EXCEPTION;			
		} catch ( Throwable t )	{
			log.error("Exception has occurred in " + t.getMessage());
		}
		return rtCode;
	}	
	
}
