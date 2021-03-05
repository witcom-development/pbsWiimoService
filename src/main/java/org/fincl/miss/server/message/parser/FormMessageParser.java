package org.fincl.miss.server.message.parser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.MessageVO;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.service.metadata.NotRegisteredServiceException;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dkitec.cfood.core.CfoodException;

@Component
public class FormMessageParser extends FixedWrapMessageParser {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public FormMessageParser() {
    }
    
    @Override
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] bMessage) throws MessageParserException {
        logger.debug("parser {}", this);
        String message = new String(bMessage);
        Map<String, String[]> mParam = new HashMap<String, String[]>();
        String[] values = null;
        String[] params = StringUtils.split(message, "&");
        for (String param : params) {
            String[] arrParam = StringUtils.split(param, "=");
            String name = "";
            String value = "";
            if (arrParam != null && arrParam.length >= 1) {
                name = arrParam[0];
                value = arrParam.length == 1 ? "" : arrParam[1];
            }
            if (mParam.get(name) == null) {
                values = new String[] { value };
            }
            else {
                values = mParam.get(name);
                List<String> list = new ArrayList<String>(Arrays.asList(values));
                list.add(value);
                values = list.toArray(new String[list.size()]);
            }
            mParam.put(name, values);
        }
        
        Map<String, Object> fParam = new HashMap<String, Object>();
        Iterator<String> it = mParam.keySet().iterator();
        while (it.hasNext()) {
            String sKey = it.next();
            String[] aVal = mParam.get(sKey);
            fParam.put(sKey, aVal != null && aVal.length == 1 ? aVal[0] : aVal);
        }
        
        if (mParam.get(RequestFixedField.SERVICE_ID.getValue()) == null) {
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_NOT_FOUND_SERVICE_ID, "serviceId is null.");
        }
        
        String serviceId = (String) fParam.get(RequestFixedField.SERVICE_ID.getValue());
        
        if (serviceId == null) {
            throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_NOT_FOUND_SERVICE_ID, "serviceId is null.");
        }
        else {
            ServiceMessageHeaderContext.getMessageHeader().put(MessageHeader.SERVICE_ID, serviceId);
        }
        
        MessageVO interfaceVO;
        
        try {
            
            ServiceMetadata serviceMetadata = serviceRegister.getServiceMetadata(serviceId);
            Class<?> interfaceVOClazz = serviceMetadata.getParamClass();
            
            interfaceVO = (MessageVO) interfaceVOClazz.newInstance();
            
            String id = null;
            if (fParam.get(RequestFixedField.ID.getValue()) != null) {
                id = (String) fParam.get(RequestFixedField.ID.getValue());
            }
            
            System.out.println("gaaaaa [" + id + "]");
            if (StringUtils.isNotEmpty(id)) {
                interfaceVO.setId(id);
            }
            
            System.out.println("jj:" + fParam);
            
            BeanUtils.populate(interfaceVO, fParam);
            
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
        catch (InstantiationException e) {
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
        
        return interfaceVO;
        
    }
    
    @Override
    public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVo) throws MessageParserException {
        throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_NOT_SUPPORTED);
    }
    
    @Override
    public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex) {
        throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_NOT_SUPPORTED);
    }
    
}
