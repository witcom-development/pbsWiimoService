/*
 * @Project Name : spb-app
 * @Package Name : com.dkitec.spb.util.push.ipns
 * @파일명          : IPNSPushSender.java
 * @작성일          : 2015. 7. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 21.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.server.push.apns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

import org.fincl.miss.server.push.PushVO;
import org.fincl.miss.server.push.Send;
import org.fincl.miss.server.util.IConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fincl.miss.server.exeption.ServerException;

/**
 * @파일명          : IPNSPushSender.java
 * @작성일          : 2015. 7. 21.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 7. 21.   |   ymshin   |  최초작성
 */
public class APNSPushSender implements Send{

	protected static Logger log = LoggerFactory.getLogger(APNSPushSender.class);
	private volatile static APNSPushSender instance = new APNSPushSender();	
	private static String cert_data = "";
	private static String cert_pw = "dkitec1234";
	
	/*
	 *  Object keystore: a reference to a keystore file, or the actual keystore content. See Preparing certificates for more information about how to create a keystore. You can pass the following objects to this parameter:
	 *	java.io.File: a direct pointer to your keystore file
	 *	java.lang.String: a path to your local keystore file
	 *	java.io.InputStream: a stream providing the bytes from a keystore
	 *	byte[]: the actual bytes of a keystore
	 *	java.security.KeyStore: an actual loaded keystore
	 * */
	private APNSPushSender() {
		// TODO Auto-generated constructor stub
	}
	public static APNSPushSender getInstance() {
		return instance;
	}
	
	/**
	 * @location   : com.dkitec.spb.util.push.ipns.Sender.send
	 * @writeDay   : 2015. 7. 21. 오후 1:30:58
	 * @overridden : @see com.dkitec.spb.util.push.Sender#send(java.util.HashMap, java.lang.Object)
	 * @Todo       :단건
	 */ 
	@Override
	public void send(PushVO pushVo, Object obj) throws Exception {
		String devToken = pushVo.getTokenId();
		boolean isProduct = true;
		HashMap<String, String> entity = new HashMap<String, String>();
		PushNotificationManager pushManager = new PushNotificationManager();
		AppleNotificationServer appServer = new AppleNotificationServerBasicImpl(cert_data, cert_pw,isProduct);
		pushManager.initializeConnection(appServer);
		Device device = new BasicDevice(devToken, true);
		PushNotificationPayload payload = (PushNotificationPayload)obj;
		
		
		List<PushedNotification> notifications = pushManager.sendNotifications(payload, device);
		PushedNotification notification = notifications.get(0);
		pushManager.stopConnection();
		if(notification.isSuccessful()) {
			log.info("APNS Push is Successed using JAVAPNS");
			
		} else {
			if (notification.getException().toString().contains("certificate_unknown")) { 
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_CERTIFICATE_UNKNOWN);
				throw new Exception(IConstants.APNS_CERTIFICATE_UNKNOWN);
			} else if (notification.getException().toString().contains("certificate_revoked")) { 
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_HANDSHAKE_REVOKE);
				throw new Exception(IConstants.APNS_HANDSHAKE_REVOKE);
			} else if (notification.getException().toString().contains("certificate_expired")) {
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_HANDSHAKE_EXPIRE);
				throw new Exception(IConstants.APNS_HANDSHAKE_EXPIRE);
			} else if (notification.getException().toString().contains("Invalid token")) {
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_INVALID_DEVTOKEN);
				throw new Exception(IConstants.APNS_INVALID_DEVTOKEN);
			} else {
				log.error("JAVAPNS Error=["+notification.getException().toString()+"]");
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_SENDBOX_FAIL);
				throw new Exception(IConstants.APNS_SENDBOX_FAIL);
			}
		}
		
	}
	/**
	 * @location   : com.dkitec.spb.util.push.ipns.Sender.send
	 * @writeDay   : 2015. 7. 21. 오후 2:41:39
	 * @overridden : @see com.dkitec.spb.util.push.Sender#send(java.util.HashMap, java.util.List, java.lang.Object)
	 * @Todo       :복수건
	 */ 
	@Override
	public void send(PushVO pushVo, List<String> regIds, Object obj) throws Exception {
		
		boolean isProduct = true;
		HashMap<String, String> entity = new HashMap<String, String>();
		List<HashMap<String, String>> errorList = new ArrayList<HashMap<String,String>>();
		PushNotificationManager pushManager = new PushNotificationManager();
		AppleNotificationServer appServer = new AppleNotificationServerBasicImpl(cert_data, cert_pw,isProduct);
		
		pushManager.initializeConnection(appServer);
		List<Device> deviceList = new ArrayList<Device>();
		Device device = null;
		for(String deviceId : regIds){
			device = new BasicDevice(deviceId, true);
			deviceList.add(device);
		}
		
		PushNotificationPayload payload = (PushNotificationPayload)obj;
		List<PushedNotification> notifications = pushManager.sendNotifications(payload, deviceList);
		PushedNotification notification = notifications.get(0);
		pushManager.stopConnection();
		
		if(notification.isSuccessful()) {
			log.info("APNS Push is Successed using JAVAPNS");
		} else {
			if (notification.getException().toString().contains("certificate_unknown")) { 
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_CERTIFICATE_UNKNOWN);
				throw new ServerException(IConstants.APNS_CERTIFICATE_UNKNOWN);
			} else if (notification.getException().toString().contains("certificate_revoked")) { 
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_HANDSHAKE_REVOKE);
				throw new ServerException(IConstants.APNS_HANDSHAKE_REVOKE);
			} else if (notification.getException().toString().contains("certificate_expired")) {
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_HANDSHAKE_EXPIRE);
				throw new ServerException(IConstants.APNS_HANDSHAKE_EXPIRE);
			} else if (notification.getException().toString().contains("Invalid token")) {
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_INVALID_DEVTOKEN);
				throw new ServerException(IConstants.APNS_INVALID_DEVTOKEN);
			} else {
				log.error("JAVAPNS Error=["+notification.getException().toString()+"]");
				entity.put(IConstants.RESULT_CODE, IConstants.APNS_SENDBOX_FAIL);
				throw new ServerException(IConstants.APNS_SENDBOX_FAIL);
			}
		}
		errorList.add(entity);
		pushVo.setErrorList(errorList);
		
	}
}