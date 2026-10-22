package org.jvnet.hk2.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@InhabitantAnnotation("default")
public @interface Service {
   String name() default "";

   String metadata() default "";

   String analyzer() default "default";
}
