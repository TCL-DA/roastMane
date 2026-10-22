package org.glassfish.jersey.internal.util.collection;

import java.lang.reflect.Type;

public final class ClassTypePair {
   private final Type type;
   private final Class<?> rawClass;

   private ClassTypePair(Class<?> c, Type t) {
      this.type = t;
      this.rawClass = c;
   }

   public Class<?> rawClass() {
      return this.rawClass;
   }

   public Type type() {
      return this.type;
   }

   public static ClassTypePair of(Class<?> rawClass) {
      return new ClassTypePair(rawClass, rawClass);
   }

   public static ClassTypePair of(Class<?> rawClass, Type type) {
      return new ClassTypePair(rawClass, type);
   }
}
