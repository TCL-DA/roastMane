package org.apache.logging.log4j.core.pattern;

import java.net.URL;
import java.security.CodeSource;
import java.util.function.Consumer;

final class ClassResourceInfo {
   static final ClassResourceInfo UNKNOWN = new ClassResourceInfo();
   private final Consumer<StringBuilder> renderer;
   final Class<?> clazz;

   private ClassResourceInfo() {
      this.renderer = buffer -> buffer.append("~[?:?]");
      this.clazz = null;
   }

   ClassResourceInfo(final Class<?> clazz, final boolean exact) {
      String exactnessPrefix = exact ? "" : "~";
      String location = getLocation(clazz);
      String version = getVersion(clazz);
      this.renderer = buffer -> {
         buffer.append(exactnessPrefix);
         buffer.append("[");
         buffer.append(location);
         buffer.append(":");
         buffer.append(version);
         buffer.append("]");
      };
      this.clazz = clazz;
   }

   private static String getLocation(final Class<?> clazz) {
      try {
         CodeSource source = clazz.getProtectionDomain().getCodeSource();
         if (source != null) {
            URL locationUrl = source.getLocation();
            if (locationUrl != null) {
               String normalizedLocationUrl = locationUrl.toString().replace('\\', '/');
               int separatorIndex = normalizedLocationUrl.lastIndexOf("/");
               if (separatorIndex >= 0 && separatorIndex == normalizedLocationUrl.length() - 1) {
                  separatorIndex = normalizedLocationUrl.lastIndexOf("/", separatorIndex - 1);
               }

               return normalizedLocationUrl.substring(separatorIndex + 1);
            }
         }
      } catch (Exception var5) {
      }

      return "?";
   }

   private static String getVersion(final Class<?> clazz) {
      Package classPackage = clazz.getPackage();
      if (classPackage != null) {
         String version = classPackage.getImplementationVersion();
         if (version != null) {
            return version;
         }
      }

      return "?";
   }

   void render(final StringBuilder buffer) {
      this.renderer.accept(buffer);
   }
}
