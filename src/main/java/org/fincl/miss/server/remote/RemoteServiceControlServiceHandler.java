package org.fincl.miss.server.remote;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.remote.server.RemoteServiceControlService;
import org.fincl.miss.server.channel.ChannelManagerImpl;
import org.fincl.miss.server.message.parser.telegram.util.TelegramRemoteUtil;
import org.fincl.miss.server.service.ServiceRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class RemoteServiceControlServiceHandler implements RemoteServiceControlService {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private ServiceRegister serviceRegister;
    
    private ChannelManagerImpl channelManager;
    
    private MessageSource errorSource;
    
    public RemoteServiceControlServiceHandler() {
        this.serviceRegister = ApplicationContextSupport.getBean(ServiceRegister.class);
        this.channelManager = ApplicationContextSupport.getBean(ChannelManagerImpl.class);
        this.errorSource = ApplicationContextSupport.getBean("errorSource", ReloadableResourceBundleMessageSource.class);
    }
    
    @Override
    public boolean reloadServiceContext() {
        boolean bResult = false;
        serviceRegister.init();
        bResult = true;
        return bResult;
    }
    
    @Override
    public boolean reloadParserMetadata() {
        // TODO Auto-generated method stub
        
        return false;
    }
    
    @Override
    public boolean reloadTelegramHeader(String headerId) {
        boolean bResult = false;
        try {
            TelegramRemoteUtil.reLoadTelegramHeader(headerId); // Header IF reload
            bResult = true;
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            bResult = false;
        }
        return bResult;
    }
    
    @Override
    public boolean reloadTelegramBody(String interfaceId) {
        boolean bResult = false;
        try {
            TelegramRemoteUtil.reLoadTelegramBody(interfaceId); // IF,IO Field reload
            bResult = true;
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            bResult = false;
        }
        return bResult;
    }
    
}
