package org.fincl.miss.client.smart.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ResponseCheckInfoVo extends SmartVo {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
    	requestFields.put("recentBlockNo", 4);
    	requestFields.put("recentSeqNo", 3);
    	requestFields.put("failCount", 3);
    	requestFields.put("failStatus", 100);
     }
    
    
    public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    
    public final Map<String, String> repeatFields = new LinkedHashMap<String, String>();
    
    private String recentBlockNo;
    private String recentSeqNo;
    private String failCount;
    private String failStatus;
    
	public String getRecentBlockNo() {
		return recentBlockNo;
	}
	public void setRecentBlockNo(String recentBlockNo) {
		this.recentBlockNo = recentBlockNo;
	}
	public String getRecentSeqNo() {
		return recentSeqNo;
	}
	public void setRecentSeqNo(String recentSeqNo) {
		this.recentSeqNo = recentSeqNo;
	}
	public String getFailCount() {
		return failCount;
	}
	public void setFailCount(String failCount) {
		this.failCount = failCount;
	}
	public String getFailStatus() {
		return failStatus;
	}
	public void setFailStatus(String failStatus) {
		this.failStatus = failStatus;
	}
    
 
}
