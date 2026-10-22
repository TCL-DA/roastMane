package org.glassfish.hk2.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.jvnet.hk2.annotations.Contract;

@Contract
public interface ClassAnalyzer {
   String DEFAULT_IMPLEMENTATION_NAME = "default";

   <T> Constructor<T> getConstructor(Class<T> var1) throws MultiException, NoSuchMethodException;

   <T> Set<Method> getInitializerMethods(Class<T> var1) throws MultiException;

   <T> Set<Field> getFields(Class<T> var1) throws MultiException;

   <T> Method getPostConstructMethod(Class<T> var1) throws MultiException;

   <T> Method getPreDestroyMethod(Class<T> var1) throws MultiException;
}
