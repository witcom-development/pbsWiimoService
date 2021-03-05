package org.fincl.miss.server.message.header;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class TelegramHeaderVo {
    
    private String val1;
    
    public String getVal1() {
        return val1;
    }
    
    public void setVal1(String val1) {
        this.val1 = val1;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
