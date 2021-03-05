package org.fincl.miss.server.service.metadata.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.core.service.Service;
import org.fincl.miss.core.service.ServiceGroup;
import org.fincl.miss.core.service.impl.TransientServiceRepositoryServiceImpl;
import org.fincl.miss.core.service.impl.WritableServiceGroupImpl;
import org.fincl.miss.core.service.impl.WritableServiceImpl;
import org.fincl.miss.server.annotation.RPCService;
import org.fincl.miss.server.annotation.RPCServiceGroup;
import org.fincl.miss.server.message.MessageVO;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * @author DKI
 * @since 1.0
 */
public class ConfiguredServiceRepositoryServiceImpl extends TransientServiceRepositoryServiceImpl implements ApplicationContextAware {
    
    /**
	 * 
	 */
    private transient ApplicationContext context;
    
    /**
	 * 
	 */
    private final transient Map<String, Method> handlerMethods;
    
    /***
	 * 
	 */
    public ConfiguredServiceRepositoryServiceImpl() {
        
        super();
        super.setReadOnly(true);
        
        handlerMethods = new ConcurrentHashMap<String, Method>();
    }
    
    /**
	 * 
	 */
    @Override
    protected void initialize(final Collection<ServiceGroup> serviceGroups, final Collection<Service> services) {
        
        // ServiceEntryListener에서 ApplicationContextSuppport를 사용하기 위해 빈 객체를 사전에 로드한다.
        context.getBean(ApplicationContextSupport.class);
        
        super.initialize(serviceGroups, services);
        
        handlerMethods.clear();
        for (final Service service : services) {
            handlerMethods.put(service.getServiceId(), ((ServiceMetadata) service).getHandlerMethod());
        }
    }
    
    /**
	 * 
	 */
    @Override
    public void initialize() {
        
        if (context == null) {
            throw new IllegalStateException("ApplicationContext null");
        }
        
        final Map<String, Service> services = new HashMap<String, Service>();
        final Map<String, ServiceGroup> serviceGroups = new HashMap<String, ServiceGroup>();
        
        final ClassLoader contextClassLoader = context.getClassLoader();
        for (final String beanName : context.getBeanDefinitionNames()) {
            
            Class<?> clazz = unwrapProxy(context.getBean(beanName)).getClass();
            final ClassLoader clazzClassLoader = clazz.getClassLoader();
            if (clazzClassLoader == null || !clazzClassLoader.equals(contextClassLoader)) {
                continue;
            }
            
            // // 위빙으로 인해 변경된 클래스에는 원 클래스에서 지정한 어노테이션이 없으므로
            // // 어노테이션을 확인하기 위해 원 클래스를 획득한다.
            // String className = clazz.getName();
            // final int doubleDollarPos = className.indexOf("$$");
            // if (doubleDollarPos >= 0) {
            // className = className.substring(0, doubleDollarPos);
            // try {
            // clazz = contextClassLoader.loadClass(className);
            // }
            // catch (ClassNotFoundException e) {
            // throw new IllegalStateException("Cannot load handler class: " + className, e);
            // }
            // }
            
            WritableServiceGroupImpl serviceGroup = null;
            String serviceGroupName = null;
            
            final RPCServiceGroup rpcServiceGroup = clazz.getAnnotation(RPCServiceGroup.class);
            if (rpcServiceGroup != null) {
                serviceGroupName = rpcServiceGroup.serviceGroupName();
                if (serviceGroups.containsKey(serviceGroupName)) {
                    throw new IllegalStateException("Duplicated serviceGroup: " + serviceGroupName);
                }
                
                serviceGroup = new WritableServiceGroupImpl();
                serviceGroup.setServiceGroupName(serviceGroupName);
                serviceGroup.setDescription(rpcServiceGroup.description());
                serviceGroup.setHandlerClassName(clazz.getName());
                
                validate(serviceGroup);
                serviceGroups.put(serviceGroupName, serviceGroup);
            }
            
            for (final Method method : clazz.getDeclaredMethods()) {
                final RPCService rpcService = method.getAnnotation(RPCService.class);
                if (rpcService != null) {
                    if (serviceGroup == null) {
                        throw new IllegalStateException("Handler method should be defined at handler class annotated by " + RPCServiceGroup.class + ": " + method.getName());
                    }
                    final String serviceId = rpcService.serviceId();
                    if (services.containsKey(serviceId)) {
                        throw new IllegalStateException("Duplicated serviceId: " + serviceId);
                    }
                    
                    final String methodName = method.getName();
                    final Class<?>[] paramClasses = method.getParameterTypes();
                    if (paramClasses.length != 1) {
                        throw new IllegalStateException("Handler method should have one parameter: " + methodName);
                    }
                    if (!MessageVO.class.isAssignableFrom(paramClasses[0])) {
                        throw new IllegalStateException("Parameter of handler method should be instance of " + MessageVO.class);
                    }
                    if (!MessageVO.class.isAssignableFrom(method.getReturnType())) {
                        throw new IllegalStateException("Handler method should return instance of " + MessageVO.class);
                    }
                    
                    final WritableServiceImpl service = new WritableServiceImpl();
                    service.setServiceId(serviceId);
                    service.setServiceName(rpcService.serviceName());
                    service.setServiceGroupName(serviceGroupName);
                    service.setDescription(rpcService.description());
                    service.setParamDescription(rpcService.paramDecription());
                    service.setReturnDescription(rpcService.returnDescription());
                    service.setExample(rpcService.example());
                    service.setHandlerMethodName(methodName);
                    
                    final ServiceMetadata metadata = new ServiceMetadataImpl(service, method);
                    validate(metadata);
                    services.put(serviceId, metadata);
                }
            }
        }
        
        initialize(serviceGroups.values(), services.values());
    }
    
    /**
	 * 
	 */
    @Override
    public void setReadOnly(boolean readOnly) {
        
        if (!readOnly) {
            throw new UnsupportedOperationException("Cannot set to writable repository");
        }
    }
    
    /**
	 * 
	 */
    @Override
    public void setApplicationContext(final ApplicationContext context) {
        
        if (context == null) {
            throw new IllegalArgumentException("ApplicationContext null");
        }
        
        this.context = context;
    }
    
    /**
     * 
     * @param serviceId
     * @return
     */
    public Method getHandlerMethod(final String serviceId) {
        
        if (serviceId == null) {
            throw new IllegalArgumentException("ServiceId null");
        }
        
        return handlerMethods.get(serviceId);
    }
    
    private final Object unwrapProxy(Object bean) {
        
        /*
         * If the given object is a proxy, set the return value as the object
         * being proxied, otherwise return the given object.
         */
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            
            Advised advised = (Advised) bean;
            
            try {
                bean = advised.getTargetSource().getTarget();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Object can't be resolved");
            }
        }
        
        return bean;
    }
}
