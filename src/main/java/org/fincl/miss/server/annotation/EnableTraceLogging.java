package org.fincl.miss.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface EnableTraceLogging {
    
    /**
     * 로깅 시작여부
     * 
     * @return
     */
    boolean isStart() default false;
}
