package org.apache.logging.log4j.core.util.internal;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import sun.misc.Unsafe;

public class UnsafeUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Unsafe unsafe = findUnsafe();

   private static Unsafe findUnsafe() {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
            public Unsafe run() throws ReflectiveOperationException, SecurityException {
               Field unsafeField = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
               unsafeField.setAccessible(true);
               return (Unsafe)unsafeField.get(null);
            }
         });
      } catch (PrivilegedActionException var2) {
         Exception wrapped = var2.getException();
         if (wrapped instanceof SecurityException) {
            throw (SecurityException)wrapped;
         } else {
            LOGGER.warn("sun.misc.Unsafe is not available. This will impact memory usage.", var2);
            return null;
         }
      }
   }

   public static void clean(final ByteBuffer bb) throws Exception {
      if (unsafe != null && bb.isDirect()) {
         unsafe.invokeCleaner(bb);
      }
   }
}
