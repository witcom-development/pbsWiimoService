package org.fincl.miss.server.logging.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.server.service.ServiceHandler;
import org.springframework.aop.target.PrototypeTargetSource;
import org.springframework.context.ApplicationContext;

public class TraceLogManager {
    
    public static ApplicationContext applicationContext; // ServiceRegister init에서 할당됨.
    
    public static long startTraceLog(String id) {
        return beginTraceLog(true, id, null);
    }
    
    public static void stopTraceLog(String id, long startTime) {
        endTraceLog(false, startTime, id, null);
        
        printLog();
        
        PrototypeTargetSource prototypeTargetSource = applicationContext.getBean("prototypeTargetSource", PrototypeTargetSource.class);
        List<TraceLog> ll = ServiceHandler.ContextTraceLog.get();
        for (TraceLog traceLog : ll) {
            try {
                prototypeTargetSource.releaseTarget(traceLog);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        ServiceHandler.ContextTraceLog.get().clear();
        ServiceHandler.ContextTraceLog.set(null);
        ServiceHandler.ContextTraceLog.remove();
    }
    
    public static long beginTraceLog(boolean startedLogging, String id, Object objParam) {
        long startTime = System.currentTimeMillis();
        if (TraceLogManager.isLogging(startedLogging)) {
            PrototypeTargetSource prototypeTargetSource = applicationContext.getBean("prototypeTargetSource", PrototypeTargetSource.class);
            TraceLog traceLog;
            
            try {
                traceLog = (TraceLog) prototypeTargetSource.getTarget();
                
                traceLog.setStartedLogging(startedLogging);
                traceLog.setId(id + " - start");
                traceLog.setStartTime(startTime);
                traceLog.setObjParam(objParam);
                traceLog.setEnd(false);
                
                //ServiceHandler.ContextTraceLog.get().add(traceLog);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return startTime;
    }
    
    public static void endTraceLog(boolean startedLogging, long startTime, String id, Object objReturn) {
        if (TraceLogManager.isLogging(startedLogging)) {
            PrototypeTargetSource prototypeTargetSource = applicationContext.getBean("prototypeTargetSource", PrototypeTargetSource.class);
            TraceLog traceLog;
            
            try {
                traceLog = (TraceLog) prototypeTargetSource.getTarget();
                
                traceLog.setStartedLogging(startedLogging);
                traceLog.setId(id + " - end");
                traceLog.setStartTime(startTime);
                traceLog.setEndTime(System.currentTimeMillis());
                traceLog.setObjReturn(objReturn);
                traceLog.setEnd(true);
                
                ServiceHandler.ContextTraceLog.get().add(traceLog);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    private static boolean isLogging(boolean isStart) {
        if (isStart == true || (ServiceHandler.ContextTraceLog != null && ServiceHandler.ContextTraceLog.get().size() > 0 && ServiceHandler.ContextTraceLog.get().get(0).isStartedLogging() == true)) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public static void printLog() {
        System.out.println(getTraceLogString());
    }
    
    public static String getTraceLogString() {
        
        List<TraceLog> traceLogList = ServiceHandler.ContextTraceLog.get();
        int printDepth = 0;
        List<Long> elapsedTimeList = new ArrayList<Long>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < traceLogList.size(); i++) {
            TraceLog traceLog = traceLogList.get(i);
            if (!traceLog.isEnd()) {
                sb.append(StringUtils.leftPad("", printDepth * 2, '-') + traceLog.getId()).append("\n");
                elapsedTimeList.add(traceLog.getStartTime());
                printDepth++;
            }
            else {
                --printDepth;
                sb.append(StringUtils.leftPad("", (printDepth * 2), '-') + traceLog.getId() + " => (elapsed time : " + (traceLog.getEndTime() - traceLog.getStartTime()) + " ms) ").append("\n");
                
            }
        }
        return sb.toString();
    }
    
    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        PrototypeTargetSource prototypeTargetSource = applicationContext.getBean("prototypeTargetSource", PrototypeTargetSource.class);
        // poolTraceLog.destroy();
        System.out.println("final!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        super.finalize();
    }
}
