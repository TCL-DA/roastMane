package org.eclipse.jetty.util.security;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionException;
import javax.security.auth.Subject;

public class SecurityUtils {
   private static final MethodHandle doAs = lookupDoAs();
   private static final MethodHandle doPrivileged = lookupDoPrivileged();

   private static MethodHandle lookupDoAs() {
      Lookup lookup = MethodHandles.lookup();

      try {
         return lookup.findStatic(Subject.class, "callAs", MethodType.methodType(Object.class, Subject.class, Callable.class));
      } catch (Throwable var7) {
         try {
            MethodType oldSignature = MethodType.methodType(Object.class, Subject.class, PrivilegedAction.class);
            MethodHandle doAs = lookup.findStatic(Subject.class, "doAs", oldSignature);
            MethodType convertSignature = MethodType.methodType(PrivilegedAction.class, Callable.class);
            MethodHandle converter = lookup.findStatic(SecurityUtils.class, "callableToPrivilegedAction", convertSignature);
            return MethodHandles.filterArguments(doAs, 1, converter);
         } catch (Throwable var6) {
            return null;
         }
      }
   }

   private static MethodHandle lookupDoPrivileged() {
      try {
         Class<?> klass = ClassLoader.getPlatformClassLoader().loadClass("java.security.AccessController");
         Lookup lookup = MethodHandles.lookup();
         return lookup.findStatic(klass, "doPrivileged", MethodType.methodType(Object.class, PrivilegedAction.class));
      } catch (Throwable var2) {
         return null;
      }
   }

   public static Object getSecurityManager() {
      try {
         return System.class.getMethod("getSecurityManager").invoke(null);
      } catch (Throwable var1) {
         return null;
      }
   }

   public static void checkPermission(Permission permission) throws SecurityException {
      Object securityManager = getSecurityManager();
      if (securityManager != null) {
         try {
            securityManager.getClass().getMethod("checkPermission").invoke(securityManager, permission);
         } catch (SecurityException var3) {
            throw var3;
         } catch (Throwable var4) {
         }
      }
   }

   public static <T> T doPrivileged(PrivilegedAction<T> action) {
      MethodHandle methodHandle = doPrivileged;
      return methodHandle == null ? action.run() : doPrivileged(methodHandle, action);
   }

   private static <T> T doPrivileged(MethodHandle doPrivileged, PrivilegedAction<T> action) {
      try {
         return (T)(Object)doPrivileged.invoke((PrivilegedAction)action);
      } catch (Error | RuntimeException var3) {
         throw var3;
      } catch (Throwable var4) {
         throw new RuntimeException(var4);
      }
   }

   public static <T> T doAs(Subject subject, Callable<T> action) {
      try {
         MethodHandle methodHandle = doAs;
         return (T)(methodHandle == null ? action.call() : (Object)methodHandle.invoke((Subject)subject, (Callable)action));
      } catch (Error | RuntimeException var3) {
         throw var3;
      } catch (Throwable var4) {
         throw new CompletionException(var4);
      }
   }

   private static <T> PrivilegedAction<T> callableToPrivilegedAction(Callable<T> callable) {
      return () -> {
         try {
            return callable.call();
         } catch (RuntimeException var2) {
            throw var2;
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      };
   }

   private SecurityUtils() {
   }
}
