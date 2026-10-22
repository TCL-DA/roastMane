package org.glassfish.hk2.api;

import jakarta.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface IterableProvider<T> extends Provider<T>, Iterable<T> {
   ServiceHandle<T> getHandle();

   int getSize();

   IterableProvider<T> named(String var1);

   <U> IterableProvider<U> ofType(Type var1);

   IterableProvider<T> qualifiedWith(Annotation... var1);

   Iterable<ServiceHandle<T>> handleIterator();
}
