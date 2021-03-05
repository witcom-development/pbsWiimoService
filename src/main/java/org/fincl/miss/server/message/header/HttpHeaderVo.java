package org.fincl.miss.server.message.header;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class HttpHeaderVo {
    
    private String contentType;
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
