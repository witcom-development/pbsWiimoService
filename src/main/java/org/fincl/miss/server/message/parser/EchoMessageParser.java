package org.fincl.miss.server.message.parser;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
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
public class EchoMessageParser extends MessageParser {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private String preDefinedServiceId = "echoService";
    
    @EnableTraceLogging
    @Override
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] bMessage) throws MessageParserException {
        
        ServiceMetadata serviceMetadata = serviceRegister.getServiceMetadata(preDefinedServiceId);
        Class<?> clazz = serviceMetadata.getParamClass();
        MessageVO vo = null;
        try {
            byte[] nMessage = new byte[bMessage.length - 6];
            System.arraycopy(bMessage, 6, nMessage, 0, bMessage.length - 6);
            vo = (MessageVO) clazz.newInstance();
            BeanUtils.setProperty(vo, "payload", nMessage);
        }
        catch (InstantiationException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.ECHO_MESSAGE_PARSE_ERROR, e);
        }
        catch (IllegalAccessException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.ECHO_MESSAGE_PARSE_ERROR, e);
        }
        catch (InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.ECHO_MESSAGE_PARSE_ERROR, e);
        }
        
        vo.setServiceId(preDefinedServiceId);
        
        ServiceMessageHeaderContext.getMessageHeader().put(MessageHeader.SERVICE_ID, preDefinedServiceId);
        
        return vo;
    }
    
    @EnableTraceLogging
    @Override
    public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVo) throws MessageParserException {
        System.out.println("gggggggggggggggggg---------------------------------");
        byte[] bReturn = null;
        try {
            Object obj = PropertyUtils.getProperty(messageInterfaceVo, "payload");
            bReturn = (byte[]) obj;
        }
        catch (IllegalAccessException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.ECHO_MESSAGE_BUILD_ERROR, e);
        }
        catch (InvocationTargetException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.ECHO_MESSAGE_BUILD_ERROR, e);
        }
        catch (NoSuchMethodException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.ECHO_MESSAGE_BUILD_ERROR, e);
        }
        // System.out.println("nn :" + new String(bb));
        return bReturn;
    }
    
    @Override
    public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex) {
        // TODO Auto-generated method stub
        return null;
    }
}
