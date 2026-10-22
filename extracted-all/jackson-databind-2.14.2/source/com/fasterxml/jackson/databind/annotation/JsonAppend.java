package com.fasterxml.jackson.databind.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonAppend {
   JsonAppend.Attr[] attrs() default {};

   JsonAppend.Prop[] props() default {};

   boolean prepend() default false;

   public @interface Attr {
      String value();

      String propName() default "";

      String propNamespace() default "";

      Include include() default Include.NON_NULL;

      boolean required() default false;
   }

   public @interface Prop {
      Class<? extends VirtualBeanPropertyWriter> value();

      String name() default "";

      String namespace() default "";

      Include include() default Include.NON_NULL;

      boolean required() default false;

      Class<?> type() default Object.class;
   }
}
