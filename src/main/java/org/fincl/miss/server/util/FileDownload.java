package org.fincl.miss.server.util;

public class FileDownload {
    
    /**
     * Sevlet 채널에서 파일 다운로드 처리시 속성 정의
     * 
     * @author Administrator
     */
    public static enum Attribute {
        
        FILENAME("filename"), FILE("file"), CHECKSUM("ETag");
        
        private final String name;
        
        Attribute(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static Attribute getEnum(String value) {
            for (Attribute e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
}
