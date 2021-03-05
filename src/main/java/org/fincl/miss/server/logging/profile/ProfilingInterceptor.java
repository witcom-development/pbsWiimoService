package org.fincl.miss.server.logging.profile;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.springframework.util.StopWatch;

public class ProfilingInterceptor implements MethodInterceptor {
    
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method m = invocation.getMethod();
        EnableTraceLogging annoTraceLogging = m.getAnnotation(EnableTraceLogging.class);
        
        boolean isStart = false;
        
        String id = "[" + invocation.getThis().getClass().getName() + "." + m.getName() + "]";
        
        long startTime = 0L;
        
        if (annoTraceLogging != null && annoTraceLogging.isStart()) {
            isStart = true;
            startTime = TraceLogManager.startTraceLog(id);
        }
        else {
            startTime = TraceLogManager.beginTraceLog(false, id, null);
        }
        
        Object target = invocation.getThis();
        // start the stop watch
        StopWatch sw = new StopWatch();
        sw.start(invocation.getMethod().getName());
        
        Object returnValue = invocation.proceed();
        
        if (isStart) {
            TraceLogManager.stopTraceLog(id, startTime);
        }
        else {
            TraceLogManager.endTraceLog(false, startTime, id, null);
        }
        sw.stop();
        
        return returnValue;
    }
    
}