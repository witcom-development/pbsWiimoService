package org.fincl.miss.server.message.parser.telegram.util;

 
import java.net.UnknownHostException;
import java.util.Hashtable;

/** 
 * 프로그램명 : SequenceUtil.java 
 * 
 * - 전문의 각종 일련번호 생성 유틸리티 - 
 */

public class SequenceUtil {

	private static Hashtable<String, String> Instances = new Hashtable<String, String>();
	private static Hashtable<String, Integer> Seqs = new Hashtable<String, Integer>();
	
	//20140106  0711C08X  141051859 0001200
	public static String getGlobalID() {
		return GUIDGenerator.generateKiupGID();
	}
	 
	// STTL_SRN 용
	// 1안) 인스턴스ID(2) + 'HHMMSSTTT' + 순번(3)	
	public static String get(String instance_id, int seqSize) {
		String datetime = DateUtil.getCurrentTime(DateUtil.TimeStampType5);
		StringBuffer buf = new StringBuffer();		
		buf.append(instance_id)
		   .append(datetime)
		   .append(StringUtil.lpad(getSeq(instance_id,datetime),seqSize));
		return buf.toString();
	}
	
	public synchronized static int getSeq(String instance_id, String datetime) {
		String beforeDateTime = Instances.get(instance_id);
		int seq = 0;
		if(beforeDateTime == null)
		{
			Instances.put(instance_id,datetime);
			Seqs.put(instance_id, new Integer(0));
		}		
		else if ( beforeDateTime.compareTo(datetime) < 0 )
		{
			Instances.put(instance_id,datetime);
			Seqs.put(instance_id, new Integer(0));
		}
		else if ( beforeDateTime.compareTo(datetime) > 0 )
		{
			Instances.put(instance_id,datetime);
			Seqs.put(instance_id, new Integer(0));
		}
		else if ( beforeDateTime.compareTo(datetime) == 0 )
		{
			Integer iseq = Seqs.get(instance_id);
			seq = iseq.intValue();
			seq++;
			Seqs.put(instance_id, new Integer(seq));
		}
		return seq;
	}

	
	private SequenceUtil() {
		
	}
	
	public static void main(String[] args) {
		for(int i=0;i<1000;i++)
		{
			System.out.println(SequenceUtil.get("01",3));
		}
		for(int i=0;i<1000;i++)
		{
			System.out.println(SequenceUtil.getGlobalID());
		}
		java.net.InetAddress localMachine = null;
		try {
			localMachine = java.net.Inet4Address.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(localMachine.getHostAddress()); 
	}
}
