package org.glassfish.jersey.internal.guava;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Primitives {
   private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;

   private Primitives() {
   }

   private static void add(Map<Class<?>, Class<?>> forward, Class<?> key, Class<?> value) {
      forward.put(key, value);
   }

   public static <T> Class<T> wrap(Class<T> type) {
      Preconditions.checkNotNull(type);
      Class<T> wrapped = (Class<T>)PRIMITIVE_TO_WRAPPER_TYPE.get(type);
      return wrapped == null ? type : wrapped;
   }

   static {
      Map<Class<?>, Class<?>> primToWrap = new HashMap<>(16);
      add(primToWrap, boolean.class, Boolean.class);
      add(primToWrap, byte.class, Byte.class);
      add(primToWrap, char.class, Character.class);
      add(primToWrap, double.class, Double.class);
      add(primToWrap, float.class, Float.class);
      add(primToWrap, int.class, Integer.class);
      add(primToWrap, long.class, Long.class);
      add(primToWrap, short.class, Short.class);
      add(primToWrap, void.class, Void.class);
      PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
   }
}
