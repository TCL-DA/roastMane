package io.netty.util.internal;

import java.lang.reflect.AccessibleObject;

public final class ReflectionUtil {
   private ReflectionUtil() {
   }

   public static Throwable trySetAccessible(AccessibleObject object, boolean checkAccessible) {
      if (checkAccessible && !PlatformDependent0.isExplicitTryReflectionSetAccessible()) {
         return new UnsupportedOperationException("Reflective setAccessible(true) disabled");
      } else {
         try {
            object.setAccessible(true);
            return null;
         } catch (SecurityException var3) {
            return var3;
         } catch (RuntimeException var4) {
            return handleInaccessibleObjectException(var4);
         }
      }
   }

   private static RuntimeException handleInaccessibleObjectException(RuntimeException e) {
      if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
         return e;
      } else {
         throw e;
      }
   }
}
