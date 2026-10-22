package org.glassfish.hk2.utilities.reflection.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.cache.HybridCacheEntry;
import org.glassfish.hk2.utilities.cache.LRUHybridCache;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;

public class ClassReflectionHelperImpl implements ClassReflectionHelper {
   private final int MAX_CACHE_SIZE = 20000;
   private final LRUHybridCache<ClassReflectionHelperImpl.LifecycleKey, Method> postConstructCache = new LRUHybridCache<>(
      20000,
      new Computable<ClassReflectionHelperImpl.LifecycleKey, HybridCacheEntry<Method>>() {
         public HybridCacheEntry<Method> compute(ClassReflectionHelperImpl.LifecycleKey key) {
            return ClassReflectionHelperImpl.this.postConstructCache
               .createCacheEntry(key, ClassReflectionHelperImpl.this.getPostConstructMethod(key.clazz, key.matchingClass), false);
         }
      }
   );
   private final LRUHybridCache<ClassReflectionHelperImpl.LifecycleKey, Method> preDestroyCache = new LRUHybridCache<>(
      20000,
      new Computable<ClassReflectionHelperImpl.LifecycleKey, HybridCacheEntry<Method>>() {
         public HybridCacheEntry<Method> compute(ClassReflectionHelperImpl.LifecycleKey key) {
            return ClassReflectionHelperImpl.this.preDestroyCache
               .createCacheEntry(key, ClassReflectionHelperImpl.this.getPreDestroyMethod(key.clazz, key.matchingClass), false);
         }
      }
   );
   private final LRUHybridCache<Class<?>, Set<MethodWrapper>> methodCache = new LRUHybridCache<>(
      20000, new Computable<Class<?>, HybridCacheEntry<Set<MethodWrapper>>>() {
         public HybridCacheEntry<Set<MethodWrapper>> compute(Class<?> key) {
            return ClassReflectionHelperImpl.this.methodCache.createCacheEntry(key, ClassReflectionHelperUtilities.getAllMethodWrappers(key), false);
         }
      }
   );
   private final LRUHybridCache<Class<?>, Set<Field>> fieldCache = new LRUHybridCache<>(20000, new Computable<Class<?>, HybridCacheEntry<Set<Field>>>() {
      public HybridCacheEntry<Set<Field>> compute(Class<?> key) {
         return ClassReflectionHelperImpl.this.fieldCache.createCacheEntry(key, ClassReflectionHelperUtilities.getAllFieldWrappers(key), false);
      }
   });

   @Override
   public Set<MethodWrapper> getAllMethods(Class<?> clazz) {
      return this.methodCache.compute(clazz).getValue();
   }

   @Override
   public Set<Field> getAllFields(Class<?> clazz) {
      return this.fieldCache.compute(clazz).getValue();
   }

   @Override
   public Method findPostConstruct(Class<?> clazz, Class<?> matchingClass) throws IllegalArgumentException {
      return this.postConstructCache.compute(new ClassReflectionHelperImpl.LifecycleKey(clazz, matchingClass)).getValue();
   }

   @Override
   public Method findPreDestroy(Class<?> clazz, Class<?> matchingClass) throws IllegalArgumentException {
      return this.preDestroyCache.compute(new ClassReflectionHelperImpl.LifecycleKey(clazz, matchingClass)).getValue();
   }

   @Override
   public void clean(Class<?> clazz) {
      while (clazz != null && !Object.class.equals(clazz)) {
         this.postConstructCache.remove(new ClassReflectionHelperImpl.LifecycleKey(clazz, null));
         this.preDestroyCache.remove(new ClassReflectionHelperImpl.LifecycleKey(clazz, null));
         this.methodCache.remove(clazz);
         this.fieldCache.remove(clazz);
         clazz = clazz.getSuperclass();
      }
   }

   @Override
   public MethodWrapper createMethodWrapper(Method m) {
      return new MethodWrapperImpl(m);
   }

   @Override
   public void dispose() {
      this.postConstructCache.clear();
      this.preDestroyCache.clear();
      this.methodCache.clear();
      this.fieldCache.clear();
   }

   @Override
   public int size() {
      return this.postConstructCache.size() + this.preDestroyCache.size() + this.methodCache.size() + this.fieldCache.size();
   }

   private Method getPostConstructMethod(Class<?> clazz, Class<?> matchingClass) {
      if (clazz != null && !Object.class.equals(clazz)) {
         if (matchingClass.isAssignableFrom(clazz)) {
            Method retVal;
            try {
               retVal = clazz.getMethod("postConstruct");
            } catch (NoSuchMethodException var6) {
               retVal = null;
            }

            return retVal;
         } else {
            for (MethodWrapper wrapper : this.getAllMethods(clazz)) {
               Method m = wrapper.getMethod();
               if (ClassReflectionHelperUtilities.isPostConstruct(m)) {
                  return m;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private Method getPreDestroyMethod(Class<?> clazz, Class<?> matchingClass) {
      if (clazz != null && !Object.class.equals(clazz)) {
         if (matchingClass.isAssignableFrom(clazz)) {
            Method retVal;
            try {
               retVal = clazz.getMethod("preDestroy");
            } catch (NoSuchMethodException var6) {
               retVal = null;
            }

            return retVal;
         } else {
            for (MethodWrapper wrapper : this.getAllMethods(clazz)) {
               Method m = wrapper.getMethod();
               if (ClassReflectionHelperUtilities.isPreDestroy(m)) {
                  return m;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   public String toString() {
      return "ClassReflectionHelperImpl(" + System.identityHashCode(this) + ")";
   }

   private static final class LifecycleKey {
      private final Class<?> clazz;
      private final Class<?> matchingClass;
      private final int hash;

      private LifecycleKey(Class<?> clazz, Class<?> matchingClass) {
         this.clazz = clazz;
         this.matchingClass = matchingClass;
         this.hash = clazz.hashCode();
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object o) {
         if (o == null) {
            return false;
         } else {
            return !(o instanceof ClassReflectionHelperImpl.LifecycleKey) ? false : this.clazz.equals(((ClassReflectionHelperImpl.LifecycleKey)o).clazz);
         }
      }
   }
}
