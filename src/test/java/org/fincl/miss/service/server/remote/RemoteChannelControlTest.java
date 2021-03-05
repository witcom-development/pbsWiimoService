package org.fincl.miss.service.server.remote;

import java.util.Map;

import org.fincl.miss.core.remote.server.ChannelControl;
import org.fincl.miss.core.remote.server.RemoteChannelControlService;
import org.junit.Test;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

public class RemoteChannelControlTest {
    
    @Test
    public void testOutBoundChannelStatus() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.getOutBoundChannelStatus("CH9900");
        System.out.println(">>" + vo.getResult());
        
    }
    
    @Test
    public void testOutBoundMultiChannelStatus() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        Map<String, ChannelControl> m = serviceInterface.getOutBoundChannelStatus(new String[] { "CH9900", "CH0001" });
        System.out.println(">>" + m);
        
    }
    
    @Test
    public void testInBoundChannelStatus() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.getInBoundChannelStatus("CH00x1");
        System.out.println(">>" + vo);
        
    }
    
    @Test
    public void testInBoundMultiChannelStatus() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        Map<String, ChannelControl> m = serviceInterface.getInBoundChannelStatus(new String[] { "CH9900", "CH0001" });
        System.out.println(">>" + m);
        
    }
    
    @Test
    public void testStartOutBoundChannel() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.startOutBoundChannel("CH0015");
        System.out.println("Result>>" + vo.getResult());
        System.out.println("Message>>" + vo.getMessage());
    }
    
    @Test
    public void testStopOutBoundChannel() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.stopOutBoundChannel("CH0015");
        System.out.println("Result>>" + vo.getResult());
        System.out.println("Message>>" + vo.getMessage());
    }
    
    @Test
    public void testReloadOutBoundChannel() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.reloadOutBoundChannel("CH9900");
        System.out.println("Result>>" + vo.getResult());
        System.out.println("Message>>" + vo.getMessage());
    }
    
    @Test
    public void testStartInBoundChannel() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.startInBoundChannel("CH0019");
        System.out.println("Result>>" + vo.getResult());
        System.out.println("Message>>" + vo.getMessage());
    }
    
    @Test
    public void testStopInBoundChannel() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.stopInBoundChannel("CH0019");
        System.out.println("Result>>" + vo.getResult());
        System.out.println("Message>>" + vo.getMessage());
    }
    
    @Test
    public void testReloadInBoundChannel() {
        HttpInvokerProxyFactoryBean httpInvokerProxy = new HttpInvokerProxyFactoryBean();
        httpInvokerProxy.setServiceUrl("http://localhost:9080/miss-service/httpInvoker/remoteChannelControlService");
        httpInvokerProxy.setServiceInterface(RemoteChannelControlService.class);
        httpInvokerProxy.afterPropertiesSet();
        RemoteChannelControlService serviceInterface = (RemoteChannelControlService) httpInvokerProxy.getObject();
        ChannelControl vo = serviceInterface.reloadInBoundChannel("CH0001");
        System.out.println("Result>>" + vo.getResult());
        System.out.println("Message>>" + vo.getMessage());
    }
    
}
