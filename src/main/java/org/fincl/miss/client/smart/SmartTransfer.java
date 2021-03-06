package org.fincl.miss.client.smart;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.fincl.miss.client.handler.SmartClientHandler;
import org.fincl.miss.client.handler.SmartClientInitializer;
import org.fincl.miss.client.handler.SmartMessageParser;
import org.fincl.miss.client.smart.consts.SmartConst;
import org.fincl.miss.client.smart.vo.RequestCheckInfoVo;
import org.fincl.miss.client.smart.vo.RequestFileInfoVo;
import org.fincl.miss.client.smart.vo.RequestFileVo;
import org.fincl.miss.client.smart.vo.RequestResponseVo;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.inbound.ListenInBoundChannel;
import org.fincl.miss.server.codec.tcp.StxEtxFrameDecoder;
import org.fincl.miss.server.codec.tcp.StxEtxFrameEncoder;
import org.fincl.miss.server.exeption.BizServiceException;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.message.GuidTopicMessage;
import org.fincl.miss.server.message.GuidTopicMessageListener;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageVO;
import org.fincl.miss.server.scheduler.job.transfer.vo.TransferVO;
import org.fincl.miss.server.util.StringUtil;
import org.fincl.miss.server.util.EnumCode.AutoStartYn;
import org.fincl.miss.server.util.EnumCode.DataRawType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.ITopic;

public class SmartTransfer {
    
	
	char lineFeed = 0x0a;
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private String host;
    private String port;
    
    private String fileDate;	//???????????? ???????????? ???????????? ???????????? ?????? fileDate ??????_20171113_JJH
    
    private EventLoopGroup group;
    
    private Channel channel;
    private ChannelFuture channelFuture;
    
    private LinkedBlockingQueue<byte[]> responseQueue = new LinkedBlockingQueue<byte[]>();
    
    private String fileName ;
    
    private String headerDate;
    
    private List<TransferVO> list;
    
    private int blockNo = 0;
    
    private int maxBlockNo = 0;
    
    private int sequenceNo =0;
    
    public List<TransferVO> getList() {
		return list;
	}

	public void setList(List<TransferVO> list) {
		this.list = list;
	}

	public SmartTransfer(String host, String port, String fileDate, String headerDate) {	// ???????????? ???????????? ???????????? ???????????? ?????? fileDate ??????_20171113_JJH
        this.host = host;
        this.port = port;
        this.fileDate = fileDate;	// ???????????? ???????????? ???????????? ???????????? ?????? fileDate ??????_20171113_JJH
        this.headerDate = headerDate; // ???????????? ???????????? ???????????? ???????????? ?????? fileDate ??????_20171120_JJH
  //      init();
    }
    
    public void init() {
        
        try {
            group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.handler(new SmartClientInitializer(this.host,this.port,null){
            	@Override
                public void initChannel(SocketChannel ch) {
                    ChannelPipeline pipeline = ch.pipeline();
                    
                    ListenInBoundChannel extChannel = new ListenInBoundChannel(null, null);
                    extChannel.setAutoStartYnEnum(AutoStartYn.YES);
                    extChannel.setTxRawDataTypeEnum(DataRawType.STRING);
                    extChannel.setHeaderLengthSize(4);
                    extChannel.setStxSize(0);
                    extChannel.setEtxSize(0);
                 //   extChannel.setStxHex("");
                 //   extChannel.setEtxHex("");
                    pipeline.addLast(new StxEtxFrameDecoder(extChannel));
                    pipeline.addLast(new StxEtxFrameEncoder(extChannel));
                    
                    pipeline.addLast(new SmartClientHandler()
                    {
                    	 @Override
                         protected void close() {
                    		 System.out.println("call close");
                    		 callClose();
                    	//	 group.shutdownGracefully();
                    	 }
                    	 
                    	 @Override
						protected void sendFileInfo(boolean send) {
                    		 callSendFileInfo(send);
                    	 }
                    	 @Override
                    	 protected void sendFile() throws Exception{
                    		 callSendFile(true); 
                    	 }
                    	 
                    	 @Override
                    	 protected void endRequest(boolean send) throws Exception{
                    		 /* BlockNo2??? ???????????? ?????? ?????? ??????_20160904_JJH_START
                    		 callEndRequest(send);   
                    		 */	 //BlockNo2??? ???????????? ?????? ?????? ??????_20160904_JJH_END
                    		 dataSendContineYn(send);
                    	 }
                    });
                }
            }); 
            /*{
                
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    byte[] bRes = new byte[msg.readableBytes()];
                    msg.readBytes(bRes);
                    responseQueue.add(bRes);
                }

            });
*/            
         // Start the connection attempt.
            channelFuture = b.connect(host, Integer.parseInt(port)).sync();
            
            channel = channelFuture.channel();

            
            
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_CONNECTION_ERROR, e);
        }
    }
    
    /**
     * ???????????? ??? ???????????? ??????.
     * ????????? ?????? ??? Bean?????? ?????? ??????.
     * @param msgVo
     */
    private void bizProcess(MessageVO msgVo){
    	RequestCheckInfoVo vo = (RequestCheckInfoVo)msgVo;
    }
    
    /**
     * ????????????
     */
    public void start(){
    	
    	if(list == null || list.size() == 0){
    		logger.debug("################### ?????????????????? (????????? ?????? ??????)");
    		/**
    		 * ?????? ??????...??????.
    		 * 
    		 */
        }else{
	    	logger.debug("################### ?????????????????? (???????????????--> ??????)");
	    	
	    	
	    	DateFormat sdFormat = new SimpleDateFormat("MMdd");
	        Date nowDate = new Date();
	        // String tempDate = sdFormat.format(nowDate);	???????????? ???????????? ???????????? ???????????? ?????? fileDate ??????_20171113_JJH
	    	String tempDate = fileDate;
	        
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(SmartConst.SEND_FILE).append(tempDate);
	    	/**
	    	 * ????????? ??????.
	    	 */
	    	this.fileName = sb.toString();
	    	
	    	sdFormat = new SimpleDateFormat("MMddHHmmss");
	        nowDate = new Date();
	        tempDate = sdFormat.format(nowDate);
	        
	    	RequestResponseVo vo = new RequestResponseVo();
	        vo.setBizGubun(SmartConst.BIZ_GUBUN);
	        vo.setOrgCode(SmartConst.BIKE_ORG_CODE);
	        vo.setCommandId(SmartConst.CommandReq.START);
	        vo.setGubun(SmartConst.GUBUN);
	        vo.setInOutFlag(SmartConst.IN_OUT_FLAG);
	        vo.setFileName("        ");
	        vo.setResCode(SmartConst.ResCode.OK);
	        vo.setSendDtm(tempDate);
	        vo.setBizInfo(SmartConst.Biz.START);
	        sb.setLength(0);
	        StringUtil.writeTailedSpace(sb,SmartConst.userId, 20);
	        vo.setSendName(sb.toString());
	        sb.setLength(0);
	        StringUtil.writeTailedSpace(sb,SmartConst.userPwd, 16);
	        vo.setSendPwd(sb.toString());
	        logger.debug(" sendValue m:: {}" , vo);
	    	
	        try {
				this.sendAndReceiveVo(vo);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }
    
    
    /**
     * ????????????
     */
    public void callEndRequest(boolean send){
    	
    	
    	logger.debug("################### ?????????????????? (???????????????--> ??????)");
    	logger.debug("################### blockNo, maxBlockNo ===> " + blockNo + ",  " +  maxBlockNo);
    	if(blockNo == maxBlockNo){
    		logger.debug("################### ?????????????????? (maxBlockNo == blockNo)");
    		DateFormat sdFormat = new SimpleDateFormat("MMdd");
            Date nowDate = new Date();
            String tempDate = sdFormat.format(nowDate);
            
        	StringBuffer sb = new StringBuffer();
        	
        	
        	sdFormat = new SimpleDateFormat("MMddHHmmss");
            nowDate = new Date();
            tempDate = sdFormat.format(nowDate);
            
        	RequestResponseVo vo = new RequestResponseVo();
            vo.setBizGubun(SmartConst.BIZ_GUBUN);
            vo.setOrgCode(SmartConst.BIKE_ORG_CODE);
            vo.setCommandId(SmartConst.CommandReq.START);
            vo.setGubun(SmartConst.GUBUN);
            vo.setInOutFlag(SmartConst.IN_OUT_FLAG);
           
            vo.setResCode(SmartConst.ResCode.OK);
            vo.setSendDtm(tempDate);
            /**
             * ????????? ?????? ?????? ?????? ??????.
             */
            if(send){
            	vo.setFileName("        ");
            	vo.setBizInfo(SmartConst.Biz.END);
            }else{
            	vo.setFileName(this.fileName);
            	if(this.maxBlockNo == this.blockNo){
                	vo.setBizInfo(SmartConst.Biz.EXIST_NONE);
                }else{
                	 vo.setBizInfo(SmartConst.Biz.EXIST_NEXT);	// ????????? ?????? ?????? ????????? ?????? ????????? ?????????... (???????????? ???????????? ????????????)_20160904_JJH
                	this.blockNo++;
                }
            }
            
            
            sb.setLength(0);
            StringUtil.writeTailedSpace(sb,SmartConst.userId, 20);
            vo.setSendName(sb.toString());
            sb.setLength(0);
            StringUtil.writeTailedSpace(sb,SmartConst.userPwd, 16);
            vo.setSendPwd(sb.toString());
            logger.debug(" sendValue m:: {}" , vo);
        	
            try {
    			this.sendAndReceiveVo(vo);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}else{
    		try {
    			this.blockNo++;
    			
    			logger.debug("################### ?????????????????? (maxBlockNo ++) ==> " + maxBlockNo + ", blockNo : " + blockNo);
    			
				callSendFile(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	

    }
    
    /**
     *  ???????????? ????????????(0630) 
     */
    public void callSendFileInfo(boolean send){

    	logger.debug("################### ???????????? ?????? ?????? (???????????????--> ??????)");
    	DateFormat sdFormat = new SimpleDateFormat("MMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        
    	StringBuffer sb = new StringBuffer();
    	RequestFileInfoVo vo = new RequestFileInfoVo();
        vo.setBizGubun(SmartConst.BIZ_GUBUN);
        vo.setOrgCode(SmartConst.BIKE_ORG_CODE);
        vo.setCommandId(SmartConst.CommandReq.FILE_INFO);
        vo.setGubun(SmartConst.GUBUN);
        vo.setInOutFlag(SmartConst.IN_OUT_FLAG);
        vo.setResCode(SmartConst.ResCode.OK);
        
      //sb.append(SmartConst.SEND_FILE).append(tempDate);
        vo.setFileName(this.fileName);
        vo.setRfileName(this.fileName);
       //seq ??? ?????? ?????? ?????? ?????????
        /**
         * TO_DO
         * ?????? ?????? ????????? ?????? ????????????...
         */
        int fileSize = 0;
        fileSize = list.size()*100+200;
        sb.setLength(0);
        StringUtil.writeLeadingZero(sb,String.valueOf(fileSize), 12);
        vo.setRfileSize(sb.toString());
        
        /**
         * ??? Block ?????? ??????
         * ??? Block ??? 100?????? seq???????????? 1seq??? 9?????? data??? ?????????.
         */
        int lstCnt = list.size();
        int byteCount = 0;
        if(send){
        	/**
        	 * ?????? ????????? ????????? ????????? ????????? ????????? ????????????.
        	 */
        	TransferVO header = new TransferVO();
            header.setSeq("H");
            list.add(0, header);
            TransferVO tailer = new TransferVO();
            tailer.setSeq("T");
            list.add(tailer);
            lstCnt = list.size(); 
            int max = 0;
            if(lstCnt<=(100*9)){
            	max = 1;
            }else{
            	max = lstCnt/(100*9);
            	if(lstCnt%900 >0){
            		max = max+1;
            	}
            }
            
            this.maxBlockNo = max;
            this.blockNo = 1;
            if(lstCnt < 10){
            	//1seq ?????? 9?????? data??? ?????????, list??? 9?????? ?????? ?????? ???????????? 7??? ??????.
            	byteCount = (list.size()-2)*100+200+39;
            }else{
            	byteCount = 939;
            }
        }else{
        	int startIdx= 0;
            int endIdx =0;
            if(this.maxBlockNo == this.blockNo){
            	//blockNo ?????? 2??????
            	startIdx = (blockNo-1)*100*9;
            	endIdx = list.size() - startIdx;
            	if(endIdx< 10){
            		byteCount = (endIdx-1)*100+50+39;
            	}else{
            		byteCount = 939;
            	}
            }else{
            	byteCount = 939;
            }
        }
        
        /**
         * byteCount??? ??? seq??? MAX size??? ??????????????? ???..
         * ???????????? ?????? ?????? 939??? FIX
         */
        byteCount = 939;
        sb.setLength(0);
        StringUtil.writeLeadingZero(sb,String.valueOf(byteCount), 4);
        vo.setByteCount(sb.toString());
        
        logger.debug(" sendValue m:: {}" , vo);
    	
        try {
			this.sendAndReceiveVo(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
    /**
     *  ?????? ????????????(0320) 
     * @throws Exception 
     */
    public void callSendFile(boolean send) throws Exception{
    	
    	responseQueue.clear();
        
        init();
    	
        logger.debug("################### ???????????? (???????????????--> ??????)");
    	DateFormat sdFormat = new SimpleDateFormat("MMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        logger.debug("step1");
    	StringBuffer sb = new StringBuffer();
    	RequestFileVo vo = new RequestFileVo();
        vo.setBizGubun(SmartConst.BIZ_GUBUN);
        vo.setOrgCode(SmartConst.BIKE_ORG_CODE);
        if(send){
        	vo.setCommandId(SmartConst.CommandReq.SEND_DATA);
        }else{
        	vo.setCommandId(SmartConst.CommandReq.RESEND);
        }
        vo.setGubun(SmartConst.GUBUN);
        vo.setInOutFlag(SmartConst.IN_OUT_FLAG);
        vo.setResCode(SmartConst.ResCode.OK);
    //    sb.append(SmartConst.SEND_FILE).append(tempDate);
        vo.setFileName(this.fileName);
        logger.debug("step2: {}", list.size());
    	
        int fileSize = list.size()*100+200;
        sb.setLength(0);
        StringUtil.writeLeadingZero(sb,String.valueOf(fileSize), 12);
        logger.debug("step3");
    	
        List<TransferVO> sendData  ;
        
        List<TransferVO> blockData ;
        
        int startIdx= 0;
        int endIdx =0;
        
        /* BlockNo2??? ???????????? ?????? ?????? ??????_20160904_JJH_START
        if(this.maxBlockNo == this.blockNo){
        	if(this.blockNo == 1){
        		startIdx = 0;
        	}else{
        		startIdx = (blockNo-1)*100*9;
        	}
        	endIdx = list.size();
        }else{
        	startIdx = (blockNo-1)*100*9;
        	endIdx = startIdx+100;
        }
        */
        
        if(this.blockNo == 1){
        	startIdx = 0;
        }else{
        	startIdx = (blockNo - 1) * 100 * 9;
        }
        
        if(this.blockNo == this.maxBlockNo){
        	endIdx = list.size();
        }else{
        	if(startIdx == 0){
        		endIdx = 900;
        	}else{
        		endIdx = (startIdx) + (100 * 9);
        	}
        }
        
        // BlockNo2??? ???????????? ?????? ?????? ??????_20160904_JJH_END
        
        logger.debug("step4");
    	
    	blockData = this.list.subList(startIdx, endIdx);

        List<String> sData = new ArrayList<String>();
        StringBuffer sb1 = new StringBuffer();
        
        int seq = 0;
        int sendCount = 0;
        int fiSize = 0;
        logger.debug("step5");
        for(TransferVO tv : blockData){
        	logger.debug("step5-1");
        	sendCount++;
        	if(tv.getSeq().equals("H")){
        		//	sData.add(getDateHeader(98));	// ???????????? ???????????? ?????? Header??????_20171120_JJH
        		sData.add(getDateHeader(98, headerDate));
        		fiSize = fiSize+100;
        	}else if(tv.getSeq().equals("T")){
        		sData.add(getTailer(88));
        		fiSize = fiSize+100;
        	}else{
        		sb1.setLength(0);
        		sb1.append("D");
        		sb1.append(StringUtil.addZero(tv.getSeq(), 10));
        		sb1.append(StringUtil.addSpace(String.valueOf(tv.getUsrSeq()), 20));
        		sb1.append(StringUtil.addSpace(tv.getMbCardNo(), 68));
        		sb1.append(this.lineFeed);
        		sData.add(sb1.toString());
        		fiSize = fiSize+100;
        	}
        	if(sendCount ==9 || tv.getSeq().equals("T")){
        		seq++;
        		this.sequenceNo = seq;
        		/**
        		 * ??????.
        		 */
        		vo.setBlockNo(StringUtil.addZero(String.valueOf(blockNo),4));
        		vo.setSeqNo(StringUtil.addZero(String.valueOf(seq),3));
        		vo.setSize(StringUtil.addZero(String.valueOf(fiSize),4));
        		vo.setData(sData.toArray(new String[sData.size()]));
        		
        		String[] dg = vo.getData();
        		for(String s:dg){
        			logger.debug("data :::{}",s);
        		}
        		
        		sendCount = 0;
        		fiSize =0;
        		channel.write(SmartMessageParser.build(vo));
        		logger.debug("send data :::{}",vo);
        		sData.clear();
        		
        	}
        }
        logger.debug("?????? ?????? ?????? ??????");
        /**
         * Block ?????????????????????, ?????? ?????? ??????.
         */
        
        channel.write(SmartMessageParser.build(callCheckComplete()));
        channel.flush();
        try {
            channelFuture.sync();
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_SENDMESSAGE_ERROR, e);
        }
        finally {
            close();
        }
    }
    
    
    /**
     *  ???????????? ????????????(0630) : ???????????? (????????? ?????? ?????????)
     *  
     */
    public void callSendFileInfoSample(boolean send){

    	logger.debug("################### ???????????? ?????? ?????? (???????????????--> ??????)");
    	DateFormat sdFormat = new SimpleDateFormat("MMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        
    	StringBuffer sb = new StringBuffer();
    	RequestFileInfoVo vo = new RequestFileInfoVo();
        vo.setBizGubun(SmartConst.BIZ_GUBUN);
        vo.setOrgCode(SmartConst.BIKE_ORG_CODE);
        vo.setCommandId(SmartConst.CommandReq.FILE_INFO);
        vo.setGubun(SmartConst.GUBUN);
        vo.setInOutFlag(SmartConst.IN_OUT_FLAG);
        vo.setResCode(SmartConst.ResCode.OK);
        
      //sb.append(SmartConst.SEND_FILE).append(tempDate);
        vo.setFileName(this.fileName);
        vo.setRfileName(this.fileName);
        
        int fileSize = list.size()*100+100;
        sb.setLength(0);
        StringUtil.writeLeadingZero(sb,String.valueOf(fileSize), 12);
        vo.setRfileSize(sb.toString());
        
        /**
         * ??? Block ?????? ??????
         * ??? Block ??? 100?????? seq???????????? 1seq??? 9?????? data??? ?????????.
         */
        int lstCnt = list.size();
        int byteCount = 0;
        if(send){
        	/**
        	 * ?????? ????????? ????????? ????????? ????????? ????????? ????????????.
        	 */
        	TransferVO header = new TransferVO();
            header.setSeq("H");
            list.add(0, header);
            TransferVO tailer = new TransferVO();
            tailer.setSeq("T");
            list.add(tailer);
            lstCnt = list.size(); 
            int max = 0;
            if(lstCnt<=(100*9)){
            	max = 1;
            }else{
            	max = lstCnt/(100*9);
            	if(lstCnt%900 >0){
            		max = max+1;
            	}
            }
            
            this.maxBlockNo = max;
            this.blockNo = 1;
            if(lstCnt < 10){
            	//1seq ?????? 9?????? data??? ?????????, list??? 9?????? ?????? ?????? ???????????? 7??? ??????.
            	byteCount = list.size()*100+39;
            }else{
            	byteCount = 939;
            }
        }else{
        	int startIdx= 0;
            int endIdx =0;
            if(this.maxBlockNo == this.blockNo){
            	//blockNo ?????? 2??????
            	startIdx = (blockNo-1)*100*9;
            	endIdx = list.size() - startIdx;
            	if(endIdx< 10){
            		byteCount = endIdx*100+39;
            	}else{
            		byteCount = 939;
            	}
            }else{
            	byteCount = 939;
            }
        }
        
        sb.setLength(0);
        StringUtil.writeLeadingZero(sb,String.valueOf(byteCount), 4);
        vo.setByteCount(sb.toString());
        
        logger.debug(" sendValue m:: {}" , vo);
    	
        try {
			this.sendAndReceiveVo(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
    }
    
    /**
     *  ?????? ????????????(0320) ????????????...(????????? ?????? ?????? ?????? ?????????)
     * @throws Exception 
     */
    public void callSendFileSample(boolean send) throws Exception{
    	
    	responseQueue.clear();
        
        init();
    	
        logger.debug("################### ???????????? (???????????????--> ??????)");
    	DateFormat sdFormat = new SimpleDateFormat("MMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        logger.debug("step1");
    	StringBuffer sb = new StringBuffer();
    	RequestFileVo vo = new RequestFileVo();
        vo.setBizGubun(SmartConst.BIZ_GUBUN);
        vo.setOrgCode(SmartConst.BIKE_ORG_CODE);
        if(send){
        	vo.setCommandId(SmartConst.CommandReq.SEND_DATA);
        }else{
        	vo.setCommandId(SmartConst.CommandReq.RESEND);
        }
        vo.setGubun(SmartConst.GUBUN);
        vo.setInOutFlag(SmartConst.IN_OUT_FLAG);
        vo.setResCode(SmartConst.ResCode.OK);
    //    sb.append(SmartConst.SEND_FILE).append(tempDate);
        vo.setFileName(this.fileName);
        logger.debug("step2 : {}", list.size());
    	
        int fileSize = list.size()*100+100;
        sb.setLength(0);
        StringUtil.writeLeadingZero(sb,String.valueOf(fileSize), 12);
        logger.debug("step3");
    	
        List<TransferVO> sendData  ;
        
        List<TransferVO> blockData ;
        
        int startIdx= 0;
        int endIdx =0;
        if(this.maxBlockNo == this.blockNo){
        	if(this.blockNo == 1){
        		startIdx = 0;
        	}else{
        		startIdx = (blockNo-1)*100*9;
        	}
        	endIdx = list.size();
        }else{
        	startIdx = (blockNo-1)*100*9;
        	endIdx = startIdx+100;
        }

        logger.debug("step4");
    	blockData = this.list.subList(startIdx, endIdx);

        List<String> sData = new ArrayList<String>();
        StringBuffer sb1 = new StringBuffer();
        
        int seq = 0;
        int sendCount = 0;
        int fiSize = 0;
        logger.debug("step5");
        for(TransferVO tv : blockData){
        	logger.debug("step5-1 : {}",tv.getSeq() );
        	sendCount++;
        	if(tv.getSeq().equals("H")){
        		sData.add(getDateHeader(98));
        		fiSize = fiSize+100;
        	}else if(tv.getSeq().equals("T")){
        		sData.add(getTailer(88));
        		fiSize = fiSize+100;
        	}else{
        		sb1.setLength(0);
        		sb1.append("D");
        		sb1.append(StringUtil.addZero(tv.getSeq(), 10));
        		sb1.append(StringUtil.addSpace(String.valueOf(tv.getUsrSeq()), 20));
        		sb1.append(StringUtil.addSpace(tv.getRideDttm(), 14));
        		sb1.append(StringUtil.addSpace(tv.getTransportCd(), 1));
        		sb1.append(StringUtil.addSpace(tv.getAlightDttm(), 53));
        		sb1.append(this.lineFeed);
        		sData.add(sb1.toString());
        		fiSize = fiSize+100;
        	}
        	if(sendCount ==9 || tv.getSeq().equals("T")){
        		seq++;
        		this.sequenceNo = seq;
        		/**
        		 * ??????.
        		 */
        		vo.setBlockNo(StringUtil.addZero(String.valueOf(blockNo),4));
        		vo.setSeqNo(StringUtil.addZero(String.valueOf(seq),3));
        		vo.setSize(StringUtil.addZero(String.valueOf(fiSize),4));
        		vo.setData(sData.toArray(new String[sData.size()]));
        		
        		String[] dg = vo.getData();
        		for(String s:dg){
        			logger.debug("data :::{}",s);
        		}
        		
        		sendCount = 0;
        		fiSize =0;
        		channel.write(SmartMessageParser.build(vo));
        		logger.debug("send data :::{}",vo);
        		sData.clear();
        		
        	}
        }
        logger.debug("?????? ?????? ?????? ??????");
        /**
         * Block ?????????????????????, ?????? ?????? ??????.
         */
        
        channel.write(SmartMessageParser.build(callCheckComplete()));
        channel.flush();
        try {
            channelFuture.sync();
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_SENDMESSAGE_ERROR, e);
        }
        finally {
            close();
        }
    }
    
    private void sendFileData(RequestFileVo vo){
    	try {
			this.sendVo(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     *  ?????? ?????? ??????(0620) 
     */
    public RequestCheckInfoVo callCheckComplete(){

    	logger.debug("################### ?????? ?????? ?????? (???????????????--> ??????)");
    	DateFormat sdFormat = new SimpleDateFormat("MMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        
    	StringBuffer sb = new StringBuffer();
    	RequestCheckInfoVo vo = new RequestCheckInfoVo();
        vo.setBizGubun(SmartConst.BIZ_GUBUN);
        vo.setOrgCode(SmartConst.BIKE_ORG_CODE);
        vo.setCommandId(SmartConst.CommandReq.CHK_MISSING);
        vo.setGubun(SmartConst.GUBUN);
        vo.setInOutFlag(SmartConst.IN_OUT_FLAG);
        vo.setResCode(SmartConst.ResCode.OK);
        
      //sb.append(SmartConst.SEND_FILE).append(tempDate);
        vo.setFileName(this.fileName);
        try{
        	vo.setRecentBlockNo(StringUtil.addZero(String.valueOf(this.blockNo), 4));
       		vo.setRecentSeqNo(StringUtil.addZero(String.valueOf(this.sequenceNo), 3));
       		
       		logger.debug(" sendValue m:: {}" , vo);
    	
       } catch (Exception e) {
			e.printStackTrace();
		}
        
        return vo;
        
    }
    
    /*	BlockNo2??? ???????????? ?????? ?????? ??????_20160904_JJH_END */
    public void dataSendContineYn(boolean flag){
		 
    	logger.debug("################### Data ???????????? ?????? ===> recentBlockNo : " + blockNo + ",  maxBlockNo : " + maxBlockNo + "###################");
    	
    	if(blockNo == maxBlockNo){
			 callEndRequest(flag); 
		 }else{
			 blockNo++;
			 
			 try {
				callSendFile(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
		 //	BlockNo2??? ???????????? ?????? ?????? ??????_20160904_JJH_END
    }
    
    public void send(byte[] bMessage) {
        responseQueue.clear();
        
        init();
        
        channelFuture = channel.writeAndFlush(bMessage);
        try {
            channelFuture.sync();
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_SENDMESSAGE_ERROR, e);
        }
        finally {
            close();
        }
    }
    
    public byte[] sendAndReceive(byte[] bMessage) {
        byte[] rMessage = null;
        logger.debug("request >>> {}",new String(bMessage));
        responseQueue.clear();
        
        init();
        
        channelFuture = channel.writeAndFlush(bMessage);
        try {
            channelFuture.sync();
            rMessage = responseQueue.take();
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_SENDMESSAGE_ERROR, e);
        }
        finally {
            close();
        }
        
        return rMessage;
    }
    
    public MessageVO sendAndReceiveVo(byte[] bMessage) throws Exception {
    	logger.debug("=======================");
    	byte[] rMsg = sendAndReceive(bMessage);
    	logger.debug("response: {}",rMsg);
    	ByteBuf bf = Unpooled.wrappedBuffer(rMsg);
    	return SmartMessageParser.parse(bf);
    }
    
    public void callClose(){
    	try {
    		channelFuture.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_CLOSE_ERROR, e);
        }
        group.shutdownGracefully();
        channel = null;
        group = null;
        channelFuture = null;
    }
    
    public MessageVO sendAndReceiveVo(MessageInterfaceVO messageInterfaceVo) throws Exception {
    	byte[] bMessage = SmartMessageParser.build(messageInterfaceVo);
    	return sendAndReceiveVo(bMessage);
    }
    
    public void sendVo(MessageInterfaceVO messageInterfaceVo) throws Exception {
    	byte[] bMessage = SmartMessageParser.build(messageInterfaceVo);
    	send(bMessage);
    }
    
    private void close() {
        try {
            channel.closeFuture().sync();
        }
        catch (InterruptedException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new BizServiceException(ErrorConstant.BIZ_SERVICE_TCP_CLOSE_ERROR, e);
        }
        group.shutdownGracefully();
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getPort() {
        return port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
    
	
	/**
	 * Data Header ??????
	 * @return
	 */
	private String getDateHeader(int blank){
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("H");
		
		DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        try{
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(nowDate);
			cal.add(Calendar.DATE, -4);
			
			/**
			 * 
			 */
			tempDate =  sdFormat.format(cal.getTime());
		
		}catch(Exception e){
			
		}
     
        StringUtil.writeTailedSpace(sb,tempDate, blank);
		
		sb.append(lineFeed);
		
        
		return sb.toString();
    }
	
	/**
	 * Data Header ??????
	 * @return
	 */
	private String getDateHeader(int blank, String headerDate){
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("H");
		
		/*DateFormat sdFormat = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = new Date();
        String tempDate = sdFormat.format(nowDate);
        
        try{
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(nowDate);
			cal.add(Calendar.DATE, -4);
			
			tempDate =  sdFormat.format(cal.getTime());
		
		}catch(Exception e){
			
		}
		*/
     
        // StringUtil.writeTailedSpace(sb,tempDate, blank);
		
		if(headerDate != null){
			System.out.println("##### getDateHeader ==> " + String.valueOf(headerDate));
		}
		
        StringUtil.writeTailedSpace(sb, headerDate, blank);
		
		sb.append(lineFeed);
		
        
		return sb.toString();
    }
	
	/**
	 * Data Tailer ??????
	 * @param length
	 * @return
	 * @throws Exception 
	 */
	private String getTailer(int blank) throws Exception{
		
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("T");
	    if(list.size()>2){
	    	sb.append(StringUtil.addZero(String.valueOf(list.size()-2), 10));
		}else{
			sb.append(StringUtil.addZero(String.valueOf(0), 10));
		}
	    sb.append(StringUtil.addSpace("", blank));
		sb.append(this.lineFeed);
		
		return sb.toString();
    }
	
	
}
