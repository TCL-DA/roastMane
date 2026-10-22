package org.glassfish.hk2.utilities;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE})
public @interface Stub {
   Stub.Type value() default Stub.Type.VALUES;

   public static enum Type {
      VALUES,
      EXCEPTIONS;
   }
}
