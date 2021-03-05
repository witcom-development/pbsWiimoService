package org.fincl.miss.server.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MessageHeader implements Map<String, Object>, Serializable {
    
    private static final long serialVersionUID = 6901029029524535147L;
    
    private static final Logger logger = LoggerFactory.getLogger(MessageHeader.class);
    
    private static volatile IdGenerator idGenerator = null;
    
    /**
     * The key for the Message ID. This is an automatically generated UUID and
     * should never be explicitly set in the header map <b>except</b> in the
     * case of Message deserialization where the serialized Message's generated
     * UUID is being restored.
     */
    public static final String ID = "id";
    
    public static final String GUID = "guid";
    
    public static final String TIMESTAMP = "timestamp";
    
    public static final String CLIENT_IP = "clientIp";
    
    public static final String CLIENT_ID = "clientId";
    
    public static final String SERVICE_ID = "serviceId";
    
    public static final String CORRELATION_ID = "correlationId";
    
    public static final String EXPIRATION_DATE = "expirationDate";
    
    public static final String PRIORITY = "priority";
    
    public static final String SEQUENCE_NUMBER = "sequenceNumber";
    
    public static final String SEQUENCE_SIZE = "sequenceSize";
    
    public static final String SEQUENCE_DETAILS = "sequenceDetails";
    
    private final Map<String, Object> headers;
    
    public MessageHeader(Map<String, Object> headers) {
        this.headers = (headers != null) ? new HashMap<String, Object>(headers) : new HashMap<String, Object>();
        if (this.headers.get(ID) == null) {
            if (MessageHeader.idGenerator == null) {
                this.headers.put(ID, UUID.randomUUID().toString());
            }
            else {
                this.headers.put(ID, MessageHeader.idGenerator.generateId().toString());
            }
        }
        this.headers.put(TIMESTAMP, new Long(System.currentTimeMillis()));
        this.headers.put(CORRELATION_ID, this.headers.get(ID));
    }
    
    public String getId() {
        return this.get(ID, String.class);
    }
    
    public Long getTimestamp() {
        return this.get(TIMESTAMP, Long.class);
    }
    
    public Long getExpirationDate() {
        return this.get(EXPIRATION_DATE, Long.class);
    }
    
    public String getCorrelationId() {
        return this.get(CORRELATION_ID, String.class);
    }
    
    public String getClientIp() {
        return this.get(CLIENT_IP, String.class);
    }
    
    public String getClientId() {
        return this.get(CLIENT_ID, String.class);
    }
    
    public String getServiceId() {
        return this.get(SERVICE_ID, String.class);
    }
    
    public Integer getSequenceNumber() {
        Integer sequenceNumber = this.get(SEQUENCE_NUMBER, Integer.class);
        return (sequenceNumber != null ? sequenceNumber : 0);
    }
    
    public Integer getSequenceSize() {
        Integer sequenceSize = this.get(SEQUENCE_SIZE, Integer.class);
        return (sequenceSize != null ? sequenceSize : 0);
    }
    
    public Integer getPriority() {
        return this.get(PRIORITY, Integer.class);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        Object value = this.headers.get(key);
        if (value == null) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Incorrect type specified for header '" + key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
        }
        return (T) value;
    }
    
    public int hashCode() {
        return this.headers.hashCode();
    }
    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof MessageHeader) {
            MessageHeader other = (MessageHeader) obj;
            return this.headers.equals(other.headers);
        }
        return false;
    }
    
    public String toString() {
        return this.headers.toString();
    }
    
    /*
     * Map implementation
     */
    
    public boolean containsKey(Object key) {
        return this.headers.containsKey(key);
    }
    
    public boolean containsValue(Object value) {
        return this.headers.containsValue(value);
    }
    
    public Set<Map.Entry<String, Object>> entrySet() {
        return Collections.unmodifiableSet(this.headers.entrySet());
    }
    
    public Object get(Object key) {
        return this.headers.get(key);
    }
    
    public boolean isEmpty() {
        return this.headers.isEmpty();
    }
    
    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.headers.keySet());
    }
    
    public int size() {
        return this.headers.size();
    }
    
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(this.headers.values());
    }
    
    /*
     * Unsupported operations
     */
    /**
     * Since MessageHeaders are immutable the call to this method will result in {@link UnsupportedOperationException}
     */
    public Object put(String key, Object value) {
        // throw new UnsupportedOperationException("MessageHeaders is immutable.");
        return this.headers.put(key, value);
    }
    
    /**
     * Since MessageHeaders are immutable the call to this method will result in {@link UnsupportedOperationException}
     */
    public void putAll(Map<? extends String, ? extends Object> t) {
        // throw new UnsupportedOperationException("MessageHeaders is immutable.");
        this.headers.putAll(t);
    }
    
    /**
     * Since MessageHeaders are immutable the call to this method will result in {@link UnsupportedOperationException}
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException("MessageHeaders is immutable.");
    }
    
    /**
     * Since MessageHeaders are immutable the call to this method will result in {@link UnsupportedOperationException}
     */
    public void clear() {
        throw new UnsupportedOperationException("MessageHeaders is immutable.");
    }
    
    /*
     * Serialization methods
     */
    
    private void writeObject(ObjectOutputStream out) throws IOException {
        List<String> keysToRemove = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : this.headers.entrySet()) {
            if (!(entry.getValue() instanceof Serializable)) {
                keysToRemove.add(entry.getKey());
            }
        }
        for (String key : keysToRemove) {
            if (logger.isInfoEnabled()) {
                logger.info("removing non-serializable header: " + key);
            }
            this.headers.remove(key);
        }
        out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
    
    public static interface IdGenerator {
        UUID generateId();
    }
}