package com.sun.javafx.reflect;

import java.lang.reflect.Proxy;

public final class ReflectUtil {
   public static final String PROXY_PACKAGE = "com.sun.proxy";

   private ReflectUtil() {
   }

   public static void checkPackageAccess(Class<?> var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         privateCheckPackageAccess(var1, var0);
      }
   }

   private static void privateCheckPackageAccess(SecurityManager var0, Class<?> var1) {
      while (var1.isArray()) {
         var1 = var1.getComponentType();
      }

      String var2 = var1.getPackageName();
      if (var2 != null && !var2.isEmpty()) {
         var0.checkPackageAccess(var2);
      }

      if (isNonPublicProxyClass(var1)) {
         privateCheckProxyPackageAccess(var0, var1);
      }
   }

   public static void checkPackageAccess(String var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         String var2 = var0.replace('/', '.');
         if (var2.startsWith("[")) {
            int var3 = var2.lastIndexOf(91) + 2;
            if (var3 > 1 && var3 < var2.length()) {
               var2 = var2.substring(var3);
            }
         }

         int var4 = var2.lastIndexOf(46);
         if (var4 != -1) {
            var1.checkPackageAccess(var2.substring(0, var4));
         }
      }
   }

   public static boolean isPackageAccessible(Class<?> var0) {
      try {
         checkPackageAccess(var0);
         return true;
      } catch (SecurityException var2) {
         return false;
      }
   }

   private static void privateCheckProxyPackageAccess(SecurityManager var0, Class<?> var1) {
      if (Proxy.isProxyClass(var1)) {
         for (Class var5 : var1.getInterfaces()) {
            privateCheckPackageAccess(var0, var5);
         }
      }
   }

   public static boolean isNonPublicProxyClass(Class<?> var0) {
      if (!Proxy.isProxyClass(var0)) {
         return false;
      } else {
         String var1 = var0.getPackageName();
         return var1 == null || !var1.startsWith("com.sun.proxy");
      }
   }
}
