package org.fincl.miss.server.logging.profile;

import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }), @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class }), @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class MybatisLogInterceptor implements Interceptor {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        
        // System.out.println("system::" + appContext);
        // System.out.println("system:::" + ApplicationContextSupport.getBean("poolTraceLog"));
        
        // CommonsPoolTargetSource poolTraceLog = (CommonsPoolTargetSource) SpringApplicationContext.getBean("poolTraceLog");
        // System.out.println("kkkk3333::" + poolTraceLog);
        
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object param = (Object) args[1];
        BoundSql boundSql = ms.getBoundSql(param);
        
        // System.out.println("====================================");
        // System.out.println(invocation.getMethod().getName());
        // System.out.println("====================================");
        // System.out.println(ms.getId());
        // System.out.println("====================================");
        // System.out.println(boundSql.getSql());
        // System.out.println("====================================");
        // System.out.println(param);
        // System.out.println("====================================");
        long startTime = System.currentTimeMillis();
        startTime = TraceLogManager.beginTraceLog(false, "[" + ms.getId() + "]", null);
        //
        Object obj = invocation.proceed();
        //
        TraceLogManager.endTraceLog(false, startTime, "[" + ms.getId() + "]", null);
        
        return obj;
    }
    
    @Override
    public Object plugin(Object target) {
        //
        
        return Plugin.wrap(target, this);
    }
    
    @Override
    public void setProperties(Properties properties) {
        //
        
    }
    
}