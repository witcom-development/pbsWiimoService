 
package org.fincl.miss.server.message.parser.telegram;

/** 
 * 
 * - 클래스 설명 - 
 */
  
public interface MessageHeader { 
    //public void makeHeader2(Message message,String txCode);
    public void makeHeader(Message message);
    
    public void makeErrHeader(Message message);
    
    public void makeNextHeader(Message message);
    

}
