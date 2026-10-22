package org.glassfish.jersey.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JSONP {
   String DEFAULT_CALLBACK = "callback";
   String DEFAULT_QUERY = "__callback";

   String callback() default "callback";

   String queryParam() default "";
}
