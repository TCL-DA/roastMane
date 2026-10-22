package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class CleanerJava9 implements Cleaner {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
   private static final Method INVOKE_CLEANER;

   static boolean isSupported() {
      return INVOKE_CLEANER != null;
   }

   @Override
   public void freeDirectBuffer(ByteBuffer buffer) {
      if (System.getSecurityManager() == null) {
         try {
            INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, buffer);
         } catch (Throwable var3) {
            PlatformDependent0.throwException(var3);
         }
      } else {
         freeDirectBufferPrivileged(buffer);
      }
   }

   private static void freeDirectBufferPrivileged(final ByteBuffer buffer) {
      Exception error = AccessController.doPrivileged(new PrivilegedAction<Exception>() {
         public Exception run() {
            try {
               CleanerJava9.INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, buffer);
               return null;
            } catch (InvocationTargetException var2) {
               return var2;
            } catch (IllegalAccessException var3) {
               return var3;
            }
         }
      });
      if (error != null) {
         PlatformDependent0.throwException(error);
      }
   }

   static {
      Method method;
      Throwable error;
      if (PlatformDependent0.hasUnsafe()) {
         final ByteBuffer buffer = ByteBuffer.allocateDirect(1);
         Object maybeInvokeMethod = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               try {
                  Method m = PlatformDependent0.UNSAFE.getClass().getDeclaredMethod("invokeCleaner", ByteBuffer.class);
                  m.invoke(PlatformDependent0.UNSAFE, buffer);
                  return m;
               } catch (NoSuchMethodException var2) {
                  return var2;
               } catch (InvocationTargetException var3) {
                  return var3;
               } catch (IllegalAccessException var4) {
                  return var4;
               }
            }
         });
         if (maybeInvokeMethod instanceof Throwable) {
            method = null;
            error = (Throwable)maybeInvokeMethod;
         } else {
            method = (Method)maybeInvokeMethod;
            error = null;
         }
      } else {
         method = null;
         error = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
      }

      if (error == null) {
         logger.debug("java.nio.ByteBuffer.cleaner(): available");
      } else {
         logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
      }

      INVOKE_CLEANER = method;
   }
}
