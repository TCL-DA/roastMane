package nonapi.io.github.classgraph.json;

import java.lang.reflect.Constructor;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

class ClassFieldCache {
   private final Map<Class<?>, ClassFields> classToClassFields = new HashMap<>();
   private final boolean resolveTypes;
   private final boolean onlySerializePublicFields;
   private final Map<Class<?>, Constructor<?>> defaultConstructorForConcreteType = new HashMap<>();
   private final Map<Class<?>, Constructor<?>> constructorForConcreteTypeWithSizeHint = new HashMap<>();
   private static final Constructor<?> NO_CONSTRUCTOR;
   ReflectionUtils reflectionUtils;

   ClassFieldCache(boolean forDeserialization, boolean onlySerializePublicFields, ReflectionUtils reflectionUtils) {
      this.resolveTypes = forDeserialization;
      this.onlySerializePublicFields = !forDeserialization && onlySerializePublicFields;
      this.reflectionUtils = reflectionUtils;
   }

   ClassFields get(Class<?> cls) {
      ClassFields classFields = this.classToClassFields.get(cls);
      if (classFields == null) {
         this.classToClassFields.put(cls, classFields = new ClassFields(cls, this.resolveTypes, this.onlySerializePublicFields, this, this.reflectionUtils));
      }

      return classFields;
   }

   private static Class<?> getConcreteType(Class<?> rawType, boolean returnNullIfNotMapOrCollection) {
      if (rawType == Map.class || rawType == AbstractMap.class || rawType == HashMap.class) {
         return HashMap.class;
      } else if (rawType == ConcurrentMap.class || rawType == ConcurrentHashMap.class) {
         return ConcurrentHashMap.class;
      } else if (rawType == SortedMap.class || rawType == NavigableMap.class || rawType == TreeMap.class) {
         return TreeMap.class;
      } else if (rawType == ConcurrentNavigableMap.class || rawType == ConcurrentSkipListMap.class) {
         return ConcurrentSkipListMap.class;
      } else if (rawType == List.class || rawType == AbstractList.class || rawType == ArrayList.class || rawType == Collection.class) {
         return ArrayList.class;
      } else if (rawType == AbstractSequentialList.class || rawType == LinkedList.class) {
         return LinkedList.class;
      } else if (rawType == Set.class || rawType == AbstractSet.class || rawType == HashSet.class) {
         return HashSet.class;
      } else if (rawType == SortedSet.class || rawType == TreeSet.class) {
         return TreeSet.class;
      } else if (rawType == Queue.class || rawType == AbstractQueue.class || rawType == Deque.class || rawType == ArrayDeque.class) {
         return ArrayDeque.class;
      } else if (rawType == BlockingQueue.class || rawType == LinkedBlockingQueue.class) {
         return LinkedBlockingQueue.class;
      } else if (rawType == BlockingDeque.class || rawType == LinkedBlockingDeque.class) {
         return LinkedBlockingDeque.class;
      } else if (rawType != TransferQueue.class && rawType != LinkedTransferQueue.class) {
         return returnNullIfNotMapOrCollection ? null : rawType;
      } else {
         return LinkedTransferQueue.class;
      }
   }

   Constructor<?> getDefaultConstructorForConcreteTypeOf(Class<?> cls) {
      if (cls == null) {
         throw new IllegalArgumentException("Class reference cannot be null");
      } else {
         Constructor<?> constructor = this.defaultConstructorForConcreteType.get(cls);
         if (constructor != null) {
            return constructor;
         } else {
            Class<?> concreteType = getConcreteType(cls, false);

            for (Class<?> c = concreteType; c != null && (c != Object.class || cls == Object.class); c = c.getSuperclass()) {
               try {
                  Constructor<?> defaultConstructor = c.getDeclaredConstructor();
                  JSONUtils.makeAccessible(defaultConstructor, this.reflectionUtils);
                  this.defaultConstructorForConcreteType.put(cls, defaultConstructor);
                  return defaultConstructor;
               } catch (SecurityException | ReflectiveOperationException var6) {
               }
            }

            throw new IllegalArgumentException("Class " + cls.getName() + " does not have an accessible default (no-arg) constructor");
         }
      }
   }

   Constructor<?> getConstructorWithSizeHintForConcreteTypeOf(Class<?> cls) {
      Constructor<?> constructor = this.constructorForConcreteTypeWithSizeHint.get(cls);
      if (constructor == NO_CONSTRUCTOR) {
         return null;
      } else if (constructor != null) {
         return constructor;
      } else {
         Class<?> concreteType = getConcreteType(cls, true);
         if (concreteType != null) {
            for (Class<?> c = concreteType; c != null && (c != Object.class || cls == Object.class); c = c.getSuperclass()) {
               try {
                  Constructor<?> constructorWithSizeHint = c.getDeclaredConstructor(int.class);
                  JSONUtils.makeAccessible(constructorWithSizeHint, this.reflectionUtils);
                  this.constructorForConcreteTypeWithSizeHint.put(cls, constructorWithSizeHint);
                  return constructorWithSizeHint;
               } catch (SecurityException | ReflectiveOperationException var6) {
               }
            }
         }

         this.constructorForConcreteTypeWithSizeHint.put(cls, NO_CONSTRUCTOR);
         return null;
      }
   }

   static {
      try {
         NO_CONSTRUCTOR = ClassFieldCache.NoConstructor.class.getDeclaredConstructor();
      } catch (SecurityException | NoSuchMethodException var1) {
         throw new RuntimeException("Could not find or access constructor for " + ClassFieldCache.NoConstructor.class.getName(), var1);
      }
   }

   private static class NoConstructor {
      public NoConstructor() {
      }
   }
}
