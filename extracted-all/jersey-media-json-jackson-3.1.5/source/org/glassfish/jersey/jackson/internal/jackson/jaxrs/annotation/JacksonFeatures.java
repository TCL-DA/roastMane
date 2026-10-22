package org.glassfish.jersey.jackson.internal.jackson.jaxrs.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JacksonFeatures {
   DeserializationFeature[] deserializationEnable() default {};

   DeserializationFeature[] deserializationDisable() default {};

   SerializationFeature[] serializationEnable() default {};

   SerializationFeature[] serializationDisable() default {};
}
