package org.glassfish.jersey.internal.inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public interface InjectionManager {
   void completeRegistration();

   void shutdown();

   boolean isShutdown();

   void register(Binding var1);

   void register(Iterable<Binding> var1);

   void register(Binder var1);

   void register(Object var1) throws IllegalArgumentException;

   boolean isRegistrable(Class<?> var1);

   <T> T create(Class<T> var1);

   <T> T createAndInitialize(Class<T> var1);

   <T> List<ServiceHolder<T>> getAllServiceHolders(Class<T> var1, Annotation... var2);

   <T> T getInstance(Class<T> var1, Annotation... var2);

   <T> T getInstance(Class<T> var1, String var2);

   <T> T getInstance(Class<T> var1);

   <T> T getInstance(Type var1);

   Object getInstance(ForeignDescriptor var1);

   ForeignDescriptor createForeignDescriptor(Binding var1);

   <T> List<T> getAllInstances(Type var1);

   void inject(Object var1);

   void inject(Object var1, String var2);

   void preDestroy(Object var1);
}
