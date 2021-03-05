package org.fincl.miss.server.scheduler.job.sms;

import java.io.Serializable;

import org.apache.ibatis.type.Alias;
import org.fincl.miss.server.sms.SendType;
import org.fincl.miss.server.util.StringUtil;


/**
 * 
 * @author civan
 *
 */
@Alias(value="partitionVO")
public class PartitionVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7182242541977683893L;


	public PartitionVO(){

	}
	
	private String tableName;
	private String partitionName;
	private String newPartitionName;
	private String partitionVal;


	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPartitionName() {
		return partitionName;
	}
	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
	}
	public String getNewPartitionName() {
		return newPartitionName;
	}
	public void setNewPartitionName(String newPartitionName) {
		this.newPartitionName = newPartitionName;
	}
	public String getPartitionVal() {
		return partitionVal;
	}
	public void setPartitionVal(String partitionVal) {
		this.partitionVal = partitionVal;
	}
	
	
	
	

}
