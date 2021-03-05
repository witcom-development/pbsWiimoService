package org.fincl.miss.server.message.parser;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.MessageHeader;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.fincl.miss.server.message.parser.telegram.Message;
import org.fincl.miss.server.message.parser.telegram.util.TelegramSysUtil;
import org.fincl.miss.server.service.ServiceMessageHeaderContext;
import org.fincl.miss.server.util.EnumCode.SyncType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dkitec.cfood.core.CfoodException;

@Component
public class BasicTelegramMessageParser extends MessageParser {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public BasicTelegramMessageParser() {
    }
    
    @EnableTraceLogging
    public MessageInterfaceVO parse(BoundChannel extChannel, byte[] bMessage) throws MessageParserException {
        // 당발, 타발 파싱 하여 VO로 리턴
        MessageInterfaceVO messageInterfaceVO = null;
        
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        
        if (extChannel.getSyncTypeEnum() == SyncType.PUBSUB) {
            messageInterfaceVO = (MessageInterfaceVO) tu.getMakeInterfaceIdVoFep(extChannel, bMessage);
            tu.getMessage().essentialValueHeaderCheck();
        }
        else {
            messageInterfaceVO = (MessageInterfaceVO) tu.getMakeInterfaceIdVo(extChannel, bMessage);
            tu.getMessage().essentialValueCheck(); 
        }
        
        if (messageInterfaceVO != null) {
            ServiceMessageHeaderContext.getMessageHeader().put(MessageHeader.SERVICE_ID, messageInterfaceVO.getServiceId());
        }
        
        return messageInterfaceVO;
    }
    
    @EnableTraceLogging
    @Override
    public byte[] build(BoundChannel extChannel, MessageInterfaceVO messageInterfaceVo) throws MessageParserException {
        // 타발 수신후 응답 메세지를 리턴한다. FEP는 사용하지 않는다.
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        byte[] retByte = null;
        try {
            
            retByte = tu.getMakeMessageOutBoundByte(extChannel, messageInterfaceVo, Message.TARGET_OUTBOUND);
            
            // message의 필수 값을 체크하자.
            tu.getMessage().essentialValueCheck(); 
            
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.TELEGRAM_MESSAGE_BUILD_ERROR, e);
        }
        
        return retByte;
    }
    
    @Override
    public byte[] buildError(BoundChannel extChannel, MessageHeader messageHeader, CfoodException ex) {
        // 당발에러 전송처리
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        byte[] retByte = null;
        try {
            retByte = tu.getMakeErrorMessageOutBoundByte(extChannel);
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new MessageParserException(ErrorConstant.TELEGRAM_MESSAGE_BUILD_ERROR, "only Header build", e);
        }
        return retByte;
    }
}
