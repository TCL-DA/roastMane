package org.glassfish.hk2.utilities.reflection;

import java.util.HashMap;

public class Constants {
   public static final String SYSTEM_LOADER_NAME = "SystemLoader";
   public static final HashMap<Class<?>, Class<?>> PRIMITIVE_MAP = new HashMap<>();

   static {
      PRIMITIVE_MAP.put(char.class, Character.class);
      PRIMITIVE_MAP.put(byte.class, Byte.class);
      PRIMITIVE_MAP.put(short.class, Short.class);
      PRIMITIVE_MAP.put(int.class, Integer.class);
      PRIMITIVE_MAP.put(long.class, Long.class);
      PRIMITIVE_MAP.put(float.class, Float.class);
      PRIMITIVE_MAP.put(double.class, Double.class);
   }
}
