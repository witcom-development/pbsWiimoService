package org.fincl.miss.server.message.parser.telegram.util;

  
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.message.MessageInterfaceVO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class TelegramSendUtil   {
   
    public MessageInterfaceVO  SendMessage( MessageInterfaceVO messageInterfaceVO , Class<?> retClass) throws Exception{
        TelegramSysUtil tu = ApplicationContextSupport.getBean(TelegramSysUtil.class);
        return tu.SendMessage(messageInterfaceVO, retClass);
    }
}
