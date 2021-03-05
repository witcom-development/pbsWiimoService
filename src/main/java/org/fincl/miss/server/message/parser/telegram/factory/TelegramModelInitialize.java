package org.fincl.miss.server.message.parser.telegram.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.exeption.MessageParserException;
import org.fincl.miss.server.message.parser.telegram.TelegramConstant;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_HEADERVo;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_HEADER_RELVo;
import org.fincl.miss.server.message.parser.telegram.factory.db.TelegramQueryInfo;
import org.fincl.miss.server.message.parser.telegram.model.DynamicHeaderModel;
import org.fincl.miss.server.message.parser.telegram.model.HeaderModel;
import org.fincl.miss.server.message.parser.telegram.model.MessageModel;
import org.springframework.stereotype.Component;

/**
 * - 전문 Factory 초기화
 */

@Component
public class TelegramModelInitialize {
    private static final transient Log LOG = LogFactory.getLog(TelegramModelInitialize.class);
    
    private static Map<String, MessageModel> MessageModelList = new HashMap<String, MessageModel>();
    
    private TelegramModelInitialize() {
        
    }
    
    @PostConstruct
    public void initialize() {
        makeMessageModel();
        
    }
    
    public static MessageModel getMessageModel(String id) {
        return MessageModelList.get(id);
    }
    
    public static Map<String, MessageModel> getMessageModels() {
        return MessageModelList;
    }
    
    // private static Object lock = new Object();
    
    public void makeMessageModel() {
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        List<TB_IFS_TLGM_HEADER_RELVo> headerRelList = ti.getHeaderRelList();
        
        // System.out.println(">>>>>>>>>>>>>" + headerRelList.size());
        
        for (int i = 0; i < headerRelList.size(); i++) {
            TB_IFS_TLGM_HEADER_RELVo vo = headerRelList.get(i);
            makeMessageModel(vo.getRelationId());
        }
    }
    
    public void makeMessageModel(String relationId) throws MessageParserException {
        MessageModel messageModel = new MessageModel();
        
        messageModel.setId(relationId);
        messageModel.setMessageIDClass(TelegramConstant.TELE_UTIL_PAKAGE + relationId + TelegramConstant.TELE_UTIL_PAKAGE_TAIL);
        TB_IFS_TLGM_HEADERVo paramVo = new TB_IFS_TLGM_HEADERVo();
        paramVo.setRelationId(relationId);
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        List<TB_IFS_TLGM_HEADERVo> headerRelList = ti.getHeaderRelationeList(paramVo);
        
        for (int j = 0; j < headerRelList.size(); j++) {
            HeaderModel header = new HeaderModel();
            TB_IFS_TLGM_HEADERVo rvo = headerRelList.get(j);
            header.setId(rvo.getHeaderId());
            
            LOG.info("[" + messageModel.getId() + "]Header Loading... ID:" + header.getId());
            header.makeFieldModel();
            
            messageModel.addHeader(header);
        }
        
        List<TB_IFS_TLGM_HEADERVo> dynamicHeaderRelList = ti.getDynamicHeaderList(paramVo);
        for (int j = 0; j < dynamicHeaderRelList.size(); j++) {
            DynamicHeaderModel header = new DynamicHeaderModel();
            TB_IFS_TLGM_HEADERVo rvo = dynamicHeaderRelList.get(j);
            header.setId(rvo.getHeaderId());
            header.setCons_cd(rvo.getDmcHeaderId());
            LOG.info("[" + messageModel.getId() + "]Dynamic Header Loading... ID:" + header.getId());
            header.makeFieldModel();
            
            messageModel.addDynamicHeader(header);
        }
        // 다이나믹 헤더
        
        MessageModelList.put(messageModel.getId(), messageModel);
        
    }
    
    public static void reloadMessageModel(String headerId) {
        MessageModel messageModel = MessageModelList.get(headerId);
        if (messageModel != null) {
            MessageModelList.remove(headerId);
        }
        TelegramModelInitialize telegramInitialize = ApplicationContextSupport.getBean(TelegramModelInitialize.class);
        telegramInitialize.makeMessageModel(headerId);
    }
    
}
