package org.glassfish.hk2.api;

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

   boolean isSelf();

   Unqualified getUnqualified();

   ActiveDescriptor<?> getInjecteeDescriptor();
}
