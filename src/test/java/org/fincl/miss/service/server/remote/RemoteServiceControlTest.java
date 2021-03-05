package org.fincl.miss.service.server.remote;

import org.fincl.miss.core.remote.server.RemoteServiceControlService;
import org.junit.Test;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

public class RemoteServiceControlTest {
    
    @Test
    public void testReloadParserMetadata() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteServiceControlService");
        httpInvokerProxy.setServiceInterface(RemoteServiceControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteServiceControlService serviceInterface = (RemoteServiceControlService) httpInvokerProxy.getObject();
        boolean result = serviceInterface.reloadParserMetadata();
        System.out.println(">>" + result);
        
    }
    
    @Test
    public void testReloadServiceContext() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteServiceControlService");
        httpInvokerProxy.setServiceInterface(RemoteServiceControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteServiceControlService serviceInterface = (RemoteServiceControlService) httpInvokerProxy.getObject();
        boolean result = serviceInterface.reloadServiceContext();
        System.out.println(">>" + result);
        
    }
    
}
