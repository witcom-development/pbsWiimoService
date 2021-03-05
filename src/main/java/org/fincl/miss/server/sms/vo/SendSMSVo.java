package org.fincl.miss.server.sms.vo;

import java.util.LinkedHashMap;

import javax.validation.GroupSequence;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.ibatis.type.Alias;

@Alias("SendSMSVo")
public class SendSMSVo implements java.io.Serializable {
    
	public final Map<String, Integer> responseFields = new LinkedHashMap<String, Integer>();
    {
        responseFields.put("cmd_id", 1);
        responseFields.put("state", 1);
        responseFields.put("dev_state", 1);
        responseFields.put("dev_id", 7);
        responseFields.put("dev_type", 1);
        responseFields.put("user_type", 1);
        responseFields.put("user_seq", 5);
        
    }
    
    private String cmd_id;
    private String state;
    private String dev_state;
    private String dev_id;
    private String dev_type;
    private String user_type;
    private String user_seq;
    
	public String getCmd_id() {
		return cmd_id;
	}
	public void setCmd_id(String cmd_id) {
		this.cmd_id = cmd_id;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDev_state() {
		return dev_state;
	}
	public void setDev_state(String dev_state) {
		this.dev_state = dev_state;
	}
	public String getDev_id() {
		return dev_id;
	}
	public void setDev_id(String dev_id) {
		this.dev_id = dev_id;
	}
	public String getDev_type() {
		return dev_type;
	}
	public void setDev_type(String dev_type) {
		this.dev_type = dev_type;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}
	public String getUser_seq() {
		return user_seq;
	}
	public void setUser_seq(String user_seq) {
		this.user_seq = user_seq;
	}
    
    
    

}
