package com.sun.javafx.fxml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class ModuleHelper {
   private static final Method getModuleMethod;
   private static final Method getResourceAsStreamMethod;
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

   public static InputStream getResourceAsStream(Object var0, String var1) {
      if (getResourceAsStreamMethod != null) {
         try {
            return (InputStream)getResourceAsStreamMethod.invoke(var0, var1);
         } catch (InvocationTargetException | IllegalAccessException var3) {
            throw new RuntimeException(var3);
         }
      } else {
         return null;
      }
   }

   public static Object invoke(Method var0, Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException {
      Object var3 = getModule(ModuleHelper.class);
      Object var4 = getModule(var0.getDeclaringClass());
      if (verbose) {
         System.out.println("thisModule = " + var3);
         System.out.println("methodModule = " + var4);
         System.out.println("m = " + var0);
      }

      return var4 != var3 ? MethodHelper.invoke(var0, var1, var2) : var0.invoke(var1, var2);
   }

   static {
      boolean var0 = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.verbose")));
      verbose = var0;
      if (verbose) {
         System.err.println(ModuleHelper.class.getName() + " : <clinit>");
      }

      Method var1 = null;
      Method var2 = null;

      try {
         var1 = Class.class.getMethod("getModule");
         Class var3 = var1.getReturnType();
         var2 = var3.getMethod("getResourceAsStream", String.class);
      } catch (NoSuchMethodException var4) {
      }

      getModuleMethod = var1;
      getResourceAsStreamMethod = var2;
      if (verbose) {
         System.err.println("getModuleMethod = " + getModuleMethod);
         System.err.println("getResourceAsStreamMethod = " + getResourceAsStreamMethod);
      }
   }
}
