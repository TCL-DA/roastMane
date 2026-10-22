package org.glassfish.hk2.api;

import java.lang.annotation.Annotation;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface Context<T> {
   Class<? extends Annotation> getScope();

   <U> U findOrCreate(ActiveDescriptor<U> var1, ServiceHandle<?> var2);

   boolean containsKey(ActiveDescriptor<?> var1);

   void destroyOne(ActiveDescriptor<?> var1);

   boolean supportsNullCreation();

   boolean isActive();

   void shutdown();
}
