package org.fincl.miss.server.remote;

import org.fincl.miss.core.remote.server.RemoteMessageParserService;
import org.fincl.miss.server.message.parser.telegram.util.TelegramRemoteUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class RemoteMessageParserServiceHandler implements RemoteMessageParserService {
    
    @Autowired
    private TelegramRemoteUtil telegramRemoteUtil;
    
    @Override
    public String parseUserTelegramFieldToString(String ifId, String jsonMessage, String messageTypeString) {
        return telegramRemoteUtil.parseUserTelegramFieldToString(ifId, jsonMessage, messageTypeString);
    }
    
    @Override
    public String parseUserTelegramStringToField(String ifId, String sMessage, String MessageTypeString) {
        return telegramRemoteUtil.parseUserTelegramStringToField(ifId, sMessage, MessageTypeString);
    }
    
    @Override
    public String parseUserTelegramHeaderString(String ifId) {
        return telegramRemoteUtil.parseUserTelegramHeaderString(ifId);
    }
    
}
