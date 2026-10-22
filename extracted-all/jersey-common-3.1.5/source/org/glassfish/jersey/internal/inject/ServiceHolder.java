package org.glassfish.jersey.internal.inject;

import java.lang.reflect.Type;
import java.util.Set;

public interface ServiceHolder<T> {
   T getInstance();

   Class<T> getImplementationClass();

   Set<Type> getContractTypes();

   int getRank();
}
