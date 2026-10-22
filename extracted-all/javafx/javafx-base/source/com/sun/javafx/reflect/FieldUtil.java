package com.sun.javafx.reflect;

import java.lang.reflect.Field;

public final class FieldUtil {
   private FieldUtil() {
   }

   public static Field getField(Class<?> var0, String var1) throws NoSuchFieldException {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getField(var1);
   }
}
