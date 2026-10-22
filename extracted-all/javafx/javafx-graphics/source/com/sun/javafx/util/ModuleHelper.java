package com.sun.javafx.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ModuleHelper {
   private static final Method getModuleMethod;
   private static final Method addReadsMethod;
   private static final Method addExportsMethod;
   private static final boolean verbose;

   public static Object getModule(Class var0) {
      if (getModuleMethod != null) {
         try {
            return getModuleMethod.invoke(var0);
         } catch (InvocationTargetException | IllegalAccessException var2) {
            throw new RuntimeException(var2);
         }
      } else {
         return null;
      }
   }

   public static void addReads(Object var0, Object var1) {
      if (addReadsMethod != null) {
         try {
            addReadsMethod.invoke(var0, var1);
         } catch (InvocationTargetException | IllegalAccessException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public static void addExports(Object var0, String var1, Object var2) {
      if (addExportsMethod != null) {
         try {
            addExportsMethod.invoke(var0, var1, var2);
         } catch (InvocationTargetException | IllegalAccessException var4) {
            throw new RuntimeException(var4);
         }
      }
   }

   static {
      boolean var0 = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.verbose")));
      verbose = var0;
      if (verbose) {
         System.err.println(ModuleHelper.class.getName() + " : <clinit>");
      }

      Method var1 = null;
      Method var2 = null;
      Method var3 = null;

      try {
         var1 = Class.class.getMethod("getModule");
         Class var4 = var1.getReturnType();
         var2 = var4.getMethod("addReads", var4);
         var3 = var4.getMethod("addExports", String.class, var4);
      } catch (NoSuchMethodException var5) {
      }

      getModuleMethod = var1;
      addReadsMethod = var2;
      addExportsMethod = var3;
      if (verbose) {
         System.err.println("getModuleMethod = " + getModuleMethod);
         System.err.println("addReadsMethod = " + addReadsMethod);
         System.err.println("addExportsMethod = " + addExportsMethod);
      }
   }
}
