package org.controlsfx.control.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionProxy {
   String id() default "";

   String text();

   String graphic() default "";

   String longText() default "";

   String accelerator() default "";

   String factory() default "";
}
