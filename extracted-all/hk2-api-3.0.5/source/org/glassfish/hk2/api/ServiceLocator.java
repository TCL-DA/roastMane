package org.glassfish.hk2.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ServiceLocator {
   <T> T getService(Class<T> var1, Annotation... var2) throws MultiException;

   <T> T getService(Type var1, Annotation... var2) throws MultiException;

   <T> T getService(Class<T> var1, String var2, Annotation... var3) throws MultiException;

   <T> T getService(Type var1, String var2, Annotation... var3) throws MultiException;

   <T> List<T> getAllServices(Class<T> var1, Annotation... var2) throws MultiException;

   <T> List<T> getAllServices(Type var1, Annotation... var2) throws MultiException;

   <T> List<T> getAllServices(Annotation var1, Annotation... var2) throws MultiException;

   List<?> getAllServices(Filter var1) throws MultiException;

   <T> ServiceHandle<T> getServiceHandle(Class<T> var1, Annotation... var2) throws MultiException;

   <T> ServiceHandle<T> getServiceHandle(Type var1, Annotation... var2) throws MultiException;

   <T> ServiceHandle<T> getServiceHandle(Class<T> var1, String var2, Annotation... var3) throws MultiException;

   <T> ServiceHandle<T> getServiceHandle(Type var1, String var2, Annotation... var3) throws MultiException;

   <T> List<ServiceHandle<T>> getAllServiceHandles(Class<T> var1, Annotation... var2) throws MultiException;

   List<ServiceHandle<?>> getAllServiceHandles(Type var1, Annotation... var2) throws MultiException;

   List<ServiceHandle<?>> getAllServiceHandles(Annotation var1, Annotation... var2) throws MultiException;

   List<ServiceHandle<?>> getAllServiceHandles(Filter var1) throws MultiException;

   List<ActiveDescriptor<?>> getDescriptors(Filter var1);

   ActiveDescriptor<?> getBestDescriptor(Filter var1);

   ActiveDescriptor<?> reifyDescriptor(Descriptor var1, Injectee var2) throws MultiException;

   ActiveDescriptor<?> reifyDescriptor(Descriptor var1) throws MultiException;

   ActiveDescriptor<?> getInjecteeDescriptor(Injectee var1) throws MultiException;

   <T> ServiceHandle<T> getServiceHandle(ActiveDescriptor<T> var1, Injectee var2) throws MultiException;

   <T> ServiceHandle<T> getServiceHandle(ActiveDescriptor<T> var1) throws MultiException;

   @Deprecated
   <T> T getService(ActiveDescriptor<T> var1, ServiceHandle<?> var2) throws MultiException;

   <T> T getService(ActiveDescriptor<T> var1, ServiceHandle<?> var2, Injectee var3) throws MultiException;

   String getDefaultClassAnalyzerName();

   void setDefaultClassAnalyzerName(String var1);

   Unqualified getDefaultUnqualified();

   void setDefaultUnqualified(Unqualified var1);

   String getName();

   long getLocatorId();

   ServiceLocator getParent();

   void shutdown();

   ServiceLocatorState getState();

   boolean isShutdown();

   boolean getNeutralContextClassLoader();

   void setNeutralContextClassLoader(boolean var1);

   <T> T create(Class<T> var1);

   <T> T create(Class<T> var1, String var2);

   void inject(Object var1);

   void inject(Object var1, String var2);

   Object assistedInject(Object var1, Method var2, MethodParameter... var3);

   Object assistedInject(Object var1, Method var2, ServiceHandle<?> var3, MethodParameter... var4);

   void postConstruct(Object var1);

   void postConstruct(Object var1, String var2);

   void preDestroy(Object var1);

   void preDestroy(Object var1, String var2);

   <U> U createAndInitialize(Class<U> var1);

   <U> U createAndInitialize(Class<U> var1, String var2);
}
