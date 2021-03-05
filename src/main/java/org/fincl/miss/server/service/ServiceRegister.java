package org.fincl.miss.server.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.fincl.miss.core.mybatis.RefreshableSqlSessionFactoryBean;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServiceRegisterException;
import org.fincl.miss.server.logging.profile.TraceLogManager;
import org.fincl.miss.server.service.metadata.ServiceMetadata;
import org.fincl.miss.server.service.metadata.ServiceMetadataResolver;
import org.fincl.miss.server.service.metadata.ServiceRequest;
import org.fincl.miss.server.service.metadata.impl.ConfiguredServiceRepositoryServiceImpl;
import org.fincl.miss.server.service.metadata.impl.ServiceMetadataResolverImpl;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ServiceRegister {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private ApplicationContext applicationContext; // 현재의 ApplicationContext
    
    private FileSystemXmlApplicationContext bizApplicationContext; // Biz ApplicationContext
    
    private ServiceClassLoader bizClassLoader;
    
    private String contextConfigurationClassPath;
    
    private String classesRootPath;
    
    private String basePackageName;
    
    private ServiceMetadataResolver metadataResolver;
    
    public ServiceRegister() {
        
    }
    
    public ServiceRegister(String contextConfigurationClassPath, String classesRootPath, String basePackageName) {
        this.contextConfigurationClassPath = contextConfigurationClassPath;
        this.classesRootPath = classesRootPath;
        this.basePackageName = basePackageName;
    }
    
    @SuppressWarnings("unused")
    @PostConstruct
    public void init() {
        
        long start = System.currentTimeMillis();
        
        TraceLogManager.applicationContext = applicationContext;
        
        try {
            
            if (bizClassLoader != null) {
               // bizClassLoader.close();
                bizClassLoader = null;
            }
            
            logger.debug("class loader start");
            bizClassLoader = new ServiceClassLoader(this.classesRootPath, this.basePackageName);
            logger.debug("class loader end");
            
            long step1 = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Biz Service ClassLoader init time [{} ms]", (step1 - start));
            }
            
            // 지정안해주면, paramterType이나 resultType에 지정하는 class를 못 찾음.
            org.apache.ibatis.io.Resources.setDefaultClassLoader(this.bizClassLoader);
            
            ClassPathResource cfgResource = new ClassPathResource(this.contextConfigurationClassPath);
            String[] arrCfg = null;
            if (cfgResource.getFile().getAbsolutePath().startsWith("/")) {
                arrCfg = new String[] { "file://" + cfgResource.getFile().getAbsolutePath() };
            }
            else {
                arrCfg = new String[] { cfgResource.getFile().getAbsolutePath() };
            }
            this.bizApplicationContext = new FileSystemXmlApplicationContext(arrCfg, false, this.applicationContext); // Root Context의 Child Context로 생성
            this.bizApplicationContext.setClassLoader(this.bizClassLoader);
            // this.bizApplicationContext.afterPropertiesSet();
            this.bizApplicationContext.refresh();
            
            // AutowireCapableBeanFactory factory = this.bizApplicationContext.getAutowireCapableBeanFactory();
            // BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
            
            // String[] gg = registry.getBeanDefinitionNames();
            // for (String gga : gg) {
            // System.out.println("gg [" + gga + "]");
            // }
            
            long step2 = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Biz Service Context init time [{} ms]", (step2 - step1));
            }
            
            long step3 = System.currentTimeMillis();
            if (logger.isDebugEnabled()) {
                logger.debug("Biz Service MBean Regist init time [{} ms]", (step3 - step2));
                logger.debug("Biz Service Total init time [{} ms]", (step3 - start));
            }
        }
        catch (IOException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceRegisterException(ErrorConstant.SERVICE_REGISTER_FILE_NOT_FOUND, e);
        }
        catch (ClassNotFoundException e) {
            if (logger.isDebugEnabled()) {
                e.printStackTrace();
            }
            throw new ServiceRegisterException(ErrorConstant.SERVICE_REGISTER_CLASS_NOT_FOUND, e);
        }
        
        //
        // GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        // beanDefinition.setBeanClass(ServiceContoller.class);
        // beanDefinition.setAutowireCandidate(true);
        // registry.registerBeanDefinition("xcvg", beanDefinition);
        // factory.autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        
        // ConfigurableApplicationContext configContext = (ConfigurableApplicationContext) this.bizApplicationContext;
        // SingletonBeanRegistry beanRegistry = configContext.getBeanFactory();
        // this.bizClassLoader.loadClass("org.fincl.miss.server.service.ServiceController");
        // beanRegistry.registerSingleton("cccmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmc", ServiceContoller.class);
        
        // this.bizApplicationContext.afterPropertiesSet();
        // this.bizApplicationContext.refresh();
        // ApplicationContext ctxRoot = applicationContext;
        // System.out.println("root::" + ctxRoot.getBeanDefinitionCount());
        // String[] agg = ctxRoot.getBeanDefinitionNames();
        // for (String xx : agg) {
        // System.out.println("root bean ==>" + xx);
        // }
        
        // ctx.getBeanDefinitionCount()
        // System.out.println("bean start-----------");
        // String[] ag = this.bizApplicationContext.getBeanDefinitionNames();
        // for (String xx : ag) {
        // System.out.println("bean ==>" + xx + "[" + this.bizApplicationContext.getBean(xx) + "]");
        //
        // }
        //
        // org.fincl.miss.server.service.ServiceContoller cc = this.bizApplicationContext.getBean(org.fincl.miss.server.service.ServiceContoller.class);
        // cc.test();
        // System.out.println("bean end-----------");
        
        // Initialize ServiceMetadataResolver
        final ConfiguredServiceRepositoryServiceImpl candidateRepositoryService = applicationContext.getBean(ConfiguredServiceRepositoryServiceImpl.class);
        candidateRepositoryService.setApplicationContext(bizApplicationContext);
        candidateRepositoryService.initialize();
        
        final ServiceMetadataResolverImpl metadataResolver = applicationContext.getBean(ServiceMetadataResolverImpl.class);
        metadataResolver.initialize();
        
        this.metadataResolver = metadataResolver;
    }
    
    public FileSystemXmlApplicationContext getBizApplicationContext() {
        return bizApplicationContext;
    }
    
    public void setBizApplicationContext(FileSystemXmlApplicationContext bizApplicationContext) {
        this.bizApplicationContext = bizApplicationContext;
    }
    
    public ServiceClassLoader getBizClassLoader() {
        return bizClassLoader;
    }
    
    public void setBizClassLoader(ServiceClassLoader bizClassLoader) {
        this.bizClassLoader = bizClassLoader;
    }
    
    /**
     * 
     * @param serviceId
     * @return
     */
    public ServiceMetadata getServiceMetadata(final String serviceId) {
        return metadataResolver.resolve(serviceId);
    }
    
    /**
     * 
     * @param serviceId
     * @param serviceRequest
     * @return
     */
    public ServiceMetadata getServiceMetadata(final String serviceId, final ServiceRequest serviceRequest) {
        return metadataResolver.resolve(serviceId, serviceRequest);
    }
    
    @PreDestroy
    public void destroy() {
        String[] sqlSessionBeanNames = bizApplicationContext.getBeanNamesForType(SqlSessionFactoryBean.class);
        for (String sqlSessionBeanName : sqlSessionBeanNames) {
            Object bean = bizApplicationContext.getBean(sqlSessionBeanName);
            if (bean instanceof RefreshableSqlSessionFactoryBean) {
                RefreshableSqlSessionFactoryBean refreshableSqlSessionFactoryBean = (RefreshableSqlSessionFactoryBean) bean;
                refreshableSqlSessionFactoryBean.destroy();
            }
        }
    }
}
