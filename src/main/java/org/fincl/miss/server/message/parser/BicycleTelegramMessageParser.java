package org.fincl.miss.server.message.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.fincl.miss.server.util.KISA_SEED_ECB;
import org.fincl.miss.server.util.SeedCipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dkitec.cfood.core.CfoodException;

@Component
public class BicycleTelegramMessageParser extends MessageParser {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final static String SERVICE_ID_PREFIX = "Bicycle_";
    
    private final static String FRAME_CONTROL_FIELD = "frameControl";
    private final static String SEQ_NUM_FIELD = "seqNum";
    private final static String COMMAND_ID_FIELD = "commandId";
    private final static String REQ_MESSAGE = "reqMessage";
    
    private final static String REQUEST_VO_FIELDS_DEFINE = "requestFields";
    private final static String RESPONSE_VO_FIELDS_DEFINE = "responseFields";
    private final static String RESPONSE_VO_FAIL_FIELDS_DEFINE = "responseFailFields";
    
    private static Map<String, Integer> appHeaderField = null;
    private static Map<String, Integer> commandIdField = null;
    
    static {
        appHeaderField = new LinkedHashMap<String, Integer>();
        
        appHeaderField.put(FRAME_CONTROL_FIELD, 2);
        appHeaderField.put(SEQ_NUM_FIELD, 1);
    }
    
    static {
        commandIdField = new LinkedHashMap<String, Integer>();
        
        commandIdField.put(COMMAND_ID_FIELD, 1);
    }
    
    @EnableTraceLogging
    @Override
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] bMessage) throws MessageParserException {
    	
    	String userKey = "seoulbicycle.com";
		
		SeedCipher seed = new SeedCipher();
		logger.debug("source0 >>>> {}", DatatypeConverter.printHexBinary(bMessage));
    	
    	
    	//encText 암호문
        // 앞에 1byte는 payload의 길이 필드여서 잘리냄
        bMessage = Arrays.copyOfRange(bMessage, 2, bMessage.length);
        logger.debug("source1 >>>> {}", DatatypeConverter.printHexBinary(bMessage));
	/**
	 * 복호화	
	 */
        //bMessage = KISA_SEED_ECB.SEED_ECB_Decrypt(userKey.getBytes(), bMessage, 0, bMessage.length);
//        bMessage = seed.decrypt(bMessage, userKey.getBytes());
        logger.debug("source :: {}", DatatypeConverter.printHexBinary(bMessage));
        
        System.out.println(DatatypeConverter.printHexBinary(bMessage));
        String serviceId = null;
        // frameControl, seqNum, commandId의 Vo에 대한 설정은 commandId의 추출 까지 끝나야 해당 Vo를 찾을 수 있기 때문에,
        // 제일 뒤에서 setProperty 함.
        String frameControl = null;
        String seqNum = null;
        String commandId = null;
        
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
                    String sVal = DatatypeConverter.printHexBinary(bv); // new String(buffer.get(bv).array());
             //        System.out.println("h>>>" + sKey + "=" + sVal + " length ==>" + length + "(" + buffer.remaining() + ")");
                    if (StringUtils.equals(sKey, FRAME_CONTROL_FIELD)) {
                        frameControl = sVal;
                    }
                    else if (StringUtils.equals(sKey, SEQ_NUM_FIELD)) {
                        seqNum = sVal;
                    }
                }
            }
            
            Iterator<String> itx = commandIdField.keySet().iterator();
            if (buffer.remaining() > 0) {
                while (itx.hasNext()) {
                    String sKey = itx.next();
                    int length = commandIdField.get(sKey);
                    byte[] bv = new byte[length];
                    buffer.get(bv);
                    commandId = DatatypeConverter.printHexBinary(bv);
                }
            }
            serviceId = SERVICE_ID_PREFIX + commandId;	//Bicycle + protocol 의 cmd id 
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
            
            System.out.println("fields:" + fields);
            System.out.println("mk:" + repeatFields);
            
            // 헤더필드 처리가 끝난 후 나머지 필드 처리시 반복필드 포함여부에 따라 처리
            Iterator<String> itz = fields.keySet().iterator();
            if (buffer.remaining() > 0) {
                while (itz.hasNext()) {
                    String sKey = itz.next();
                    if (repeatFields != null && repeatFields.containsKey(sKey)) {
                        // 반복필드 정의에 포함된 키이면,
                        // map의 value에 해당하는 값의 필드에 반복값이 있으므로 그 반큼 반복 정보 생성
                        String repeatValue = BeanUtils.getProperty(interfaceVO, repeatFields.get(sKey));
                        System.out.println("mk:re:" + repeatValue);
                        List<String> rl = new ArrayList<String>();
                        int iRepeatValue = Integer.parseInt(repeatValue, 16);
                        for (int i = 0; i < iRepeatValue; i++) {
                            int length = fields.get(sKey);
                            byte[] bv = new byte[length];
                            buffer.get(bv);
                            rl.add(DatatypeConverter.printHexBinary(bv));
                        }
                        BeanUtils.setProperty(interfaceVO, sKey, rl.toArray());
                    }
                    else {
                        int length = fields.get(sKey);
                        byte[] bv = new byte[length];
                        buffer.get(bv);
                        BeanUtils.setProperty(interfaceVO, sKey, DatatypeConverter.printHexBinary(bv));
                    }
                }
            }
            
            BeanUtils.setProperty(interfaceVO, FRAME_CONTROL_FIELD, frameControl);
            BeanUtils.setProperty(interfaceVO, SEQ_NUM_FIELD, seqNum);
            BeanUtils.setProperty(interfaceVO, COMMAND_ID_FIELD, commandId);
            BeanUtils.setProperty(interfaceVO, REQ_MESSAGE, DatatypeConverter.printHexBinary(bMessage));
            
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
        
        logger.debug("start : BicycleTelegramMessageParser");
        
        try {
            Iterator<String> it = appHeaderField.keySet().iterator();
            String resFields = RESPONSE_VO_FIELDS_DEFINE;
            while (it.hasNext()) {
                String sKey = it.next();
                int length = appHeaderField.get(sKey);
                String val = BeanUtils.getProperty(messageInterfaceVo, sKey);
                logger.debug("response control : getProperty sKey{} : val ={}",sKey,val);
                lBuf.add(DatatypeConverter.parseHexBinary(val));
                if(val.equals("4301")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("0301")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }else if(val.equals("8301")){
                	resFields = RESPONSE_VO_FAIL_FIELDS_DEFINE;
                }
            }
            
            logger.debug("end : BicycleTelegramMessageParser");
            
            Iterator<String> itz = commandIdField.keySet().iterator();
            while (itz.hasNext()) {
                String sKey = itz.next();
                int length = commandIdField.get(sKey);
                String val = BeanUtils.getProperty(messageInterfaceVo, sKey);
                
                logger.debug("BicycleTelegramMessageParse commandIdField  BeanUtils.getProperty skey={} : val={}",sKey,val);
                		
                lBuf.add(DatatypeConverter.parseHexBinary(val));
            }
            
            Field responseVOFields = messageInterfaceVo.getClass().getField(resFields);
            Map<String, Integer> fields = (Map<String, Integer>) responseVOFields.get(messageInterfaceVo);
            
            Iterator<String> it2 = fields.keySet().iterator();
            while (it2.hasNext()) {
                String sKey = it2.next();
                int length = fields.get(sKey);
                String val = BeanUtils.getProperty(messageInterfaceVo, sKey);
                
                logger.debug("BicycleTelegramMessageParse responseVOFields  BeanUtils.getProperty skey={} :val={}",sKey,val);
                
                if (val != null) {
                    lBuf.add(DatatypeConverter.parseHexBinary(val));
                }
                
            }
            
            
            responseOutputStream = new ByteArrayOutputStream();
            for (byte[] el : lBuf) {
                responseOutputStream.write(el);
            }
            
            rMessage = responseOutputStream.toByteArray();
            
            logger.debug("BicycleTelegramMessageParse QR_RESPONSE to Client  >>>> {}", DatatypeConverter.printHexBinary(rMessage));      
    //        System.out.println("----RESPONSE : " + DatatypeConverter.printHexBinary(rMessage));
            
            String userKey = "seoulbicycle.com";
    		
    		SeedCipher seed = new SeedCipher();
    	/**
    	 * 암호화	
    	 */
//        	rMessage = seed.encrypt(new String(rMessage,"UTF-8"), userKey.getBytes(), "UTF-8");
    		//rMessage = KISA_SEED_ECB.SEED_ECB_Encrypt(userKey.getBytes(), rMessage, 0, rMessage.length); 
            
        }catch (UnsupportedEncodingException e) {
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
