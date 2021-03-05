package org.fincl.miss.server.remote;

import org.fincl.miss.core.remote.server.RemoteSendMessageService;
import org.fincl.miss.server.message.parser.telegram.util.TelegramRemoteUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoteSendMessageServiceHandler implements RemoteSendMessageService {
    
    @Autowired
    private TelegramRemoteUtil telegramRemoteUtil;
    
    @Override
    public boolean sendUserTelegramMessage(String ifId, String sMessage, String messageTypeString, String ip, int port) {
        
        telegramRemoteUtil.sendUserTelegramMessage(ifId, sMessage, messageTypeString, ip, port);
        
        return true;
    }
    
}
