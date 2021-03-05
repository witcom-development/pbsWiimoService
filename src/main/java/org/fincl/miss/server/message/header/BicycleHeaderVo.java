package org.fincl.miss.server.message.header;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BicycleHeaderVo {
    
    private String frameControl;
    private String seqNum;
    
    public String getFrameControl() {
        return frameControl;
    }
    
    public void setFrameControl(String frameControl) {
        this.frameControl = frameControl;
    }
    
    public String getSeqNum() {
        return seqNum;
    }
    
    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
