package org.glassfish.hk2.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

public interface ActiveDescriptor<T> extends Descriptor, SingleCache<T> {
   boolean isReified();

   Class<?> getImplementationClass();

   Type getImplementationType();

   Set<Type> getContractTypes();

   Annotation getScopeAsAnnotation();

   Class<? extends Annotation> getScopeAnnotation();

   Set<Annotation> getQualifierAnnotations();

   List<Injectee> getInjectees();

   Long getFactoryServiceId();

   Long getFactoryLocatorId();

   T create(ServiceHandle<?> var1);

   void dispose(T var1);
}
