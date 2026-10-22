package com.sun.javafx.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class MethodUtil extends SecureClassLoader {
   private static final String MISC_PKG = "com.sun.javafx.reflect.";
   private static final String TRAMPOLINE = "com.sun.javafx.reflect.Trampoline";
   private static final Method bounce = getTrampoline();

   private MethodUtil() {
   }

   public static Method getMethod(Class<?> var0, String var1, Class<?>[] var2) throws NoSuchMethodException {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getMethod(var1, var2);
   }

   public static Method[] getMethods(Class<?> var0) {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getMethods();
   }

   static Method[] getPublicMethods(Class<?> var0) {
      if (System.getSecurityManager() == null) {
         return var0.getMethods();
      } else {
         HashMap var1 = new HashMap();

         while (var0 != null) {
            boolean var2 = getInternalPublicMethods(var0, var1);
            if (var2) {
               break;
            }

            getInterfaceMethods(var0, var1);
            var0 = var0.getSuperclass();
         }

         return var1.values().toArray(new Method[var1.size()]);
      }
   }

   private static void getInterfaceMethods(Class<?> var0, Map<MethodUtil.Signature, Method> var1) {
      Class[] var2 = var0.getInterfaces();

      for (int var3 = 0; var3 < var2.length; var3++) {
         Class var4 = var2[var3];
         boolean var5 = getInternalPublicMethods(var4, var1);
         if (!var5) {
            getInterfaceMethods(var4, var1);
         }
      }
   }

   private static boolean getInternalPublicMethods(Class<?> var0, Map<MethodUtil.Signature, Method> var1) {
      Object var2 = null;

      try {
         if (!Modifier.isPublic(var0.getModifiers())) {
            return false;
         }

         if (!ReflectUtil.isPackageAccessible(var0)) {
            return false;
         }

         var2 = var0.getMethods();
      } catch (SecurityException var6) {
         return false;
      }

      boolean var3 = true;

      for (int var4 = 0; var4 < ((Object[])var2).length; var4++) {
         Class var5 = ((Object[])var2)[var4].getDeclaringClass();
         if (!Modifier.isPublic(var5.getModifiers())) {
            var3 = false;
            break;
         }
      }

      if (var3) {
         for (int var8 = 0; var8 < ((Object[])var2).length; var8++) {
            addMethod(var1, (Method)((Object[])var2)[var8]);
         }
      } else {
         for (int var9 = 0; var9 < ((Object[])var2).length; var9++) {
            Class var10 = ((Object[])var2)[var9].getDeclaringClass();
            if (var0.equals(var10)) {
               addMethod(var1, (Method)((Object[])var2)[var9]);
            }
         }
      }

      return var3;
   }

   private static void addMethod(Map<MethodUtil.Signature, Method> var0, Method var1) {
      MethodUtil.Signature var2 = new MethodUtil.Signature(var1);
      if (!var0.containsKey(var2)) {
         var0.put(var2, var1);
      } else if (!var1.getDeclaringClass().isInterface()) {
         Method var3 = (Method)var0.get(var2);
         if (var3.getDeclaringClass().isInterface()) {
            var0.put(var2, var1);
         }
      }
   }

   public static Module getTrampolineModule() {
      return bounce.getDeclaringClass().getModule();
   }

   public static Object invoke(Method var0, Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException {
      try {
         return bounce.invoke(null, var0, var1, var2);
      } catch (InvocationTargetException var5) {
         Throwable var4 = var5.getCause();
         if (var4 instanceof InvocationTargetException) {
            throw (InvocationTargetException)var4;
         } else if (var4 instanceof IllegalAccessException) {
            throw (IllegalAccessException)var4;
         } else if (var4 instanceof RuntimeException) {
            throw (RuntimeException)var4;
         } else if (var4 instanceof Error) {
            throw (Error)var4;
         } else {
            throw new Error("Unexpected invocation error", var4);
         }
      } catch (IllegalAccessException var6) {
         throw new Error("Unexpected invocation error", var6);
      }
   }

   private static Method getTrampoline() {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               Class var1 = MethodUtil.getTrampolineClass();
               Class[] var2 = new Class[]{Method.class, Object.class, Object[].class};
               Method var3 = var1.getDeclaredMethod("invoke", var2);
               var3.setAccessible(true);
               return var3;
            }
         });
      } catch (Exception var1) {
         throw new InternalError("bouncer cannot be found", var1);
      }
   }

   @Override
   protected synchronized Class<?> loadClass(String var1, boolean var2) throws ClassNotFoundException {
      ReflectUtil.checkPackageAccess(var1);
      Class var3 = this.findLoadedClass(var1);
      if (var3 == null) {
         try {
            var3 = this.findClass(var1);
         } catch (ClassNotFoundException var5) {
         }

         if (var3 == null) {
            var3 = this.getParent().loadClass(var1);
         }
      }

      if (var2) {
         this.resolveClass(var3);
      }

      return var3;
   }

   @Override
   protected Class<?> findClass(String var1) throws ClassNotFoundException {
      if (!var1.startsWith("com.sun.javafx.reflect.")) {
         throw new ClassNotFoundException(var1);
      } else {
         String var2 = var1.replace('.', '/').concat(".class");

         try {
            InputStream var3 = MethodUtil.class.getModule().getResourceAsStream(var2);
            if (var3 != null) {
               InputStream var4 = var3;

               Class var6;
               try {
                  byte[] var5 = var3.readAllBytes();
                  var6 = this.defineClass(var1, var5);
               } catch (Throwable var8) {
                  if (var3 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (var3 != null) {
                  var3.close();
               }

               return var6;
            }
         } catch (IOException var9) {
            throw new ClassNotFoundException(var1, var9);
         }

         throw new ClassNotFoundException(var1);
      }
   }

   private Class<?> defineClass(String var1, byte[] var2) throws IOException {
      CodeSource var3 = new CodeSource(null, (Certificate[])null);
      if (!var1.equals("com.sun.javafx.reflect.Trampoline")) {
         throw new IOException("MethodUtil: bad name " + var1);
      } else {
         return this.defineClass(var1, var2, 0, var2.length, var3);
      }
   }

   @Override
   protected PermissionCollection getPermissions(CodeSource var1) {
      PermissionCollection var2 = super.getPermissions(var1);
      var2.add(new AllPermission());
      return var2;
   }

   private static Class<?> getTrampolineClass() {
      try {
         return Class.forName("com.sun.javafx.reflect.Trampoline", true, new MethodUtil());
      } catch (ClassNotFoundException var1) {
         return null;
      }
   }

   private static class Signature {
      private final String methodName;
      private final Class<?>[] argClasses;
      private final int hashCode;

      Signature(Method var1) {
         this.methodName = var1.getName();
         this.argClasses = var1.getParameterTypes();
         this.hashCode = this.methodName.hashCode() + Arrays.hashCode((Object[])this.argClasses);
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            MethodUtil.Signature var2 = (MethodUtil.Signature)var1;
            if (!this.methodName.equals(var2.methodName)) {
               return false;
            } else if (this.argClasses.length != var2.argClasses.length) {
               return false;
            } else {
               for (int var3 = 0; var3 < this.argClasses.length; var3++) {
                  if (this.argClasses[var3] != var2.argClasses[var3]) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }
}
