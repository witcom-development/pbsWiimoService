package org.fincl.miss.server.message.parser.telegram.util;

 
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

 

import org.apache.commons.lang3.time.DateFormatUtils;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicInteger;

/**
 * @author helios
 *
 */
public class GUIDGenerator {
    
    private static SecureRandom _secureRand;
    private static Random _rand;
    private static AtomicInteger index = new AtomicInteger(0);
    private static String hostName;
    private static String fullHostName;
//    private static String ip;
    
    static {
        _secureRand = new SecureRandom();
        long l = _secureRand.nextLong();
        _rand = new Random(l);
        try {
            java.net.InetAddress localMachine = java.net.Inet4Address.getLocalHost();
            hostName = localMachine.getHostName().toLowerCase().trim();
            fullHostName = hostName;
//            ip = localMachine.getHostAddress();
            if(hostName.length() > 8) {
                 
                hostName = hostName.substring(0, 8);
            } else {
                hostName = StringUtil.rpad(hostName, 8);
            }
            System.out.println("Hostname of local machine: " + hostName);
        } catch (java.net.UnknownHostException e) { // [beware typo in code sample -dmw]
            e.printStackTrace();
        }
    }
    
    public static String generate(String hostName, String appl_code, String flowNodeSeq) {
        StringBuffer stringbuffer = new StringBuffer();
        
        String docCreateDate = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
        stringbuffer.append(docCreateDate.substring(0, 17));
        if(hostName.length() == 2) {
        } else if(hostName.length() < 2) {
            hostName = StringUtil.lpad(hostName, 2, "0");
        } else if(hostName.length() < 2) {
            hostName = StringUtil.substring(hostName, hostName.length()-2);
        }
        
        String appl_code_attach = "0000";
        
        String docGenSystem = hostName+appl_code+appl_code_attach;
        stringbuffer.append(docGenSystem);
        
        String standardSeqNumAttach = new Integer(getAndIncrementModuloN(99999)).toString();
        standardSeqNumAttach = StringUtil.lpad(standardSeqNumAttach, 5, "0");
        String standardSeqNum = standardSeqNumAttach;
        stringbuffer.append(standardSeqNum);
        stringbuffer.append(flowNodeSeq);
        
        return stringbuffer.toString();
    }
    
    public static String generateKiupGID() {
        long start = System.currentTimeMillis();
        String STTL_WRTN_YMD = DateFormatUtils.format(start, "yyyyMMdd");
        String STTL_SRN1 = DateFormatUtils.format(start, "HHmmssSSS");      
        String STTL_SRN2 = StringUtil.lpad(Integer.toString(index.getAndIncrement()), 5, "0");
        String STTL_GLBL_ID = STTL_WRTN_YMD+hostName+STTL_SRN1+STTL_SRN2+"00"+"00";
        
        //작성년월일(8) + 시스템명(8) + 일련번호(14) + 진행구분번호(2) + 진행번호(2)
        
        return STTL_GLBL_ID;
    }
    
    
    public static String generate() {
        String flowNodeSeq = "00";
        StringBuffer stringbuffer = new StringBuffer();
        String docCreateDate = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS");
        stringbuffer.append(docCreateDate.substring(0, 17));
        
        if(hostName.length() < 8) {
            hostName = StringUtil.rpad(hostName, 8, "0");
        }
        String docGenSystem = StringUtil.substring(hostName, 0, 8);
        stringbuffer.append(docGenSystem);
        String standardSeqNumAttach = new Integer(getAndIncrementModuloN(99)).toString();
        standardSeqNumAttach = StringUtil.lpad(standardSeqNumAttach, 5, "0");
        String standardSeqNum = standardSeqNumAttach;
        stringbuffer.append(standardSeqNum);
        stringbuffer.append(flowNodeSeq);       
        return stringbuffer.toString();
    }
    
    /**
     * 사용하는 프로세서의 인덱스를 얻기
     */
    private static int getAndIncrementModuloN(int modulus) {
        if (modulus == 0) {
            return -1;
        }
        while (true) {
            int lastIndex = index.get();
            int nextIndex = (lastIndex + 1) % modulus;
            if (index.compareAndSet(lastIndex, nextIndex)) {
                return nextIndex;
            }
        }
    }
    
    public static String generate(boolean secureflag, boolean sep) throws UnknownHostException {
        
        MessageDigest messagedigest;
        
        StringBuffer stringbuffer = new StringBuffer();
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch(NoSuchAlgorithmException nosuchalgorithmexception) {
            throw new RuntimeException(nosuchalgorithmexception);
        }
        StringBuffer stringbuffer2;
        
        InetAddress inetaddress = InetAddress.getLocalHost();
        
        long l = System.currentTimeMillis();
        long l1 = 0L;
        if(secureflag)
            l1 = _secureRand.nextLong();
        else
            l1 = _rand.nextLong();
        stringbuffer.append(inetaddress.toString());
        stringbuffer.append(":");
        stringbuffer.append(Long.toString(l));
        stringbuffer.append(":");
        stringbuffer.append(Long.toString(l1));
        messagedigest.update(stringbuffer.toString().getBytes());
        byte abyte0[] = messagedigest.digest();
        StringBuffer stringbuffer1 = new StringBuffer();
        for(int i = 0; i < abyte0.length; i++) {
            int j = abyte0[i] & 0xff;
            if(j < 16)
                stringbuffer1.append('0');
            stringbuffer1.append(Integer.toHexString(j));
        }
        
        String s = stringbuffer1.toString().toUpperCase();
        stringbuffer2 = new StringBuffer();
        
        if (sep) {
            stringbuffer2.append(s.substring(0, 8));
            stringbuffer2.append("-");
            stringbuffer2.append(s.substring(8, 12));
            stringbuffer2.append("-");
            stringbuffer2.append(s.substring(12, 16));
            stringbuffer2.append("-");
            stringbuffer2.append(s.substring(16, 20));
            stringbuffer2.append("-");
            stringbuffer2.append(s.substring(20));
            return stringbuffer2.toString();
        } else {
            return s;
        }        
    }
    
    
    
    public static String getFullHostName() {
        return fullHostName;
    }

    public static void main(String args[]) throws Exception {       
        for (int i = 0; i < 103; i++) {
            long start = System.currentTimeMillis();
            String guid =  generateKiupGID();
            long end = System.currentTimeMillis();          
            System.out.println("RandomGUID = " + guid+" = "+(end-start));
        }        
    }
    
}