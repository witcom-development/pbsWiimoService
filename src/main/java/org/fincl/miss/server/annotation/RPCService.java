package org.fincl.miss.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Required;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface RPCService {

	/** service Id */
	@Required
	String serviceId();

	/** service name */
	@Required
	String serviceName();

	/** description */
	String description() default "";

	/** input definition */
	String paramDecription() default "";

	/** output definition */
	String returnDescription() default "";

	/** example */
	String example() default "";
}
