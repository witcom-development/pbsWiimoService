package org.fincl.miss.server.logging.profile;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TraceLog {
    
    private boolean startedLogging;
    
    private String id;
    
    private long startTime;
    private long endTime;
    private long elapsedTime;
    
    private Object objParam;
    private Object objReturn;
    
    private boolean isEnd;
    
    public boolean isEnd() {
        return isEnd;
    }
    
    public void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }
    
    public boolean isStartedLogging() {
        return startedLogging;
    }
    
    public void setStartedLogging(boolean startedLogging) {
        this.startedLogging = startedLogging;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public void setEndTime(long endTime) {
        setElapsedTime(endTime - startTime);
        this.endTime = endTime;
    }
    
    public long getElapsedTime() {
        return elapsedTime;
    }
    
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    
    public Object getObjParam() {
        return objParam;
    }
    
    public void setObjParam(Object objParam) {
        this.objParam = objParam;
    }
    
    public Object getObjReturn() {
        return objReturn;
    }
    
    public void setObjReturn(Object objReturn) {
        this.objReturn = objReturn;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
