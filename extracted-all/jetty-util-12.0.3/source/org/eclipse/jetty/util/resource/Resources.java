package org.eclipse.jetty.util.resource;

public final class Resources {
   public static boolean exists(Resource resource) {
      return resource != null && resource.exists();
   }

   public static boolean isCombined(Resource resource) {
      return resource instanceof CombinedResource;
   }

   public static boolean missing(Resource resource) {
      return resource == null || !resource.exists();
   }

   public static boolean isDirectory(Resource resource) {
      return resource != null && resource.isDirectory();
   }

   public static boolean isReadable(Resource resource) {
      return resource != null && resource.isReadable();
   }

   public static boolean isReadableDirectory(Resource resource) {
      return resource != null && resource.isDirectory() && resource.isReadable();
   }

   public static boolean isReadableFile(Resource resource) {
      return resource != null && !resource.isDirectory() && resource.isReadable();
   }
}
