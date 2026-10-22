package org.glassfish.hk2.utilities.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public interface ClassReflectionHelper {
   Set<MethodWrapper> getAllMethods(Class<?> var1);

   MethodWrapper createMethodWrapper(Method var1);

   Set<Field> getAllFields(Class<?> var1);

   Method findPostConstruct(Class<?> var1, Class<?> var2) throws IllegalArgumentException;

   Method findPreDestroy(Class<?> var1, Class<?> var2) throws IllegalArgumentException;

   void clean(Class<?> var1);

   void dispose();

   int size();
}
