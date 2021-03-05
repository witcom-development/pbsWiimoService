package org.fincl.miss.server.message.parser.telegram.factory;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.message.parser.telegram.factory.db.TB_IFS_TLGM_IFVo;
import org.fincl.miss.server.message.parser.telegram.factory.db.TelegramQueryInfo;
import org.springframework.stereotype.Component;

    
@Component
public class TelegramInterfaceManager {
    public TelegramInterfaceManager() {
        
    }
     
    public TB_IFS_TLGM_IFVo getInterfaceInfo(String ifId) {
        if(TelegramHazelInstance.getTelegramInterfaceInfoMap().get(ifId)==null){
            loadInterfaceInfo(ifId);
        }
        return TelegramHazelInstance.getTelegramInterfaceInfoMap().get(ifId);
    }
     
    // private static Object lock = new Object();
    
    private void loadInterfaceInfo(String ifId) { 
        TB_IFS_TLGM_IFVo vo = new TB_IFS_TLGM_IFVo();
        vo.setIfId(ifId);
        TelegramQueryInfo ti = ApplicationContextSupport.getBean(TelegramQueryInfo.class);
        vo = ti.getInterfaceInfo(vo);
        if(vo!=null)
            TelegramHazelInstance.getTelegramInterfaceInfoMap().put(vo.getIfId(), vo);
    }
      
    public static void reLoadInterfaceInfo(String ifId) {
        TelegramHazelInstance.getTelegramInterfaceInfoMap().remove(ifId);
    }
     
}
