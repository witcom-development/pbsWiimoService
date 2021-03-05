package org.fincl.miss.server.message.parser.telegram;

import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramInterfaceFactory;
import org.fincl.miss.server.message.parser.telegram.factory.TelegramModelInitialize;
import org.fincl.miss.server.message.parser.telegram.model.MessageModel;
import org.fincl.miss.server.message.parser.telegram.valueobjects.InterfaceIdVo;
import org.fincl.miss.server.message.parser.telegram.valueobjects.MessageVO;
import org.fincl.miss.server.util.EnumCode.Charset;
  

/** 
 * - 전문을 생성,수정 관리하는 클래스 -
 * 
 */

public class MessageManager {

    protected String Message_ID = null;
    
    public MessageManager () {
        
    }
    
    public String getMessage_id() {
        return Message_ID;
    }
    
    public MessageManager(String Message_ID) {
        this.Message_ID = Message_ID;
    }
    
//    public Message getMessage(String id) throws MessageParserException {
//        // TODO Auto-generated method stub
//        MessageModel model = TelegramInitialize.getMessageModel(Message_ID);
//        MessageVO msg = new MessageVO(id, model, Message.IO_OUTBOUND);
//        msg.defaultSetting();
//        InterfaceIdVo infid = TelegramInterfaceFactory.getMessageID(Message_ID);
//        infid.setId(id, msg);
//   
//        msg.setMessageManagerId(Message_ID);
//        return msg;
//    }
    
//    public Message getMessage(byte[] messageBuf) throws MessageParserException {
//        // TODO Auto-generated method stub
//        MessageModel model = TelegramInitialize.getMessageModel(Message_ID);
//        MessageVO msg = new MessageVO(model, Message.IO_INBOUND);
//        msg.parsingMessageBuf(messageBuf);
// 
//        msg.setMessageManagerId(Message_ID);
//        return msg;
//    }
    
    public Message getHeaderMessage(byte[] messageBuf,Charset charset) throws MessageParserException {
        // TODO Auto-generated method stub
        MessageModel model = TelegramModelInitialize.getMessageModel(Message_ID);
        MessageVO msg = new MessageVO(model, Message.SOURCE_INBOUND);//헤더만 파싱 하기 때문에 messagetype는 의미가 없다.
        msg.setCharset(charset);
        msg.parsingHeaderMessageBuf(messageBuf);
 
        msg.setMessageManagerId(Message_ID);
        return msg;
    }
    
    public Message getMessage(String id, int messageType,Charset charset) throws MessageParserException {
        // TODO Auto-generated method stub
        MessageModel model = TelegramModelInitialize.getMessageModel(Message_ID);
  
        MessageVO msg = new MessageVO(id, model, messageType);
        msg.setCharset(charset);
        msg.defaultSetting();
        InterfaceIdVo infid = TelegramInterfaceFactory.getMessageID(Message_ID);
        infid.setId(id, msg); 
        msg.setMessageManagerId(Message_ID);
        return msg;
    }
    
 
    
    public Message getMessage(byte[] messageBuf, int messageType, Charset charset) throws MessageParserException {
        // TODO Auto-generated method stub 
        MessageModel model = TelegramModelInitialize.getMessageModel(Message_ID); 
        MessageVO msg = new MessageVO(model, messageType); 
        msg.setCharset(charset);
        msg.parsingMessageBuf(messageBuf); 
        
        msg.setMessageManagerId(Message_ID);
        return msg;
    }   
    
    public Message getMessage(String messageId, byte[] messageBuf, int messageType) throws MessageParserException {
        // TODO Auto-generated method stub      
        MessageModel model = TelegramModelInitialize.getMessageModel(messageId);
        MessageVO msg = new MessageVO(model, messageType);
        msg.parsingMessageBuf(messageBuf);
 
        msg.setMessageManagerId(Message_ID);
        return msg;
    }
    
    public Message getMessage(String messageId, byte[] messageBuf, String id, int messageType) throws MessageParserException {
        // TODO Auto-generated method stub      
        MessageModel model = TelegramModelInitialize.getMessageModel(messageId);
        MessageVO msg = new MessageVO(model, messageType);
        msg.parsingMessageBuf(messageBuf);
 
        msg.setMessageManagerId(Message_ID);
        return msg;
    }
    
    public Message getMessage(String messageId, String id, int messageType) throws MessageParserException {
        // TODO Auto-generated method stub
        MessageModel model = TelegramModelInitialize.getMessageModel(messageId);      
        MessageVO msg = new MessageVO(id, model, messageType);
        msg.defaultSetting();
 
        msg.setMessageManagerId(Message_ID);
        return msg;
    }
    
    public Message getMessage(byte[] messageBuf, String id, int messageType) throws MessageParserException {
        // TODO Auto-generated method stub  
        MessageModel model = TelegramModelInitialize.getMessageModel(Message_ID);
        MessageVO msg = new MessageVO(model, messageType);
 
        msg.setMessageManagerId(Message_ID);
        return msg;
    }

    
    public Message getErrMessage(Charset charset) throws MessageParserException {
        // TODO Auto-generated method stub
        MessageModel model = TelegramModelInitialize.getMessageModel(Message_ID);
        MessageVO msg = new MessageVO( model, 0);
        msg.setCharset(charset);
        msg.defaultHedaderetting(); 
        msg.setMessageManagerId(Message_ID);
        return msg;
    }
    
}
