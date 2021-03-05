package org.fincl.miss.server.scheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Batch 멀티구성시 먼저 수행된 node에서만 수행되도록 설정하는 어노테이션.
 * @author civan
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ClusterSynchronized {

/**
* job 식별 아이디.
* 유니크한 식별자이며, job id를 의미한다.
*
* @return
*/
String jobToken();


}
