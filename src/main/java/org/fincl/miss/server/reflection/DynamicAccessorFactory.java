package org.fincl.miss.server.reflection;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DynamicAccessorFactory {
    
    private static Logger logger = LoggerFactory.getLogger(DynamicAccessorFactory.class);
    
    private static final CacheLoader<Class<?>, DynamicAccessor> loader;
    private static final LoadingCache<Class<?>, DynamicAccessor> cache;
    
    static {
        loader = new CacheLoader<Class<?>, DynamicAccessor>() {
            @Override
            @SuppressWarnings("unchecked")
            public DynamicAccessor<?> load(Class<?> type) throws Exception {
                return new DynamicAccessor(type);
            }
        };
        
        cache = CacheBuilder.newBuilder().build(loader);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> DynamicAccessor<T> create(Class<T> targetType) {
        try {
            return (DynamicAccessor<T>) cache.get(targetType);
        }
        catch (ExecutionException e) {
            if (logger.isErrorEnabled()) logger.error("DynamicAccessor 를 생성하는데 실패했습니다. targetType=" + targetType.getName(), e);
            return null;
        }
    }
    
    public static void clear() {
        synchronized (cache) {
            cache.cleanUp();
        }
    }
}