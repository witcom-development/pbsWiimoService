package org.fincl.miss.server.message.parser.telegram.valueobjects;

/** 
 * - VO 링크저장구조 - 
 */

public class LinkEntry {
    
    private int index = 0; 
    private Object obj = null;
    
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public Object getObj() {
        return obj;
    }
    public void setObj(Object obj) {
        this.obj = obj;
    }
//    public Object getKey() {
//        return key;
//    }
//    public void setKey(Object key) {
//        this.key = key;
//    }   
}
