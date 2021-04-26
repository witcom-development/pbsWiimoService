package org.fincl.miss.server.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.HttpsURLConnection;

import com.mainpay.sdk.net.HttpSendTemplate;
import com.mainpay.sdk.utils.MakeID;
import com.mainpay.sdk.utils.ParseUtils;


public class MainPayUtil {
	
	//private static final String FILE_PATH   = "D:/logs";
	private final String FILE_PATH   = "/MainPayLogs";
	
	private String APPROVE_URI				= "";
	private String CANCEL_URI      			= "";

	//생성자
	public MainPayUtil(){
		APPROVE_URI			= "https://dev-relay.mainpay.co.kr/v1/api/payments/payment/card-auto/trans";			//TEST
		//APPROVE_URI			= "https://relay.mainpay.co.kr/v1/api/payments/payment/card-auto/trans";				//REAL
		CANCEL_URI			= "https://dev-relay.mainpay.co.kr/v1/api/payments/payment/card-auto/cancel";			//TEST
		//CANCEL_URI			= "https://relay.mainpay.co.kr/v1/api/payments/payment/card-auto/cancel";				//REAL
		
	}
	
	ObjectMapper mapper = new ObjectMapper();
	java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	
	/**
	 * 자동결제 결제요청
	 * @param map
	 * @param logYn : Y/N
	 * @return
	 */
	public String approve(HashMap<String, String> parameters, String logYn)
	{
		String returnStr = "";
		
		String mbrNo = "100011";											// 테스트 가맹점 번호
		String apiKey = "U1FVQVJFLTEwMDAxMTIwMTgwNDA2MDkyNTMyMTA1MjM0";		// 테스트 apiKey
		
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat ( "yyyyMMddHHmmss");
		Date date = new Date();
		String TODAY = format.format(date);
		
		parameters.put("mbrNo", mbrNo);							// SPC Networks에서 부여한 가맹점번호 (상점 ID)
		
		parameters.put("mbrRefNo", MakeID.orderNo("WG_"+TODAY, 20));	// 가맹점에서 나름대로 정한 중복되지 않는 주문번호
		
		try 
		{
	    	returnStr = getSSLConnection( APPROVE_URI, parameters, apiKey);
	    	
	    	makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][결제요청] " +"[callUrl :" + APPROVE_URI +" ] " + mapper.writeValueAsString(parameters), logYn);
    		makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][결제요청 결과] " + returnStr, logYn);
	    } 
		catch (Exception e)
		{
	    	returnStr = "{\"code\":\"9999\",\"message\":\""+e.getMessage()+"\"}";
	    	System.out.println(e.toString());
	    }
		
		return returnStr;
	}
	
	/**
	 * 자동결제 취소요청
	 * @param map
	 * @param logYn : Y/N
	 * @return
	 */
	public String cancel(HashMap<String, String> parameters, String logYn)
	{
		String returnStr = "";
		
		String mbrNo = "100011";											// 테스트 가맹점 번호
		String apiKey = "U1FVQVJFLTEwMDAxMTIwMTgwNDA2MDkyNTMyMTA1MjM0";		// 테스트 apiKey
		
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat ( "yyyyMMddHHmmss");
		Date date = new Date();
		String TODAY = format.format(date);
		
		parameters.put("mbrNo", mbrNo);							// SPC Networks에서 부여한 가맹점번호 (상점 ID)
		
		parameters.put("mbrRefNo", MakeID.orderNo("WG_"+TODAY, 20));	// 가맹점에서 나름대로 정한 중복되지 않는 주문번호
		
		try 
		{
	    	returnStr = getSSLConnection( CANCEL_URI, parameters, apiKey);
	    	
	    	makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][결제 취소요청] " +"[callUrl :" + CANCEL_URI +" ] " + mapper.writeValueAsString(parameters), logYn);
    		makeServiceCheckApiLogFile("[" +dateformat.format(new java.util.Date()) + "][결제 취소요청 결과] " + returnStr, logYn);
	    } 
		catch (Exception e)
		{
	    	returnStr = "{\"code\":\"9999\",\"message\":\""+e.getMessage()+"\"}";
	    	System.out.println(e.toString());
	    }
		
		return returnStr;
	}
	
	public String getConnection(String apiUrl, String arrayObj, String kakaoAK) throws Exception
	{
		
		URL url 			  = new URL(apiUrl); 	// 요청을 보낸 URL
		String sendData 	  = arrayObj;
		HttpURLConnection con = null;
		StringBuffer buf 	  = new StringBuffer();
		String returnStr 	  = "";
		
		try {
			con = (HttpURLConnection)url.openConnection();
			
			con.setConnectTimeout(30000);		//서버통신 timeout 설정. 페이코 권장 30초
			con.setReadTimeout(30000);			//스트림읽기 timeout 설정. 페이코 권장 30초
			
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			con.setRequestProperty("Authorization", "KakaoAK " + kakaoAK);
			con.setDoOutput(true);
		    con.setRequestMethod("POST");
		    con.connect();
		    
		    // 송신할 데이터 전송.
		    DataOutputStream dos = new DataOutputStream(con.getOutputStream());
		    dos.write(sendData.getBytes("UTF-8"));
		    dos.flush();
		    dos.close();
		    
		    int resCode = con.getResponseCode();
		    
		    if (resCode == HttpURLConnection.HTTP_OK) {
		    
		    	BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			   	
				int c;
			    
			    while ((c = br.read()) != -1) {
			    	buf.append((char)c);
			    }
			    
			    returnStr = buf.toString();
			    br.close();
			    
		    } else {
		    	returnStr = "{ \"code\" : 9999, \"message\" : \"Connection Error\" }";
		    }
		    
		} catch (IOException e) {
			int result = 0;
		} finally {
		    con.disconnect();
		}
		
		return returnStr;
	}
	
	public String getSSLConnection(String apiUrl, HashMap<String, String> arrayObj, String apiKey) throws Exception
	{
		String responseJson = "";
		String resultMsg = "";
		
		StringBuffer buf 	   = new StringBuffer();
		String returnStr 	   = "";
		
		try {
			responseJson = HttpSendTemplate.post(apiUrl, arrayObj, apiKey);
		    // 송신할 데이터 전송.
			
			return responseJson;
		    
		} catch (IOException e) {
			e.printStackTrace();
			int result = 0;
		} finally {
		}
		
		return returnStr;
	}
	
	public void makeServiceCheckApiLogFile(String logText, String logYn) {
		
		if(logYn.equals("Y")){
			String filePath   = FILE_PATH;
		  	java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyyMMdd HH:mm:ss");
		  	String nowTotDate = dateformat.format(new java.util.Date());
		  	Integer nowdate = Integer.parseInt( nowTotDate.substring(0, 8) );
		   
			String fileName = "service_check_log_" + nowdate + ".txt"; //생성할 파일명
		  	String logPath = filePath + File.separator + fileName; 
		  
		  	File folder = new File(filePath); //로그저장폴더
		  	File f 		= new File(logPath);  //파일을 생성할 전체경로
		  
		  	try{
		  	
		  		if(folder.exists() == false) {
		   			folder.mkdirs();
				}

		   		if (f.exists() == false){
		    		f.createNewFile(); //파일생성
		   		}

		   		// 파일쓰기
		   		FileWriter fw = null;

		   		try {

		   			fw = new FileWriter(logPath, true); //파일쓰기객체생성
		   			fw.write(logText +"\n"); //파일에다 작성

		   		} catch(IOException e) {
		   			throw e;
		   		} finally {
		   			if(fw != null) fw.close(); //파일핸들 닫기
		   		}

		  	}catch (IOException e) { 
		  		int result = 0;
		   		//System.out.println(e.toString()); //에러 발생시 메시지 출력
		  	}
		}else{
			return;
		}
	}
	
	/*
	public static void main(String[] args)
    {
		System.out.println("Test");
        try {
        	System.out.println("Test");
        	HashMap<String, String> parameters = new HashMap<String, String>();
        	
        	
        	String billkey = "1234";
    		if( billkey != null && !"".equals(billkey)) {	// 빌링키 없음 실패		
    			parameters.put("billkey", billkey);	// 정기결제 인증 키
    		}
    		parameters.put("goodsId", "BIL_009");
    		parameters.put("goodsName", "추가과금");
    		parameters.put("amount", "1234");
        	
        	
        	approve(parameters,"Y");
        	System.out.println("Test");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.toString());
        }
        
    }
    */
		
	
}

