package org.fincl.miss.server.message.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageVO;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dkitec.cfood.core.CfoodException;

@Component
public class SmartTelegramMessageParser extends MessageParser {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final static String SERVICE_ID_PREFIX = "Smart_";
    
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
    private final static String REPEAT_VO_FIELDS_DEFINE = "repeatFields";
    private final static String RESPONSE_VO_FAIL_FIELDS_DEFINE = "responseWaitFields";
    private final static String RESPONSE_NO_FIELDS_DEFINE = "noreponseFields";
    
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
    
    @EnableTraceLogging
    @Override
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] bMessage) throws MessageParserException {
        
        // 앞에 1byte는 payload의 길이 필드여서 잘리냄
        bMessage = Arrays.copyOfRange(bMessage, 4, bMessage.length);
        logger.debug("source :: {}", new String(bMessage));
        
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
            ByteBuffer buffer = ByteBuffer.wrap(bMessage);
            // 4번째 byte가 commandId
            Iterator<String> it = appHeaderField.keySet().iterator();
            
            if (buffer.remaining() > 0) {
                while (it.hasNext()) {
                    String sKey = it.next();
                    int length = appHeaderField.get(sKey);
                    byte[] bv = new byte[length];
                    buffer.get(bv);
                    String sVal = new String(bv); // new String(buffer.get(bv).array());
           //         System.out.println("h>>>" + sKey + "=" + sVal + " length ==>" + length + "(" + buffer.remaining() + ")");
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
          /* 
            Iterator<String> itx = commandIdField.keySet().iterator();
            if (buffer.remaining() > 0) {
                while (itx.hasNext()) {
                    String sKey = itx.next();
                    int length = commandIdField.get(sKey);
                    byte[] bv = new byte[length];
                    buffer.get(bv);
                    commandId = new String(bv);
                }
            }*/
            serviceId = SERVICE_ID_PREFIX + commandId;
            ServiceMessageHeaderContext.getMessageHeader().put(MessageHeader.SERVICE_ID, serviceId);
            
            ServiceMetadata serviceMetadata = serviceRegister.getServiceMetadata(serviceId);
            Class<?> interfaceVOClazz = serviceMetadata.getParamClass();
            
            interfaceVO = (MessageVO) interfaceVOClazz.newInstance();
            
            // 반복필드여부 interface 구현체인지 확인하여,
            // 반복필드 정보 획득
            if (IncludeFieldsRepeatable.class.isAssignableFrom(interfaceVOClazz)) {
                repeatFields = ((IncludeFieldsRepeatable) interfaceVO).getRepeatFields();
            }
            
            
            Field requestVOFields = interfaceVOClazz.getField(REQUEST_VO_FIELDS_DEFINE);
            Map<String, Integer> fields = (Map<String, Integer>) requestVOFields.get(interfaceVO);
            
            Field repeatVOFields = null;
            Map<String, String> repeats = null;
            
            try{
            	repeatVOFields = interfaceVOClazz.getField(REPEAT_VO_FIELDS_DEFINE);
	            repeats = (Map<String, String>) repeatVOFields.get(interfaceVO);
            }catch(Exception e){
            	//e.printStackTrace();
            	logger.debug("no repeat fields");
            }   
            
            System.out.println("fields:" + fields);
            System.out.println("mk:" + repeats);
            
            // 헤더필드 처리가 끝난 후 나머지 필드 처리시 반복필드 포함여부에 따라 처리
            Iterator<String> itz = fields.keySet().iterator();
            if (buffer.remaining() > 0) {
                while (itz.hasNext()) {
                    String sKey = itz.next();
                    if (repeats != null && repeats.containsKey(sKey)) {
                        // 반복필드 정의에 포함된 키이면,
                        // map의 value에 해당하는 값의 필드에 반복값이 있으므로 그 반큼 반복 정보 생성
                    	// 반복필드는 100byte로 고정.
                        String repeatValue = BeanUtils.getProperty(interfaceVO, repeats.get(sKey));
                        System.out.println("mk:re:" + repeatValue);
                        List<String> rl = new ArrayList<String>();
                        int iRepeatValue = Integer.parseInt(repeatValue)/100;
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
            
            BeanUtils.setProperty(interfaceVO, BIZ_FIELD, bizGubun);
            BeanUtils.setProperty(interfaceVO, ORG_FIELD, orgCode);
            BeanUtils.setProperty(interfaceVO, COMMAND_ID_FIELD, commandId);
            BeanUtils.setProperty(interfaceVO, GUBUN_FILED, gubun);
            BeanUtils.setProperty(interfaceVO, INOUT_FILED, inOutFlag);
            BeanUtils.setProperty(interfaceVO, FILE_FILED, fileName);
            BeanUtils.setProperty(interfaceVO, RES_FILED, resCode);
            BeanUtils.setProperty(interfaceVO, REQ_MESSAGE, new String(bMessage));
            
            interfaceVO.setServiceId(serviceId);
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
        catch (InstantiationException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.BICYCLE_MESSAGE_BUILD_ERROR, e);
        }
        catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return interfaceVO;
        
    }
    
    @EnableTraceLogging
    @Override
    public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVo) throws MessageParserException {
        
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
             /*   if(val.equals("090")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("310")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("320")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("630")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("631")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("632")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("633")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("800")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }*/
            }
            /*
            Iterator<String> itz = commandIdField.keySet().iterator();
            while (itz.hasNext()) {
                String sKey = itz.next();
                int length = commandIdField.get(sKey);
                String val = BeanUtils.getProperty(messageInterfaceVo, sKey);
                lBuf.add(DatatypeConverter.parseHexBinary(val));
            }
            */
            Field responseVOFields = messageInterfaceVo.getClass().getField(resFields);
            
            Map<String, Integer> fields = (Map<String, Integer>) responseVOFields.get(messageInterfaceVo);
            
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
	            
	            
	            responseOutputStream = new ByteArrayOutputStream();
	            for (byte[] el : lBuf) {
	                responseOutputStream.write(el);
	            }
	            
	            rMessage = responseOutputStream.toByteArray();
	//            logger.debug("----RESPONSE : {}" , DatatypeConverter.printHexBinary(rMessage));
            }else{
    //        	logger.debug("----RESPONSE : NO_RESPONSE");
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
    
    /**
     * 오류발생시 에러 메시지 생성
     */
    @Override
    public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex) {
        byte[] bError = DatatypeConverter.parseHexBinary("EC");
        return bError;
    }
}
