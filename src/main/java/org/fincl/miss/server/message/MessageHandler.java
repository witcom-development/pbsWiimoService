package org.fincl.miss.server.message;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.BasicTelegramMessageParser;
import org.fincl.miss.server.message.parser.BicycleTelegramMessageParser;
import org.fincl.miss.server.message.parser.EchoMessageParser;
import org.fincl.miss.server.message.parser.FormMessageParser;
import org.fincl.miss.server.message.parser.JsonMessageParser;
import org.fincl.miss.server.message.parser.MessageParser;
import org.fincl.miss.server.message.parser.SmartTelegramMessageParser;
import org.fincl.miss.server.message.parser.StressMessageParser;
import org.fincl.miss.server.message.parser.XmlMessageParser;
import org.fincl.miss.server.service.ServiceRegister;
import org.fincl.miss.server.util.EnumCode.RequestDataType;
import org.fincl.miss.server.util.EnumCode.ResponseDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.dkitec.cfood.core.CfoodException;
import com.google.common.collect.Maps;

@Component("messageHandler")
public class MessageHandler {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ServiceRegister serviceRegister;
    
    private Map<RequestDataType, Class<? extends MessageParser>> requestParser = null;
    
    private Map<ResponseDataType, Class<? extends MessageParser>> responseParser = null;
    
    @PostConstruct
    public void init() {
        requestParser = Maps.newHashMap();
        
        requestParser.put(RequestDataType.JSON, JsonMessageParser.class);
        requestParser.put(RequestDataType.XML, XmlMessageParser.class);
        requestParser.put(RequestDataType.FORM, FormMessageParser.class);
        requestParser.put(RequestDataType.LENGTH_HEADER, BasicTelegramMessageParser.class);
        requestParser.put(RequestDataType.DELIMETER, BasicTelegramMessageParser.class);
        requestParser.put(RequestDataType.BICYCLE, BicycleTelegramMessageParser.class);
        requestParser.put(RequestDataType.STRESS, StressMessageParser.class);
        requestParser.put(RequestDataType.ECHO, EchoMessageParser.class);
        requestParser.put(RequestDataType.SMART, SmartTelegramMessageParser.class);
        
        responseParser = Maps.newHashMap();
        responseParser.put(ResponseDataType.JSON, JsonMessageParser.class);
        responseParser.put(ResponseDataType.XML, XmlMessageParser.class);
        responseParser.put(ResponseDataType.LENGTH_HEADER, BasicTelegramMessageParser.class);
        responseParser.put(ResponseDataType.DELIMETER, BasicTelegramMessageParser.class);
        responseParser.put(ResponseDataType.BICYCLE, BicycleTelegramMessageParser.class);
        responseParser.put(ResponseDataType.STRESS, StressMessageParser.class);
        responseParser.put(ResponseDataType.ECHO, EchoMessageParser.class);
        responseParser.put(ResponseDataType.SMART, SmartTelegramMessageParser.class);
    }
    
    @EnableTraceLogging
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] message) throws MessageParserException {
        MessageInterfaceVO in = null;
        try {
            MessageParser messageParser = applicationContext.getBean(requestParser.get(extChannel.getRequestDataTypeEnum()));
            logger.debug("MessageHandler parse {}", messageParser);
            in = messageParser.parse(extChannel, message);
        }
        catch (Exception ex) {
            System.out.println("aaaa::" + (ex instanceof CfoodException));
            System.out.println("mkkang!!!!!!!!!!!AAAAAAAAA" + ex);
            System.out.println("mkkang!!!!!!!!!!!AAAAAAAAA" + ex.getCause());
            if (ex instanceof MessageParserException) {
                throw (MessageParserException) ex;
            }
            else if (ex instanceof CfoodException) {
                throw new MessageParserException(((CfoodException) ex).getCode(), ex.getCause());
            }
            else {
                throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, ex.getCause());
            }
        }
        return in;
    }
    
    @EnableTraceLogging
    public byte[] build(BoundChannel extChannel, MessageInterfaceVO interfaceIdVo) throws MessageParserException {
        byte[] rMessage = null;
        try {
            MessageParser messageParser = applicationContext.getBean(responseParser.get(extChannel.getResponseDataTypeEnum()));
            logger.debug("MessageHandler build {}", messageParser);
            rMessage = messageParser.build(extChannel, interfaceIdVo);
        }
        catch (Exception ex) {
            System.out.println("aaaa::" + (ex instanceof CfoodException));
            if (ex instanceof MessageParserException) {
                throw (MessageParserException) ex;
            }
            else if (ex instanceof CfoodException) {
                throw new MessageParserException(((CfoodException) ex).getCode(), ex);
            }
            else {
                throw new MessageParserException(ErrorConstant.MESSAGE_PARSER_ERROR, ex);
            }
        }
        return rMessage;
    }
    
    @EnableTraceLogging
    public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex) {
        MessageParser messageParser = applicationContext.getBean(responseParser.get(extChannel.getResponseDataTypeEnum()));
        byte[] rMessage = messageParser.buildError(extChannel, messageHeader, ex);
        return rMessage;
    }
}
