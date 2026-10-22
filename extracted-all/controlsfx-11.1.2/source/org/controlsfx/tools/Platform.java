package org.controlsfx.tools;

import java.security.AccessController;
import java.security.PrivilegedAction;

public enum Platform {
   WINDOWS("windows"),
   OSX("mac"),
   UNIX("unix"),
   UNKNOWN("");

   private static Platform current = getCurrentPlatform();
   private String platformId;

   private Platform(String platformId) {
      this.platformId = platformId;
   }

   public String getPlatformId() {
      return this.platformId;
   }

   public static Platform getCurrent() {
      return current;
   }

   private static Platform getCurrentPlatform() {
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Windows")) {
         return WINDOWS;
      } else if (osName.startsWith("Mac")) {
         return OSX;
      } else if (osName.startsWith("SunOS")) {
         return UNIX;
      } else {
         if (osName.startsWith("Linux")) {
            String javafxPlatform = AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  return System.getProperty("javafx.platform");
               }
            });
            if (!"android".equals(javafxPlatform) && !"Dalvik".equals(System.getProperty("java.vm.name"))) {
               return UNIX;
            }
         }

         return UNKNOWN;
      }
   }
}
