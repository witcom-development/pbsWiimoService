package org.fincl.miss.server.reflection;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;

public class DynamicAccessor<T> {
    
    private static Logger logger = LoggerFactory.getLogger(DynamicAccessor.class);
    
    private final Class<T> targetType;
    private final ConstructorAccess<T> ctorAccessor;
    private final FieldAccess fieldAccessor;
    private final MethodAccess methodAccessor;
    
    private final List<String> fieldNames;
    private final List<String> methodNames;
    
    public DynamicAccessor(Class<T> targetType) {
        // Guard.shouldNotBeNull(targetType, "targetType");
        // if (log.isDebugEnabled()) log.debug("");
        
        this.targetType = targetType;
        this.ctorAccessor = ConstructorAccess.get(this.targetType);
        this.fieldAccessor = FieldAccess.get(this.targetType);
        this.methodAccessor = MethodAccess.get(this.targetType);
        
        this.fieldNames = Lists.newArrayList(fieldAccessor.getFieldNames());
        this.methodNames = Lists.newArrayList(methodAccessor.getMethodNames());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T newInstance() {
        return (T) ctorAccessor.newInstance();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T newInstance(Object enclosingInstance) {
        return (T) ctorAccessor.newInstance(enclosingInstance);
    }
    
    public String[] getFieldNames() {
        return fieldAccessor.getFieldNames();
    }
    
    public String[] getMethodNames() {
        return methodAccessor.getMethodNames();
    }
    
    public Object getField(Object instance, String fieldName) {
        return fieldAccessor.get(instance, fieldName);
    }
    
    public void setField(Object instance, String fieldName, Object nv) {
        fieldAccessor.set(instance, fieldName, nv);
    }
    
    public void setFieldBoolean(Object instance, String fieldName, boolean nv) {
        fieldAccessor.setBoolean(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public void setFieldByte(Object instance, String fieldName, byte nv) {
        fieldAccessor.setByte(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public void setFieldChar(Object instance, String fieldName, char nv) {
        fieldAccessor.setChar(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public void setFieldDouble(Object instance, String fieldName, double nv) {
        fieldAccessor.setDouble(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public void setFieldFloat(Object instance, String fieldName, float nv) {
        fieldAccessor.setFloat(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public void setFieldInt(Object instance, String fieldName, int nv) {
        fieldAccessor.setInt(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public void setFieldLong(Object instance, String fieldName, long nv) {
        fieldAccessor.setLong(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public void setFieldShort(Object instance, String fieldName, short nv) {
        fieldAccessor.setShort(instance, fieldAccessor.getIndex(fieldName), nv);
    }
    
    public Object getProperty(Object instance, String fieldName) {
        String methodName = (methodNames.contains(fieldName)) ? fieldName : "get" + getPropertyName(fieldName);
        
        return invoke(instance, methodName);
    }
    
    public void setProperty(Object instance, String fieldName, Object nv) {
        String methodName = (methodNames.contains(fieldName)) ? fieldName : "set" + getPropertyName(fieldName);
        invoke(instance, methodName, nv);
    }
    
    public Object invoke(Object instance, String methodName, Object... args) {
        return methodAccessor.invoke(instance, methodName, args);
    }
    
    private static final String getPropertyName(String filedName) {
        return filedName.substring(0, 1).toUpperCase() + filedName.substring(1);
    }
}