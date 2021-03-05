package org.fincl.miss.server.message.parser.telegram.factory;
 
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.model.MessageModel;
import org.fincl.miss.server.message.parser.telegram.util.TelegramSysUtil;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceIdVo;

public  class TelegramInterfaceFactory {
     
     
    public static InterfaceIdVo getMessageID(String id) {
        InterfaceIdVo idclass = TelegramHazelInstance.getTelegramInterfaceMap().get(id);
 
        if(idclass == null)
            idclass = makeMessageID(id);
        return idclass;
    }
     
    private synchronized static InterfaceIdVo makeMessageID(String id) throws MessageParserException {
        InterfaceIdVo messageidClass = null;
        MessageModel model = TelegramModelInitialize.getMessageModel(id);// REL0000001
        messageidClass = (InterfaceIdVo)TelegramSysUtil.getClassInstance(model.getMessageIDClass());
        if(messageidClass != null)
            TelegramHazelInstance.getTelegramInterfaceMap().put(id, messageidClass);
             
 
        return messageidClass;
    }
   
    public synchronized static void reLoadMessageID(String id) throws MessageParserException {
        InterfaceIdVo messageidClass = null; 
 
        messageidClass = TelegramHazelInstance.getTelegramInterfaceMap().get(id);
        if(messageidClass!=null)
            TelegramHazelInstance.getTelegramInterfaceMap().remove(id);
        
        TelegramModelInitialize.reloadMessageModel(id);
        MessageModel model = TelegramModelInitialize.getMessageModel(id); 
        messageidClass = (InterfaceIdVo)TelegramSysUtil.getClassInstance(model.getMessageIDClass());
         
        if(messageidClass != null)
            TelegramHazelInstance.getTelegramInterfaceMap().put(id, messageidClass);
        
    }
  
}
