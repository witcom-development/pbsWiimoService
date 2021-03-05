package org.fincl.miss.client.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageVO;
import org.fincl.miss.server.message.parser.IncludeFieldsRepeatable;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.service.ServiceRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.fincl.miss.client.smart.consts.SmartConst;
import org.fincl.miss.client.smart.vo.*;
/**
 * Handles a client-side channel.
 */
@Sharable
public class SmartMessageParser{
    
	private static Logger logger = LoggerFactory.getLogger(SmartMessageParser.class);
    
	@Autowired
	protected ServiceRegister serviceRegister;

    private final static String SERVICE_ID_PREFIX = "Client_";
    
    private final static String BIZ_FIELD = "bizGubun";
    private final static String ORG_FIELD = "orgCode";
    private final static String COMMAND_ID_FIELD = "commandId";
    private final static String GUBUN_FILED = "gubun";
    private final static String INOUT_FILED = "inOutFlag";
    private final static String FILE_FILED = "fileName";
    private final static String RES_FILED = "resCode";
    private final static String REQ_MESSAGE = "reqMessage";
    
    private final static String REQUEST_VO_FIELDS_DEFINE = "requestFields";
    private final static String RESPONSE_VO_FIELDS_DEFINE = "responseFields";
    private final static String RESPONSE_VO_FAIL_FIELDS_DEFINE = "responseFailFields";
    private final static String REPEAT_VO_FIELDS_DEFINE = "repeatFields";
    private static Map<String, Integer> appHeaderField = null;
    private static Map<String, Integer> commandIdField = null;
    
    static {
        appHeaderField = new LinkedHashMap<String, Integer>();
        appHeaderField.put(BIZ_FIELD, 3);
        appHeaderField.put(ORG_FIELD, 8);
        appHeaderField.put(COMMAND_ID_FIELD, 4);
        appHeaderField.put(GUBUN_FILED, 1);
        appHeaderField.put(INOUT_FILED, 1);
        appHeaderField.put(FILE_FILED, 8);
        appHeaderField.put(RES_FILED, 3);
        
    }
    
    static {
        commandIdField = new LinkedHashMap<String, Integer>();
        
        commandIdField.put(COMMAND_ID_FIELD, 4);
    }
    
    public static MessageVO parse(ByteBuf xmessage) throws Exception {
        byte[] message = new byte[xmessage.readableBytes()];
        xmessage.readBytes(message);
        
        logger.debug("client response message [{}]" , new String(message) );
        logger.debug("response parsing...");
        // 앞에 1byte는 payload의 길이 필드여서 잘리냄
        message = Arrays.copyOfRange(message, 4, message.length);
    
        String serviceId = null;
        // frameControl, seqNum, commandId의 Vo에 대한 설정은 commandId의 추출 까지 끝나야 해당 Vo를 찾을 수 있기 때문에,
        // 제일 뒤에서 setProperty 함.
        String bizGubun = null;
        String orgCode = null;
        String inOutFlag = null;
        String fileName = null;
        String resCode = null;
        String commandId = null;
        String gubun = null;

        MessageVO interfaceVO = null;
        
        Map<String, String> repeatFields = null;
        
        try {
            ByteBuffer buffer = ByteBuffer.wrap(message);
            // 4번째 byte가 commandId
            Iterator<String> it = appHeaderField.keySet().iterator();
            
            if (buffer.remaining() > 0) {
                while (it.hasNext()) {
                    String sKey = it.next();
                    int length = appHeaderField.get(sKey);
                    byte[] bv = new byte[length];
                    buffer.get(bv);
                    String sVal = new String(bv); // new String(buffer.get(bv).array());
      //              System.out.println("h>>>" + sKey + "=" + sVal + " length ==>" + length + "(" + buffer.remaining() + ")");
                    if (StringUtils.equals(sKey, BIZ_FIELD)) {
                    	bizGubun = sVal;
                    }else if (StringUtils.equals(sKey, ORG_FIELD)) {
                    	orgCode = sVal;
                    }else if (StringUtils.equals(sKey, COMMAND_ID_FIELD)) {
                    	commandId = sVal;
                    }else if (StringUtils.equals(sKey, GUBUN_FILED)) {
                    	gubun = sVal;
                    }else if (StringUtils.equals(sKey, INOUT_FILED)) {
                    	inOutFlag = sVal;
                    }else if (StringUtils.equals(sKey, FILE_FILED)) {
                    	fileName = sVal;
                    }else if (StringUtils.equals(sKey, RES_FILED)) {
                    	resCode = sVal;
                    }
                }
            }
         
            serviceId = SERVICE_ID_PREFIX + commandId;
            ServiceMessageHeaderContext.getMessageHeader().put(MessageHeader.SERVICE_ID, serviceId);
            Class<?> interfaceVOClazz =  null;
            /**
             * 요청별 VO 분기 처리.
             */
            boolean validCommand = true;
            
            if(commandId.equals("0610")){
            	interfaceVOClazz = RequestResponseVo.class;
            }else if(commandId.equals("0640")){
            	interfaceVOClazz = RequestFileInfoVo.class;
            }else if(commandId.equals("0300")){
            	interfaceVOClazz = RequestFileInfoVo.class;
            }else{
            	validCommand = false;
            }
            if(validCommand){
            	interfaceVO = (MessageVO) interfaceVOClazz.newInstance();
            	// 반복필드여부 interface 구현체인지 확인하여,
            	// 반복필드 정보 획득
            	if (IncludeFieldsRepeatable.class.isAssignableFrom(interfaceVOClazz)) {
            		repeatFields = ((IncludeFieldsRepeatable) interfaceVO).getRepeatFields();
            	}
              
            	Field requestVOFields = interfaceVOClazz.getField(REQUEST_VO_FIELDS_DEFINE);
            	Map<String, Integer> fields = (Map<String, Integer>) requestVOFields.get(interfaceVO);
            	logger.debug("fields:" + fields);
              
	           // 헤더필드 처리가 끝난 후 나머지 필드 처리시 반복필드 포함여부에 따라 처리
				Iterator<String> itz = fields.keySet().iterator();
				if (buffer.remaining() > 0) {
					while (itz.hasNext()) {
						String sKey = itz.next();
				        if (repeatFields != null && repeatFields.containsKey(sKey)) {
				        	// 반복필드 정의에 포함된 키이면,
				        	// map의 value에 해당하는 값의 필드에 반복값이 있으므로 그 반큼 반복 정보 생성
				        	String repeatValue = BeanUtils.getProperty(interfaceVO, repeatFields.get(sKey));
				        	logger.debug("mk:re:{}" , repeatValue);
				        	List<String> rl = new ArrayList<String>();
				        	int iRepeatValue = Integer.parseInt(repeatValue, 16);
				        	for (int i = 0; i < iRepeatValue; i++) {
				        		int length = fields.get(sKey);
				        		byte[] bv = new byte[length];
								buffer.get(bv);
								rl.add(new String(bv));
				        	}
				        	BeanUtils.setProperty(interfaceVO, sKey, rl);
				        }
				        else {
				        	int length = fields.get(sKey);
				        	byte[] bv = new byte[length];
				        	buffer.get(bv);
				        	logger.debug("field : {}, length : {}, value : {}", sKey, length, new String(bv));
				        	BeanUtils.setProperty(interfaceVO, sKey, new String(bv));
				        }
					}
				}
				
            }
            
            BeanUtils.setProperty(interfaceVO, BIZ_FIELD, bizGubun);
            BeanUtils.setProperty(interfaceVO, ORG_FIELD, orgCode);
            BeanUtils.setProperty(interfaceVO, COMMAND_ID_FIELD, commandId);
            BeanUtils.setProperty(interfaceVO, GUBUN_FILED, gubun);
            BeanUtils.setProperty(interfaceVO, INOUT_FILED, inOutFlag);
            BeanUtils.setProperty(interfaceVO, FILE_FILED, fileName);
            BeanUtils.setProperty(interfaceVO, RES_FILED, resCode);
            BeanUtils.setProperty(interfaceVO, REQ_MESSAGE, new String(message));
            
            interfaceVO.setServiceId(serviceId);
            
            
        }catch(Exception e){}
        
        return interfaceVO;
    }
    
    public static byte[] build( MessageInterfaceVO messageInterfaceVo) throws MessageParserException {
        
        ByteArrayOutputStream responseOutputStream = null;
        List<byte[]> lBuf = new LinkedList<byte[]>();
        byte[] rMessage = null;
        
        try {
            Iterator<String> it = appHeaderField.keySet().iterator();
            String resFields = RESPONSE_VO_FIELDS_DEFINE;
            while (it.hasNext()) {
                String sKey = it.next();
                int length = appHeaderField.get(sKey);
                String val = BeanUtils.getProperty(messageInterfaceVo, sKey);
                lBuf.add(val.getBytes());
            }
            Field responseVOFields = messageInterfaceVo.getClass().getField(resFields);
            
            String repeatFields = REPEAT_VO_FIELDS_DEFINE;
             
            Map<String, Integer> fields = (Map<String, Integer>) responseVOFields.get(messageInterfaceVo);
            

            Field repeatVOFields = null;
            Map<String, String> repeats = null;
            
            try{
	            repeatVOFields = messageInterfaceVo.getClass().getField(repeatFields);
	            repeats = (Map<String, String>) repeatVOFields.get(messageInterfaceVo);
            }catch(Exception e){
            	e.printStackTrace();
            }   
            
            
            if(fields.size() >0){
	            Iterator<String> it2 = fields.keySet().iterator();
	            while (it2.hasNext()) {
	                String sKey = it2.next();
	                int length = fields.get(sKey);
	                String val = BeanUtils.getProperty(messageInterfaceVo, sKey);
	                if (val != null) {
	                    lBuf.add(val.getBytes());
	                }
	                
	            }
	            
	            if(repeats != null && repeats.size()>0){
	            	logger.debug("repeats=============");
	            	Iterator<String> it3 = repeats.keySet().iterator();
		            while (it3.hasNext()) {
		                String sKey = it3.next();
		                
		                String[] vals = BeanUtils.getArrayProperty(messageInterfaceVo, sKey);
		                if (vals != null && vals.length >0) {
		                	for(String val:vals){
		                		lBuf.add(val.getBytes());
		                	}
		               }
		            }
	            }
	            
	            responseOutputStream = new ByteArrayOutputStream();
	            for (byte[] el : lBuf) {
	                responseOutputStream.write(el);
	            }
	            
	            rMessage = responseOutputStream.toByteArray();
	            logger.debug("----REQUEST : {}" , new String(rMessage) );
            }else{
            	logger.debug("----REQUEST : NO_RESPONSE");
            }
        }
        catch (IllegalAccessException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.BICYCLE_MESSAGE_BUILD_ERROR, e);
        }
        catch (InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.BICYCLE_MESSAGE_BUILD_ERROR, e);
        }
        catch (NoSuchMethodException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.BICYCLE_MESSAGE_BUILD_ERROR, e);
        }
        catch (NoSuchFieldException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.BICYCLE_MESSAGE_BUILD_ERROR, e);
        }
        catch (SecurityException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.BICYCLE_MESSAGE_BUILD_ERROR, e);
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.BICYCLE_MESSAGE_BUILD_ERROR, e);
        }
        
        return rMessage;
    }
    
}