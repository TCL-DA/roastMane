package com.sun.webkit;

import com.sun.javafx.logging.PlatformLogger;
import java.util.Locale;
import java.util.ResourceBundle;

final class LocalizedStrings {
   private static final PlatformLogger log = PlatformLogger.getLogger(LocalizedStrings.class.getName());
   private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("com.sun.webkit.LocalizedStrings", Locale.getDefault());

   private LocalizedStrings() {
   }

   private static String getLocalizedProperty(String var0) {
      log.fine("Get property: " + var0);
      String var1 = BUNDLE.getString(var0);
      if (var1 != null && var1.trim().length() > 0) {
         log.fine("Property value: " + var1);
         return var1.trim();
      } else {
         log.fine("Unknown property value");
         return null;
      }
   }
}
