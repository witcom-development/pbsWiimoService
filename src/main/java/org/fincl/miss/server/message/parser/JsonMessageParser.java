package org.fincl.miss.server.message.parser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageVO;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.service.metadata.NotRegisteredServiceException;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.fincl.miss.server.util.CharsetConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dkitec.cfood.core.CfoodException;

@Component
public class JsonMessageParser extends FixedWrapMessageParser {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public JsonMessageParser() {
        
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] bMessage) throws MessageParserException {
        
        if (bMessage == null || bMessage.length == 0) {
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, "message empty.");
        }
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        Map<String, Object> mRequest;
        Map<String, Object> mWrapper;
        MessageVO interfaceVO = null;
        String serviceId = null;
        
        try {
            mRequest = mapper.readValue(bMessage, HashMap.class);
            mWrapper = (Map<String, Object>) mRequest.get(requestRoot);
            serviceId = (String) mWrapper.get(RequestFixedField.SERVICE_ID.getValue());
            
            if (serviceId == null) {
                throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_NOT_FOUND_SERVICE_ID, "serviceId is null.");
            }
            else {
                ServiceMessageHeaderContext.getMessageHeader().put(MessageHeader.SERVICE_ID, serviceId);
            }
            
            Map mParam = (Map) mWrapper.get(RequestFixedField.PARAM.getValue());
            ServiceMetadata serviceMetadata = serviceRegister.getServiceMetadata(serviceId);
            Class<?> interfaceVOClazz = serviceMetadata.getParamClass();
            interfaceVO = (MessageVO) interfaceVOClazz.newInstance();
            
            String id = (String) mWrapper.get(RequestFixedField.ID.getValue());
            if (StringUtils.isNotEmpty(id)) {
                interfaceVO.setId(id);
            }
            
            BeanUtils.populate(interfaceVO, mParam);
            interfaceVO.setServiceId(serviceId);
            if (interfaceVO.getId() == null) {
                interfaceVO.setId(interfaceVO.getMessageHeader().getId().toString());
            }
            
        }
        catch (NotRegisteredServiceException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new NotRegisteredServiceException(serviceId, null);
        }
        catch (JsonParseException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (JsonMappingException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (IllegalAccessException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (InstantiationException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        
        return interfaceVO;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVO) throws MessageParserException {
        
        String res = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map mMessageVo = null;
        
        try {
            mMessageVo = PropertyUtils.describe(messageInterfaceVO);
            
            ResponseIgnoreFiled[] responseIgnoreFiledValues = ResponseIgnoreFiled.values();
            for (ResponseIgnoreFiled field : responseIgnoreFiledValues) {
                mMessageVo.remove(field.getValue());
            }
            
            Map<String, Object> mWrapper = new HashMap<String, Object>();
            mWrapper.put(ResponseFixedField.ID.getValue(), messageInterfaceVO.getMessageHeader().getId());
            mWrapper.put(ResponseFixedField.CODE.getValue(), StringUtils.defaultIfEmpty(messageInterfaceVO.getResultCode(), ErrorConstant.SUCCESS));
            mWrapper.put(ResponseFixedField.MSG.getValue(), StringUtils.defaultIfEmpty(messageInterfaceVO.getResultMessage(), ""));
            mWrapper.put(ResponseFixedField.DATA.getValue(), mMessageVo);
            
            //
            Map<String, Object> mResponse = new HashMap<String, Object>();
            mResponse.put(responseRoot, mWrapper);
            res = mapper.writeValueAsString(mResponse);
        }
        catch (IllegalAccessException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (JsonGenerationException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (JsonMappingException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (IllegalArgumentException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (NoSuchMethodException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        
        byte[] bRes = null;
        
        try {
            bRes = CharsetConvertUtil.convert(res.getBytes(), Charset.defaultCharset(), Charset.forName(extChannel.getCharsetEnum().toString()));
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        
        return bRes;
    }
    
    @Override
    public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex) {
        byte[] bRes = null;
        Map<String, Object> mWrapper = new HashMap<String, Object>();
        mWrapper.put(ResponseFixedField.ID.getValue(), messageHeader.getId());
        mWrapper.put(ResponseFixedField.CODE.getValue(), ex.getCode());
        mWrapper.put(ResponseFixedField.MSG.getValue(), ex.getMessage());
        
        String res = null;
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> mResponse = new HashMap<String, Object>();
        mResponse.put(responseRoot, mWrapper);
        try {
            res = mapper.writeValueAsString(mResponse);
            bRes = CharsetConvertUtil.convert(res.getBytes(), Charset.defaultCharset(), Charset.forName(extChannel.getCharsetEnum().toString()));
        }
        catch (JsonGenerationException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (JsonMappingException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, e);
        }
        
        return bRes;
    }
}
