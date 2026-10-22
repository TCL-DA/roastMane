package org.eclipse.yasson.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;

public class InstanceCreator {
   private static final Map<Class<?>, Supplier<?>> CREATORS = new HashMap<>();

   private InstanceCreator() {
      throw new IllegalStateException("This class should never be instantiated");
   }

   public static <T> T createInstance(Class<T> tClass) {
      Supplier<T> creator = (Supplier<T>)CREATORS.get(tClass);
      if (creator == null) {
         Constructor<T> constructor = ReflectionUtils.getDefaultConstructor(tClass, true);
         creator = () -> ReflectionUtils.createNoArgConstructorInstance(constructor);
         CREATORS.put(tClass, creator);
      }

      return creator.get();
   }

   static {
      CREATORS.put(ArrayList.class, ArrayList::new);
      CREATORS.put(LinkedList.class, LinkedList::new);
      CREATORS.put(HashSet.class, HashSet::new);
      CREATORS.put(TreeSet.class, TreeSet::new);
      CREATORS.put(HashMap.class, HashMap::new);
      CREATORS.put(TreeMap.class, TreeMap::new);
   }
}
