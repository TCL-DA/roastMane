package com.sun.javafx.fxml;

import com.sun.javafx.reflect.MethodUtil;
import com.sun.javafx.reflect.ReflectUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class MethodHelper {
   private static final boolean logAccessErrors = AccessController.doPrivileged(
      (PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("sun.reflect.debugModuleAccessChecks"))
   );
   private static final Module trampolineModule = MethodUtil.getTrampolineModule();

   public static Object invoke(Method var0, Object var1, Object[] var2) throws InvocationTargetException, IllegalAccessException {
      Class var3 = var0.getDeclaringClass();
      String var4 = var3.getPackage().getName();
      Module var5 = var3.getModule();
      Module var6 = MethodHelper.class.getModule();

      try {
         if (!var5.isExported(var4)) {
            if (!var5.isOpen(var4, var6)) {
               throw new IllegalAccessException(
                  "module "
                     + var6.getName()
                     + " cannot access class "
                     + var3.getName()
                     + " (in module "
                     + var5.getName()
                     + ") because module "
                     + var5.getName()
                     + " does not open "
                     + var4
                     + " to "
                     + var6.getName()
               );
            }

            if (!var5.isOpen(var4, trampolineModule)) {
               ReflectUtil.checkPackageAccess(var4);
               var5.addOpens(var4, trampolineModule);
            }
         }
      } catch (IllegalAccessException var8) {
         if (logAccessErrors) {
            var8.printStackTrace(System.err);
         }

         throw var8;
      }

      return MethodUtil.invoke(var0, var1, var2);
   }

   private MethodHelper() {
   }
}
