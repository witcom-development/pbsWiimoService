package org.fincl.miss.server.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.fincl.miss.server.scheduler.job.transfer.service.TransferMapper;
import org.fincl.miss.server.scheduler.job.transfer.vo.TransferVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransferScheduler {
	
	@Autowired
	private TransferMapper transferMapper;
	
	public void dbToSendFile() throws IOException {
		System.out.println("Log TransferScheduler :: dbToSendFile ======> Current time is :: "+ new Date());
		
		//현재날짜 구하기
		Date now = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String fileDate = format.format(now);
		String bwHeader = "";
		String bwTrailer = "";
		//String lineFeed = System.getProperty("line.seperator");
		char lineFeed = 0x0a;
		
		String filePreFix = "NDAN";
		String dirPathName = "/nas_link/spb/SEND_FILES/";
		
		File dirPath = new File(dirPathName);
	    if(!dirPath.exists()){
	    	dirPath.mkdir();
	    }
	    
	    String fileFullPathName = dirPathName + filePreFix + fileDate.substring(4, 8);
		
		List<TransferVO> list = transferMapper.getTransCardList();
		
		if(list.size() > 0){
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileFullPathName));
			
			bwHeader = "H" + getDate(-4) + setFiller(90) + lineFeed;
			bw.write(bwHeader);
			
			for(TransferVO cardVO:list){
				String data = "D" + setLPad(cardVO.getSeq(), 10, "0") + setRPad(cardVO.getUsrSeq().toString(), 20 , " ") + setRPad(cardVO.getMbCardNo(), 20 , " ") + setFiller(48) + lineFeed;
				bw.write(data);
			}
			
			bwTrailer = "T" + setLPad(list.size() + "", 10, "0") + setFiller(88) + lineFeed;
			bw.write(bwTrailer);
			
			bw.close();
		}
	}
	
	public void recvFileToDb() throws IOException {
		System.out.println("Log TransferScheduler :: recvFileToDb ======> Current time is :: "+ new Date());
		
		int result = 0;
		String searchDttm = "";
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat ndFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    	SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//현재날짜 구하기
    	Date now = new Date();
    	String fileDate = format.format(now);
		TransferVO transferVO = null;
		
		String filePreFix = "NEAN";
		String dirPathName = "/nas_link/spb/RECV_FILES/";
		String fileFullPathName = dirPathName + filePreFix + fileDate.substring(4, 8);
		String fileBackupFullPathName = dirPathName + "BACKUP/" + filePreFix + fileDate.substring(4, 8);
		
		File f = new File(fileFullPathName);
	    if(!f.isFile()){
	    	return;
	    }
	    
	    BufferedReader br = new BufferedReader(new FileReader(fileFullPathName));
	    
	    while(true) {
            String line = br.readLine();
            
            if(line == null){
            	break;
            } else {
            	if(line.substring(0,1).equals("H")){
            		System.out.println("RAW DATA H :: " + line);
            		
            		searchDttm =  Integer.toString(Integer.parseInt(line.substring(1,9)));
            		
            		try {
						Date cvSDate = format.parse(searchDttm);
						searchDttm = dFormat.format(cvSDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
            		
            		System.out.println("RAW DATA H searchDttm :: " + searchDttm);
            	}
            	
            	if(line.substring(0,1).equals("D")){
                	System.out.println("RAW DATA D :: " + line);
                	
                	//String recType = line.substring(0,1);
                	String seq =  Integer.toString(Integer.parseInt(line.substring(1,11)));
                	BigInteger usrSeq = new BigInteger(line.substring(11,31).replaceAll(" ", ""));
                	String rideDttm = line.substring(31,45);
                	String transportCd = line.substring(45,46);
                	String alightDttm = line.substring(46,60);
                	
                	try {
						Date cvRdDate = ndFormat.parse(rideDttm);
						Date cvAdDate = ndFormat.parse(alightDttm);
						
						rideDttm = sdFormat.format(cvRdDate);
						alightDttm = sdFormat.format(cvAdDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
                	
                	transferVO = new TransferVO();
                	transferVO.setSeq(seq);
                	transferVO.setUsrSeq(usrSeq);
                	transferVO.setRideDttm(rideDttm);
                	transferVO.setTransportCd(transportCd);
                	transferVO.setAlightDttm(alightDttm);
                	
                	result = transferMapper.addTransTmoneyHistory(transferVO);
                }
            }
        }
	    
        br.close();
        
        addTransMileage(searchDttm);
        
        fileMove(fileFullPathName, fileBackupFullPathName);
	}
	
	public void addTransMileage(String searchDttm) {
		System.out.println("Log TransferScheduler :: addTransMileage ======> Current time is :: "+ new Date());
		
		int result = 0;
		TransferVO transferVO = new TransferVO();
		transferVO.setSearchDttm(searchDttm);
		
		List<TransferVO> transMileList = transferMapper.getTransMileList(transferVO);
			
		if(transMileList.size() > 0){
			for(TransferVO mileVO:transMileList){
				transferVO = new TransferVO();
				
				transferVO.setUsrSeq(mileVO.getUsrSeq());
				transferVO.setMbCardSeq(mileVO.getMbCardSeq());
				transferVO.setMileageClsCd(mileVO.getMileageClsCd());
				transferVO.setMileagePoint(mileVO.getMileagePoint());
				transferVO.setRentHistSeq(mileVO.getRentHistSeq());
				transferVO.setTransferSeq(mileVO.getTransferSeq());
					
				result = transferMapper.addTransMileage(transferVO);
				result = transferMapper.setTransTmoney(transferVO);
			}
		}
	}
	
	public static void fileDelete(String deleteFileName) {
		File I = new File(deleteFileName);
		I.delete();
	}
	
	public static void fileMove(String inFileName, String outFileName) {
		try {
			FileInputStream fis = new FileInputStream(inFileName);
			FileOutputStream fos = new FileOutputStream(outFileName);
			int data = 0;
			
			while((data=fis.read())!=-1) {
				fos.write(data);
			}
			fis.close();
			fos.close();
		   
			//복사한뒤 원본파일을 삭제함
			fileDelete(inFileName);
	   
		} catch (IOException e) {
			e.printStackTrace();
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
	
//	public static void tcpBatchProtocolServer() {
//		ServerSocket serverSocket = null;
//
//		try {
//			serverSocket = new ServerSocket(7777);
//			serverSocket.setSoTimeout(7000);
//			System.out.println(getTime() + " 서버가 준비되었습니다.");
//
//			while (true) {
//				try {
//					System.out.println(getTime() + " 연결요청을 기다립니다.");
//					
//					Socket clientSocket = serverSocket.accept();
//					System.out.println(getTime() + " " + clientSocket.getInetAddress() + "로부터 연결요청이 들어왔습니다.");
//					System.out.println("getPort() : " + clientSocket.getPort());
//		            System.out.println("getLocalPort() : " + clientSocket.getLocalPort());
//
//					BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//					
//					System.out.println(getTime() + " "+ inputReader.readLine());
//					
//					OutputStream out = clientSocket.getOutputStream();
//		            DataOutputStream dos = new DataOutputStream(out);
//		 
//		            // 원격 소켓(remote socket)에 데이터를 보낸다.
//		            dos.writeUTF("[Notice] Test Message1 from Server.");
//		            System.out.println(getTime() + " 데이터를 전송하였습니다.");
//		            dos.close();
//		            
//				} catch (SocketTimeoutException e) {
//					//e.printStackTrace();
//				}
//			}
//
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} finally {
//			try {
//				if (serverSocket != null) {
//					serverSocket.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//    }
	
	/*transMsgStart
	transFileStart
	transFileData
	transFileLossCheck
	transFileLossData
	transFileEnd
	transMsgEnd*/

	
//	public static void tcpBatchProtocolClient() {
//		Socket socket = null;
//		String serverIp = "localhost";
//		int serverPort = 9999;
//		
//		// 파일경로
//		final String filePreFix = "NDAN";
//		String dirPathName = "D:/SEND_FILES/";
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
//			return;
//		}
//		
//		//현재날짜 구하기
//		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
//    	//Date now = new Date();
//    	
//		try {
//			//Thread.sleep(3000);
//			
//			System.out.println(getTime() + "서버에 연결중입니다. 서버 IP : " + serverIp + " PORT : " + serverPort);
//			socket = new Socket(serverIp, serverPort);
//
//			
//			//PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//			DataInputStream in = new DataInputStream(socket.getInputStream());
//			
//			System.out.println(getTime() + "서버로 메세지 전송 ");
//			
//			String transSendMsg = "";
//			String transReturnMsg = "";
//			//0600/001 업무개시요청
//			String transByteCnt = "0077";
//			String transType = "FTP";
//			String transOrgType = "00000000";
//			String transJobType = "0600";
//			String transDealType = "R";
//			String transFlag = "E";
//			String transFileName = setFiller(8);
//			String transReturnCd = "000";
//			
//			String transDttm = format.format(new Date());
//			String transCtrCd = "001";
//			String transSender = "SPB" + setFiller(17);
//			String transCipher = "spb" + setFiller(13);
//			
//			transSendMsg = transByteCnt + transType + transOrgType + transJobType + transDealType + transFlag 
//					+ transFileName + transReturnCd + transDttm + transCtrCd + transSender + transCipher;
//			
//			out.writeUTF(transSendMsg);
//			out.flush();
//			
//			//0610/001 업무개시응답
//			transReturnMsg = in.readUTF();
//			
//			transByteCnt = transByteCnt.substring(0, 4);
//			transType = transReturnMsg.substring(4, 7);
//			transOrgType = transReturnMsg.substring(7, 15);
//			transJobType = transReturnMsg.substring(15, 19);
//			transDealType = transReturnMsg.substring(19, 20);
//			transFlag = transReturnMsg.substring(20, 21);
//			transFileName = transReturnMsg.substring(21, 29);
//			transReturnCd = transReturnMsg.substring(29, 32); //000:정상
//			transDttm = transReturnMsg.substring(32, 42);
//			transCtrCd = transReturnMsg.substring(42, 45); //001:개시, 002:다음파일, 003:파일없음, 004:종료
//			transSender = transReturnMsg.substring(45, 65);
//			transCipher = transReturnMsg.substring(65);
//			
//			System.out.println(getTime() + "서버로부터 받은 메세지  : " + transByteCnt  + ":" + transType  + ":" + transOrgType  
//					+ ":" + transJobType + ":"  + transDealType + ":" + transFlag + ":" + transFileName + ":" + transReturnCd + ":" + transDttm 
//					+ ":" + transCtrCd + ":" + transSender + ":" + transCipher);
//			
//			Thread.sleep(1000);
//			
//			transSendMsg = "";
//			transReturnMsg = "";
//			//0630 파일정보 수신요청
//			transByteCnt = "0052";
//			transType = "FTP";
//			transOrgType = "00000000";
//			transJobType = "0630";
//			transDealType = "R";
//			transFlag = "E";
//			transFileName = fileName;
//			transReturnCd = "000";
//			
//			String transFileSize = setLPad(Long.toString(fileSize) , 12, "0");
//			String transMsgByteCnt = "0939";
//			
//			transSendMsg = transByteCnt + transType + transOrgType + transJobType + transDealType + transFlag 
//					+ transFileName + transReturnCd + transFileName + transFileSize + transMsgByteCnt;
//			
//			out.writeUTF(transSendMsg);
//			out.flush();
//			
//			
//			
//			
//			
//			in.close();
//			
//			System.out.println(getTime() + "연결을 종료합니다.");
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (socket != null){
//					socket.close();
//					System.out.println(getTime() + "연결이 종료되었습니다.");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
 
    static String getTime() {
        SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
        return f.format(new Date());
    }
}
