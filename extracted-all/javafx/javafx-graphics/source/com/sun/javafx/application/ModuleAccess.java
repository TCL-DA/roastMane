package com.sun.javafx.application;

import java.lang.module.ModuleDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public final class ModuleAccess {
   private final Object module;
   private static final Method bootLayerMethod;
   private static final Method getModuleNameMethod;
   private static final Method findModuleMethod;
   private static final Method getDescriptorMethod;
   private static final Method classForNameMethod;

   private ModuleAccess(Object var1) {
      this.module = var1;
   }

   ModuleDescriptor getDescriptor() {
      try {
         return (ModuleDescriptor)getDescriptorMethod.invoke(this.module);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         throw new InternalError(var2);
      }
   }

   String getName() {
      try {
         return (String)getModuleNameMethod.invoke(this.module);
      } catch (InvocationTargetException | IllegalAccessException var2) {
         throw new InternalError(var2);
      }
   }

   Class<?> classForName(String var1) {
      try {
         return (Class<?>)classForNameMethod.invoke(null, this.module, var1);
      } catch (InvocationTargetException | IllegalAccessException var3) {
         throw new InternalError(var3);
      }
   }

   static ModuleAccess load(String var0) {
      try {
         Object var1 = bootLayerMethod.invoke(null);
         Optional var2 = (Optional)findModuleMethod.invoke(var1, var0);
         if (!var2.isPresent()) {
            throw new InternalError("Module " + var0 + " not in boot Layer");
         } else {
            return new ModuleAccess(var2.get());
         }
      } catch (InvocationTargetException | IllegalAccessException var3) {
         throw new InternalError(var3);
      }
   }

   static {
      Class var0 = null;
      Method var1 = null;
      Method var2 = null;
      Object var3 = null;
      Object var4 = null;
      Object var5 = null;
      Object var6 = null;
      Object var7 = null;

      try {
         var1 = Class.class.getMethod("getModule");
         var0 = var1.getReturnType();
         var2 = var0.getMethod("getLayer");
         var3 = var0.getMethod("getDescriptor");
         var4 = var0.getMethod("getName");
         Class var8 = var2.getReturnType();
         var5 = var8.getMethod("boot");
         var6 = var8.getMethod("findModule", String.class);
         var7 = Class.class.getMethod("forName", var0, String.class);
      } catch (NoSuchMethodException var9) {
         throw new InternalError("Module reflection failed", var9);
      }

      bootLayerMethod = (Method)var5;
      getModuleNameMethod = (Method)var4;
      getDescriptorMethod = (Method)var3;
      findModuleMethod = (Method)var6;
      classForNameMethod = (Method)var7;
   }
}
