package org.fincl.miss.client.smart.vo;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class RequestFileVo extends SmartVo {
    
	
	public final Map<String, Integer> requestFields = new LinkedHashMap<String, Integer>();
    {
        requestFields.put("blockNo", 4);
        requestFields.put("seqNo", 3);
        requestFields.put("size", 4);
         
    }
    
    
    public final Map<String, String> repeatFields = new LinkedHashMap<String, String>();
    {
        repeatFields.put("data", "size"); // 반복될 필드, 반복수 참조 필드
    }
    
    public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
    	responseFields.put("blockNo", 4);
    	responseFields.put("seqNo", 3);
    	responseFields.put("size", 4);
         
    }
    
    private String blockNo;
    private String seqNo;
    private String size;
    private String[] data;
    
	public String getBlockNo() {
		return blockNo;
	}
	public void setBlockNo(String blockNo) {
		this.blockNo = blockNo;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String[] getData() {
		return data;
	}
	public void setData(String[] data) {
		this.data = data;
	}
    
    
}
