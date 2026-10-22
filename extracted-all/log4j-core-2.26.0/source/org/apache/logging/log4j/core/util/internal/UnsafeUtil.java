package org.apache.logging.log4j.core.util.internal;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public class UnsafeUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static Method cleanerMethod;
   private static Method cleanMethod;

   public static void clean(final ByteBuffer bb) throws Exception {
      if (cleanerMethod != null && cleanMethod != null && bb.isDirect()) {
         cleanMethod.invoke(cleanerMethod.invoke(bb));
      }
   }

   static {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws ReflectiveOperationException, SecurityException {
               ByteBuffer direct = ByteBuffer.allocateDirect(1);
               UnsafeUtil.cleanerMethod = direct.getClass().getDeclaredMethod("cleaner");
               UnsafeUtil.cleanerMethod.setAccessible(true);
               Object cleaner = UnsafeUtil.cleanerMethod.invoke(direct);
               UnsafeUtil.cleanMethod = cleaner.getClass().getMethod("clean");
               return null;
            }
         });
      } catch (PrivilegedActionException var2) {
         Exception wrapped = var2.getException();
         if (wrapped instanceof SecurityException) {
            throw (SecurityException)wrapped;
         }

         LOGGER.warn("sun.misc.Cleaner#clean() is not accessible. This will impact memory usage.", wrapped);
         cleanerMethod = null;
         cleanMethod = null;
      }
   }
}
