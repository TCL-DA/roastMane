package org.glassfish.jersey.internal.inject;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class PrimitiveMapper {
   public static final Map<Class, Class> primitiveToClassMap = getPrimitiveToClassMap();
   public static final Map<Class, Object> primitiveToDefaultValueMap = getPrimitiveToDefaultValueMap();

   private static Map<Class, Class> getPrimitiveToClassMap() {
      Map<Class, Class> m = new WeakHashMap<>();
      m.put(boolean.class, Boolean.class);
      m.put(byte.class, Byte.class);
      m.put(char.class, Character.class);
      m.put(short.class, Short.class);
      m.put(int.class, Integer.class);
      m.put(long.class, Long.class);
      m.put(float.class, Float.class);
      m.put(double.class, Double.class);
      return Collections.unmodifiableMap(m);
   }

   private static Map<Class, Object> getPrimitiveToDefaultValueMap() {
      Map<Class, Object> m = new WeakHashMap<>();
      m.put(Boolean.class, false);
      m.put(Byte.class, (byte)0);
      m.put(Character.class, '\u0000');
      m.put(Short.class, (short)0);
      m.put(Integer.class, 0);
      m.put(Long.class, 0L);
      m.put(Float.class, 0.0F);
      m.put(Double.class, 0.0);
      return Collections.unmodifiableMap(m);
   }

   private PrimitiveMapper() {
   }
}
