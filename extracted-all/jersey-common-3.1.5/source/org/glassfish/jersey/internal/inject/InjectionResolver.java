package org.glassfish.jersey.internal.inject;

import java.lang.annotation.Annotation;

public interface InjectionResolver<T extends Annotation> {
   Object resolve(Injectee var1);

   boolean isConstructorParameterIndicator();

   boolean isMethodParameterIndicator();

   Class<T> getAnnotation();
}
