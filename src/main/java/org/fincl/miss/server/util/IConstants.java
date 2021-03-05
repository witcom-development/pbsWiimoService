package org.fincl.miss.server.util;

public interface IConstants {
	/**
	 * push
	 * */
	// Push Ÿ��
	public final static String RT_SUCCESS = "0000";
	public final static String RT_FAIL	  = "9999";
	public final static String RT_CANCEL  = "9998";
	
	public final static String RESULT_CODE = "RT";
	public final static String RESULT_MSG  = "RT_MSG";
	
	public final static String PUSH_TYPE_APNS = "ios";//apple
	public final static String PUSH_TYPE_GCM = "android";//android
	
	public final static String APP_ID 				= "APP_ID";
	public final static String MESSAGE_ID 			= "MESSAGE_ID";
	
	public final static String TEST_YN 				= "TEST_YN";
	public final static String PUSH_TYPE 			= "PUSH_TYPE";
	public final static String PRIORITY 			= "PRIORITY";
	public final static String PUSH_ID				= "PUSH_ID";
	public final static String PUSH_ID_TYPE			= "PUSH_ID_TYPE";
	public final static String PUSH_TOKEN			= "PUSH_TOKEN";
	public final static String REQUEST_TYPE			= "REQUEST_TYPE";
	public final static String SEND_TYPE			= "SEND_TYPE";
	public final static String RESV_DATE			= "RESV_DATE";
	public final static String REPORT_URL			= "REPORT_URL";	
	public final static String PUSH_DATA			= "PUSH_DATA";
	public final static String PAYLOAD				= "PAYLOAD";
	public final static String MESSAGE				= "MESSAGE";
	public final static String ENCRYPT_YN			= "ENCRYPT_YN";
	public final static String PUSH_LINK_URL        = "PUSH_LINK_URL";
	public final static String REPEAT_KEY 			= "REPEAT_KEY";
	
	public final static String ALERT				= "ALERT";
	public final static String SOUND				= "SOUND";
	public final static String BEDGE				= "BEDGE";
	
	public final static String COLLAPSE_KEY			= "COLLAPSE_KEY";
	public final static String DELAY_WHILE_IDLE		= "DELAY_WHILE_IDLE";
	
	// GCM ���� �ڵ� 4�ڸ�
	public final static String PGS_SEND_FAIL 		 				= "1115";  // PGS Send Fail

	// GCM ���� �ڵ� 4�ڸ� 1200 ����
	public final static String GCM_SEND_EXCEPTION      				= "1200";  // GCM Send Exception
	public final static String GCM_PARAM_NOTFOUND      				= "1201";  // GCM Parameter is not found
	public final static String GCM_CONNECTION_ERROR    				= "1202";  // GCM Server Connect Error
	public final static String GCM_AUTH_TOKEN_ERROR    				= "1203";  // GCM AUTH TOKEN Error
	public final static String GCM_SEND_TIMEOUT        				= "1204";  // GCM Send Timeout
	public final static String GCM_QUOTAEXCEEDED       				= "1205";  // GCM Quota Exceeded (�ٷ� �޽��� ���)
	public final static String GCM_DEVICEQUOTAEXCEEDED 				= "1206";  // GCM Device Quota Exceeded (Ư�� �ܸ��� �ٷ� �޽��� ���)
	public final static String GCM_INVALIDREGISTRATION 				= "1207";  // GCM REGISTRATION ID Invalid (REGISTRATION ID ����)
	public final static String GCM_NOTREGISTERED       				= "1208";  // GCM REGISTRATION ID  (REGISTRATION ID ����)
	public final static String GCM_MESSAGETOOBIG       				= "1209";  // GCM ��� ���� (�޽��� ������ �ʰ�)
	public final static String GCM_MISSINGCOLLAPSEKEY  				= "1210";  // GCM ��� ���� (COLLAPSE KEY ����)
	
	// APNS ���� �ڵ� 4�ڸ� 1300 ����
	public final static String APNS_SOCKET_ERROR     				= "1303"; // APNS ���ῡ ���� �߻��߽��ϴ�.
	public final static String APNS_SEND_ERROR       				= "1304"; // APNS ��� �� ���� �߻��߽��ϴ�.
	public final static String APNS_INVALID_DEVTOKEN 				= "1305"; // ��ȿ���� ���� Device Token
	
	public final static String APNS_PROCESSING_ERROR				= "1306"; // 
	public final static String APNS_MISSING_DEVICE_TOKEN			= "1307"; // 
	public final static String APNS_MISSING_TOPIC					= "1308"; //
	public final static String APNS_MISSING_PAYLOAD					= "1309"; // 
	public final static String APNS_INVALID_TOKEN_SIZE				= "1310"; // 
	public final static String APNS_INVALID_TOPIC_SIZE				= "1311"; // 
	public final static String APNS_INVALID_PAYLOAD_SIZE			= "1312"; // 
	public final static String APNS_INVALID_TOKEN					= "1313"; // 
	public final static String APNS_UNKNOWN_ERROR					= "1314"; //
	
	// FeedBack
	public final static String FEEDBACK_EXCEPTION        			= "1315"; // FeedBack ó�� �� ���ܰ� �߻��߽��ϴ�.
	public final static String FEEDBACK_APP_NOTFOUND     			= "1316"; // FeedBack ��� ������ ����ϴ�.
	public final static String FEEDBACK_CONNECTION_ERROR 			= "1317"; // FeedBack ������ ���� �� ���� �߻��߽��ϴ�.
	public final static String FEEDBACK_NOTFOUND         			= "1318"; // FeedBack ��ȸ ��� ����ϴ�.
	public final static String FEEDBACK_REQ_ERROR        			= "1319"; // FeedBack ���� ��û �� ���� �߻��߽��ϴ�.
	
	// Certification
	public final static String CERT_EXCEPTION    					= "1320"; // ������ ó�� �� ���ܰ� �߻��߽��ϴ�.
	public final static String CERT_SAVE_ERROR   					= "1321"; // ������ ���� �� ���� �߻��߽��ϴ�.
	public final static String CERT_DELETE_ERROR 					= "1322"; // ������ ���� �� ���� �߻��߽��ϴ�.
	public final static String CERT_LOAD_ERROR   					= "1323"; // ������ �ε� �� ���� �߻��߽��ϴ�.
	
	// JAVAPNS Error
	public final static String APNS_CERTIFICATE_UNKNOWN 			= "1324";
	public final static String APNS_HANDSHAKE_REVOKE 				= "1325";
	public final static String APNS_HANDSHAKE_EXPIRE 				= "1326";
	public final static String APNS_SENDBOX_FAIL 					= "1327";
	public final static String APNS_SEND_FAIL 						= "1328";
	
	// Push Agent ���� �ڵ� 4�ڸ� 1400 ����
	public final static String AGT_APP_IS_NOT_EXIST					= "1400";
	public final static String SVC_MESSAGE_SIZE_EXCEED 				= "1036";  // Push Message Size is Exceed
	public final static String TARGET_MENU_URL = "http://www.google.com";
	public final static String PUSH_001 = "<따릉이> 자전거 반납 추천 대여소 <br/>주변에 추천 반납 대여소 확인하기";
}
