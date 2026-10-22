package org.glassfish.jersey.internal.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Set;

public interface Injectee {
   Type getRequiredType();

   Set<Annotation> getRequiredQualifiers();

   int getPosition();

   Class<?> getInjecteeClass();

   AnnotatedElement getParent();

   boolean isOptional();

   ForeignDescriptor getInjecteeDescriptor();

   Class<? extends Annotation> getParentClassScope();

   boolean isFactory();

   boolean isProvider();
}
