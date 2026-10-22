package org.glassfish.jersey.server;

import jakarta.ws.rs.core.Configuration;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Documented
public @interface ClientBinding {
   Class<? extends Configuration> configClass() default Configuration.class;

   boolean inheritServerProviders() default true;

   String baseUri() default "";
}
