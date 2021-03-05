package org.fincl.miss.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EnumCode {
    
    /**
     * 버젼정보
     * 
     * @author Administrator
     */
    public static enum Version {
        
        UNDEFINED(""), MOBP_RPC_1_0("MOBP-RPC-1.0");
        
        private final String name;
        
        Version(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static Version getEnum(String value) {
            for (Version e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 모드 타입
     * 
     * @author Administrator
     */
    public static enum InOutModeType {
        
        SERVER("001"), CLIENT("002");
        
        private final String name;
        
        InOutModeType(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static InOutModeType getEnum(String value) {
            for (InOutModeType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * Channel 상태
     * 
     * @author Administrator
     */
    public static enum ChannelStatus {
        
        DEAD(false), ALIVE(true);
        
        private final boolean name;
        
        ChannelStatus(boolean name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return String.valueOf(this.name);
        }
        
        public static ChannelStatus getEnum(String value) {
            for (ChannelStatus e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 프로토콜 타입
     * 
     * @author Administrator
     */
    public static enum ProtocolType {
        
        TCP("001"), HTTP("002"), REST("003"), WEBSOCKET("004"), SERVLET("005");
        
        private final String name;
        
        ProtocolType(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static ProtocolType getEnum(String value) {
            for (ProtocolType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 싱글 Yn
     * 
     * @author Administrator
     */
    public static enum SingleYn {
        
        YES("Y"), NO("N");
        
        private final String name;
        
        SingleYn(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static SingleYn getEnum(String value) {
            for (SingleYn e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * SSL Yn
     * 
     * @author Administrator
     */
    public static enum SSLYn {
        
        YES("Y"), NO("N");
        
        private final String name;
        
        SSLYn(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static SSLYn getEnum(String value) {
            for (SSLYn e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * SSL Yn
     * 
     * @author Administrator
     */
    public static enum AutoStartYn {
        
        YES("Y"), NO("N");
        
        private final String name;
        
        AutoStartYn(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static AutoStartYn getEnum(String value) {
            for (AutoStartYn e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 요청 데이터포멧.
     * 
     * @author Administrator
     */
    public static enum RequestDataType {
        
        LENGTH_HEADER("001"), DELIMETER("002"), XML("003"), JSON("004"), FORM("005"), BICYCLE("006"), STRESS("007"), ECHO("008"), FILEDOWNLOAD("009")
        , SMART("010");
        
        private final String name;
        
        RequestDataType(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static RequestDataType getEnum(String value) {
            for (RequestDataType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 요청 데이터포멧.
     * 
     * @author Administrator
     */
    public static enum ResponseDataType {
        
        LENGTH_HEADER("001"), DELIMETER("002"), XML("003"), JSON("004"), BICYCLE("006"), STRESS("007"), ECHO("008") , SMART("010");
        
        private final String name;
        
        ResponseDataType(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static ResponseDataType getEnum(String value) {
            for (ResponseDataType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 요청 및 응답 데이터포멧.
     * 
     * @author Administrator
     */
    public static enum ContentType {
        
        UNDEFINED(""), APPLICATION_JSON("application/json; charset=UTF-8"), APPLICATION_XML("application/xml; charset=UTF-8"), APPLICATION_FORM("application/x-www-form-urlencoded; charset=UTF-8"), LENGTH_HEADER_STRING("lengthHeaderString"), DELIMETER_STRING("delimeterString");
        
        private final String name;
        
        ContentType(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static ContentType getEnum(String value) {
            for (ContentType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 요청 및 응답 데이터포멧.
     * 
     * @author Administrator
     */
    public static enum Accept {
        
        UNDEFINED(""), APPLICATION_JSON("application/json; charset=UTF-8"), APPLICATION_XML("application/xml; charset=UTF-8");
        
        private final String name;
        
        Accept(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static Accept getEnum(String value) {
            for (Accept e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 데이타의 RAW 타입
     * 
     * @author Administrator
     */
    public static enum DataRawType {
        
        STRING("001"), BYTE("002");
        
        private final String name;
        
        DataRawType(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static DataRawType getEnum(String value) {
            for (DataRawType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 데이타의 RAW 타입
     * 
     * @author Administrator
     */
    public static enum Charset {
        
        UTF_8("001"), EUC_KR("002");
        
        private final String name;
        
        Charset(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        @Override
        public String toString() {
            return super.toString().replaceAll("[_]", "-");
        }
        
        public static Charset getEnum(String value) {
            for (Charset e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 지원 데이터 타입
     * 
     * @author Administrator
     */
    public static enum ValueType {
        
        ARRAY(new Class<?>[] { ArrayList.class }), LIST(new Class<?>[] { List.class }), MAP(new Class<?>[] { Map.class }), HASHMAP(new Class<?>[] { HashMap.class }), OBJECT(new Class<?>[] { LinkedHashMap.class }), NUMBER(new Class<?>[] { Float.class, Double.class }), INTEGER(new Class<?>[] { Integer.class, Long.class }), BOOLEAN(new Class<?>[] { Boolean.class }), STRING(new Class<?>[] { String.class });
        
        private final Class<?>[] classes;
        
        private ValueType(Class<?>[] classes) {
            
            this.classes = classes;
        }
        
        public static String getName(Class<?> clazz) {
            
            // check all data types
            for (ValueType type : ValueType.values()) {
                for (Class<?> c : type.classes) {
                    if (clazz.equals(c)) {
                        return type.getValue();
                    }
                }
            }
            
            // non of the above, so this must be POJO object
            return ValueType.OBJECT.getValue();
        }
        
        public String getValue() {
            
            return name().toLowerCase();
        }
    }
    
    /**
     * 처리결과
     * 
     * @author Administrator
     */
    public static enum Result {
        
        TRUE("1"), FALSE("0");
        
        private final String name;
        
        Result(String name) {
            
            this.name = name;
        }
        
        public String getValue() {
            
            return this.name;
        }
        
        public static Result getEnum(String value) {
            for (Result e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * E2E 사용여부
     * 
     * @author Administrator
     */
    public static enum HeaderE2E {
        USE("1"), NOT_USE("0");
        
        private final String name;
        
        HeaderE2E(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.name;
        }
        
        public static HeaderE2E getEnum(String value) {
            for (HeaderE2E e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 응답 또는 요청 구분
     * 
     * @author Administrator
     */
    public static enum DataType {
        REQUEST("request"), RESPONSE("response");
        
        private final String name;
        
        DataType(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.name;
        }
        
        public static DataType getEnum(String value) {
            for (DataType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * 용청 / 응답 데이터 형식
     * 
     * @author Administrator
     */
    public static enum DataFormat {
        LENGTH_HEADER("LH"), DELIMETER("DE"), XML("XM"), JSON("JS"), FORM_DATA("FD");
        
        private final String name;
        
        DataFormat(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.name;
        }
        
        public static DataFormat getEnum(String value) {
            for (DataFormat e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
    
    /**
     * Sync 타입
     * 
     * @author Administrator
     */
    public static enum SyncType {
        SYNC("001"), ASYNC("002"), PUBSUB("003");
        
        private final String name;
        
        SyncType(String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.name;
        }
        
        public static SyncType getEnum(String value) {
            for (SyncType e : values()) {
                if (e.getValue().equalsIgnoreCase(value)) {
                    return e;
                }
            }
            return null;
        }
    }
}
