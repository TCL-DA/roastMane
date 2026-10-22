package com.sun.javafx.tk;

import com.sun.javafx.FXPermissions;
import java.lang.reflect.Constructor;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.Permission;

public class PermissionHelper {
   private static boolean awtInitialized = false;
   private static Permission awtClipboardPermission;

   private static synchronized Permission getAWTClipboardPermission() {
      if (!awtInitialized) {
         try {
            Class var0 = Class.forName("java.awt.AWTPermission", false, PermissionHelper.class.getClassLoader());
            Constructor var1 = var0.getConstructor(String.class);
            awtClipboardPermission = (Permission)var1.newInstance("accessClipboard");
         } catch (Exception var2) {
            awtClipboardPermission = null;
         }

         awtInitialized = true;
      }

      return awtClipboardPermission;
   }

   public static void checkClipboardPermission() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         try {
            var0.checkPermission(FXPermissions.ACCESS_CLIPBOARD_PERMISSION);
         } catch (SecurityException var5) {
            Permission var2 = getAWTClipboardPermission();
            if (var2 == null) {
               throw var5;
            }

            try {
               var0.checkPermission(var2);
            } catch (SecurityException var4) {
               throw var5;
            }
         }
      }
   }

   public static void checkClipboardPermission(AccessControlContext var0) {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         if (var0 == null) {
            throw new AccessControlException("AccessControlContext is null");
         } else {
            try {
               var1.checkPermission(FXPermissions.ACCESS_CLIPBOARD_PERMISSION, var0);
            } catch (SecurityException var6) {
               Permission var3 = getAWTClipboardPermission();
               if (var3 == null) {
                  throw var6;
               }

               try {
                  var1.checkPermission(var3, var0);
               } catch (SecurityException var5) {
                  throw var6;
               }
            }
         }
      }
   }

   private PermissionHelper() {
   }
}
