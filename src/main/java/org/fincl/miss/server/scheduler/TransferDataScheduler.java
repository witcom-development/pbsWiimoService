package org.fincl.miss.server.scheduler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
//import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.fincl.miss.client.smart.SmartTransferThread;
import org.fincl.miss.server.scheduler.annotation.ClusterSynchronized;
import org.fincl.miss.server.scheduler.job.transfer.service.TransferMapper;
import org.fincl.miss.server.scheduler.job.transfer.vo.TransferVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferDataScheduler {
	private Logger logger = LoggerFactory.getLogger(TransferDataScheduler.class);
	
	@Autowired
	private TransferMapper transferMapper;
	
	@Autowired
	private String smartIp;
	
	@Autowired
	private String threadTime;
	
	
	//@ClusterSynchronized( jobToken="tcpBatchProtocolClient")
	public void tcpBatchProtocolClient() throws Exception {
		logger.debug("******************************스마트 카드 배치 연동 시작.*******************************************");
//      String serverIp = "210.216.95.123";
//   	String serverIp = "210.216.95.124";
//		
//		int serverPort = 6100;
//		int timeout = 60000;
//		boolean result;
//		
//		// 파일경로
//		final String filePreFix = "NDAN";
//		String dirPathName = "/nas_link/spb/SEND_FILES/";
//		String fileName = "";
//		String fileFullPathName = "";
//		long fileSize = 0;
//		
//		File path = new File(dirPathName);
//		String fileList[] = path.list(new FilenameFilter() {
//		    @Override
//		    public boolean accept(File dir, String name) { 
//		        return name.startsWith(filePreFix);        // TEMP로 시작하는 파일들만 return 
//		    }
//		});
//		
//		if(fileList.length > 0){
//			fileName = fileList[0];
//			fileFullPathName = dirPathName + fileName;
//			
//			File oFile = new File(fileFullPathName);
//			fileSize = oFile.length();
//			
//			/*for(int i=0; i < fileList.length; i++){
//		        System.out.println(fileList[i]) ;
//		    }*/
//		} else {
//			System.out.println(getTime() + "전송할 파일이 없습니다.");
//			return ;
//		}
//		
//		//600:001 업무개시요청
//		for(int i=0; i <3; i++){
//			result = transMsgStart(serverIp, serverPort, timeout);
//			
//			if(result){
//				System.out.println(getTime() + "600(001) 업무개시요청 성공");
//				break;
//			} else {
//				System.out.println(getTime() + "600(001) 업무개시요 실패 = 재전솔");
//				if(i == 2){
//					System.out.println(getTime() + "600(001) 업무개시요 실패 = 종료");
//					return ;
//				}
//			}
//		}
//		
//		//630 파일정보수신요청
//		for(int i=0; i <3; i++){
//			result = transFileStart(serverIp, serverPort, timeout, fileName, fileSize);
//			
//			if(result){
//				System.out.println(getTime() + "630 파일정보수신요청 성공");
//				break;
//			} else {
//				System.out.println(getTime() + "630 파일정보수신요청 실패 = 재전솔");
//				if(i == 2){
//					System.out.println(getTime() + "630 파일정보수신요청 실패 = 종료");
//					return ;
//				}
//			}
//		}
//		
//		//320 DATA 처리
//		
//		
//		
//		System.out.println(getTime() + "다음 진행");
//		Thread t = new Thread(){
//            @Override
//           public void run(){
//               while(true){
            	   String host = this.smartIp;
            	   String port = "6100";
            	   String fileDate = "";	// 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171113_JJH
            	   String headerDate = "";	// 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171120_JJH
            	   int interval = Integer.parseInt(this.threadTime);
            	   System.out.println(getTime() + "서버에 연결중입니다. 서버 IP : " +host + " PORT : " + port + " interval : " + interval);
  //          	   SmartTransfer client = new SmartTransfer(host,port);
       	       
       	        //"/Users/civan/Downloads/SEND_FILE";
       	     
            	
//       	        List<TransferVO> data = new ArrayList<TransferVO>();
//       	        TransferVO header = new TransferVO();
       	   //     data.add(header);
       	        /**
       	         * 전송요청할 데이터 조회..( 스마트카드 요청용)
       	         */
//       	        List<TransferVO> list = new ArrayList<TransferVO>();
       	        List<TransferVO> list = transferMapper.getTransCardList();
       	        
       	        // 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171113_JJH
       	        if(list != null){
       	        	fileDate = String.valueOf(list.get(0).getFileDate());
       	        	headerDate = String.valueOf(list.get(0).getHeaderDate());
       	        	
       	        	System.out.println(getTime() + "전문요청 fileDate : " + fileDate);
       	        }
       	        
       	        
       	        
//       	        TransferVO tv = new TransferVO();
////       	        tv.setSeq("1");
////       	        tv.setUsrSeq(new BigInteger("99"));
////       	        tv.set
////       	        tv.setMbCardNo("1234445343231860");
////       	        
//       	        tv.setSeq("1");
//       	        tv.setUsrSeq(new BigInteger("7"));
//       	    	tv.setRideDttm("20150723063740");
//       	    	tv.setTransportCd("1");
//       	    	tv.setAlightDttm("20150723070915");
//       	        list.add(tv);
//       	        
//       	        TransferVO tv1 = new TransferVO();
//       	        tv1.setSeq("2");
//       	        tv1.setUsrSeq(new BigInteger("9"));
//       	    	tv1.setRideDttm("20150723114043");
//       	    	tv1.setTransportCd("1");
//       	    	tv1.setAlightDttm("20150723114519");
//       	        list.add(tv1);
//       	        
//       	        data.addAll(list);
       	       
       	     Runnable r = new SmartTransferThread(host, list, fileDate, headerDate);	// 대중교통 이용정보 전문요청 누락으로 인한 fileDate 추가_20171113_JJH
             Thread t = new Thread(r);
             t.start();
             t.join(interval);
//            System.exit(1); //non zero value to exit says abnormal termination of JVM
            
       	 //       client.setList(list); 
       	        
       	        
       	        
       	        /**
       	         * 전송요청할 데이터 조회..( 테스트서버 전달용)
       	         */
       	        
    //   	        client.start();
            	   
                
//               }
//           }
//       };
//      
//       t.start();
//       Thread.sleep(10000);
//       logger.debug("스마트카드 연동 배치 종료...");
//       System.exit(1); //non zero value to exit says abnormal termination of JVM

		
	}
	
	/*
	transFileData
	transFileLossCheck
	transFileLossData
	transFileEnd
	transMsgEnd
	*/
	
	public static boolean transFileStart(String serverIp, int serverPort, int timeout, String fileName, long fileSize) {
		boolean result = false;
		Socket socket = null;
    	
		try {
			System.out.println(getTime() + "서버에 연결중입니다. 서버 IP : " + serverIp + " PORT : " + serverPort);
			SocketAddress socketAddress = new InetSocketAddress(serverIp, serverPort);
			socket = new Socket();
			//socket.connect(socketAddress, timeout);
			socket.connect(socketAddress);
			socket.setSoTimeout(timeout);
			
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			System.out.println(getTime() + "서버로 메세지 전송 ");
			
			String transSendMsg = "";
			String transReturnMsg = "";
			//0630 파일정보수신요청
			String transByteCnt = "0052";
			String transType = "FTP";
			String transOrgType = "00000000";
			String transJobType = "0630";
			String transDealType = "R";
			String transFlag = "E";
			String transFileName = fileName;
			String transReturnCd = "000";
			String transInfoFileName = fileName;
			String transInfoFileSize = setLPad(Long.toString(fileSize) , 12, "0");
			String transInfoByteCnt = "0939";
			
			transSendMsg = transByteCnt + transType + transOrgType + transJobType + transDealType + transFlag 
					+ transFileName + transReturnCd + transInfoFileName + transInfoFileSize + transInfoByteCnt;
			
			out.writeUTF(transSendMsg);
			out.flush();
			
			//0640  파일정보수신응답
			transReturnMsg = in.readUTF();
			
			transByteCnt = transReturnMsg.substring(0, 4);
			transType = transReturnMsg.substring(4, 7);
			transOrgType = transReturnMsg.substring(7, 15);
			transJobType = transReturnMsg.substring(15, 19);
			transDealType = transReturnMsg.substring(19, 20);
			transFlag = transReturnMsg.substring(20, 21);
			transFileName = transReturnMsg.substring(21, 29);
			transReturnCd = transReturnMsg.substring(29, 32); //000:정상
			transInfoFileName = transReturnMsg.substring(32, 40);
			transInfoFileSize = transReturnMsg.substring(40, 52);
			transInfoByteCnt = transReturnMsg.substring(52, 56);
			
			System.out.println(getTime() + "서버로부터 받은 메세지  : " + transByteCnt  + ":" + transType  + ":" + transOrgType  
					+ ":" + transJobType + ":"  + transDealType + ":" + transFlag + ":" + transFileName + ":" + transReturnCd 
					+ ":" + transInfoFileName + ":" + transInfoFileSize + ":" + transInfoByteCnt);
			
			if(transReturnCd.equals("000")){
				result = true;
			}
			in.close();
			
			System.out.println(getTime() + "연결을 종료합니다.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null){
					socket.close();
					System.out.println(getTime() + "연결이 종료되었습니다.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static boolean transMsgStart(String serverIp, int serverPort, int timeout) {
		boolean result = false;
		//현재날짜 구하기
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		
		Socket socket = null;
    	
		try {
			System.out.println(getTime() + "서버에 연결중입니다. 서버 IP : " + serverIp + " PORT : " + serverPort);
			SocketAddress socketAddress = new InetSocketAddress(serverIp, serverPort);
			socket = new Socket();
			//socket.connect(socketAddress, timeout);
			socket.connect(socketAddress);
			socket.setSoTimeout(timeout);
			
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
			System.out.println(getTime() + "서버로 메세지 전송 ");
			
			String transSendMsg = "";
			String transReturnMsg = "";
			//0600/001 업무개시요청
			String transByteCnt = "0077";
			String transType = "FTP";
			String transOrgType = "00000000";
			String transJobType = "0600";
			String transDealType = "R";
			String transFlag = "E";
			String transFileName = setFiller(8);
			String transReturnCd = "000";
			
			String transDttm = format.format(new Date());
			String transCtrCd = "001";
			String transSender = "SPB" + setFiller(17);
			String transCipher = "spb" + setFiller(13);
			
			transSendMsg = transByteCnt + transType + transOrgType + transJobType + transDealType + transFlag 
					+ transFileName + transReturnCd + transDttm + transCtrCd + transSender + transCipher;
			
			out.writeUTF(transSendMsg);
			out.flush();
			
			//0610/001 업무개시응답
			transReturnMsg = in.readUTF();
			
			System.out.println(getTime() + transReturnMsg);
			
			transByteCnt = transReturnMsg.substring(0, 4);
			transType = transReturnMsg.substring(4, 7);
			transOrgType = transReturnMsg.substring(7, 15);
			transJobType = transReturnMsg.substring(15, 19);
			transDealType = transReturnMsg.substring(19, 20);
			transFlag = transReturnMsg.substring(20, 21);
			transFileName = transReturnMsg.substring(21, 29);
			transReturnCd = transReturnMsg.substring(29, 32); //000:정상
			transDttm = transReturnMsg.substring(32, 42);
			transCtrCd = transReturnMsg.substring(42, 45); //001:개시, 002:다음파일, 003:파일없음, 004:종료
			transSender = transReturnMsg.substring(45, 65);
			transCipher = transReturnMsg.substring(65);
			
			System.out.println(getTime() + "서버로부터 받은 메세지  : " + transByteCnt  + ":" + transType  + ":" + transOrgType  
					+ ":" + transJobType + ":"  + transDealType + ":" + transFlag + ":" + transFileName + ":" + transReturnCd + ":" + transDttm 
					+ ":" + transCtrCd + ":" + transSender + ":" + transCipher);
			
			if(transReturnCd.equals("000")){
				result = true;
			}
			in.close();
			
			System.out.println(getTime() + "연결을 종료합니다.");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null){
					socket.close();
					System.out.println(getTime() + "연결이 종료되었습니다.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	public static void tcpBatchProtocolServer() {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(7777);
			serverSocket.setSoTimeout(7000);
			System.out.println(getTime() + " 서버가 준비되었습니다.");

			while (true) {
				try {
					System.out.println(getTime() + " 연결요청을 기다립니다.");
					
					Socket clientSocket = serverSocket.accept();
					System.out.println(getTime() + " " + clientSocket.getInetAddress() + "로부터 연결요청이 들어왔습니다.");
					System.out.println("getPort() : " + clientSocket.getPort());
		            System.out.println("getLocalPort() : " + clientSocket.getLocalPort());

					BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					
					System.out.println(getTime() + " "+ inputReader.readLine());
					
					OutputStream out = clientSocket.getOutputStream();
		            DataOutputStream dos = new DataOutputStream(out);
		 
		            // 원격 소켓(remote socket)에 데이터를 보낸다.
		            dos.writeUTF("[Notice] Test Message1 from Server.");
		            System.out.println(getTime() + " 데이터를 전송하였습니다.");
		            dos.close();
		            
				} catch (SocketTimeoutException e) {
					//e.printStackTrace();
				}
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
	
	public static String setFiller(int size) {
		String str = "";
		
		for(int i = 0; i < size; i++) {
            str += " ";
        }
        return str;
    }
	
	public static String setRPad(String str, int size, String strFillText) {
        for(int i = (str.getBytes()).length; i < size; i++) {
            str += strFillText;
        }
        return str;
    }
	
	public static String setLPad(String str, int size, String strFillText) {
		for(int i = (str.getBytes()).length; i < size; i++) {
			str = strFillText + str;
        }
        return str;
    }
	
	public String getDate (int iDay) {
	    Calendar temp = Calendar.getInstance();    
	    temp.add (Calendar.DAY_OF_MONTH, iDay);
	     
	    int nYear = temp.get(Calendar.YEAR);
	    int nMonth = temp.get(Calendar.MONTH) + 1;
	    int nDay = temp.get(Calendar.DAY_OF_MONTH);
	     
	    StringBuffer sbDate = new StringBuffer();
	    sbDate.append(nYear);
	     
	    if(nMonth < 10){ 
	    	sbDate.append("0");
	    }
	    sbDate.append(nMonth);
	    
	    if(nDay < 10){ 
	        sbDate.append("0");
	    }
	    sbDate.append(nDay);
	    
	    return sbDate.toString();
	}
	
    static String getTime() {
        SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
        return f.format(new Date());
    }
}
