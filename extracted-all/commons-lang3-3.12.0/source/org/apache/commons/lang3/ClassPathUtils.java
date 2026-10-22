package org.apache.commons.lang3;

public class ClassPathUtils {
   public static String toFullyQualifiedName(Class<?> context, String resourceName) {
      Validate.notNull(context, "context");
      Validate.notNull(resourceName, "resourceName");
      return toFullyQualifiedName(context.getPackage(), resourceName);
   }

   public static String toFullyQualifiedName(Package context, String resourceName) {
      Validate.notNull(context, "context");
      Validate.notNull(resourceName, "resourceName");
      return context.getName() + "." + resourceName;
   }

   public static String toFullyQualifiedPath(Class<?> context, String resourceName) {
      Validate.notNull(context, "context");
      Validate.notNull(resourceName, "resourceName");
      return toFullyQualifiedPath(context.getPackage(), resourceName);
   }

   public static String toFullyQualifiedPath(Package context, String resourceName) {
      Validate.notNull(context, "context");
      Validate.notNull(resourceName, "resourceName");
      return context.getName().replace('.', '/') + "/" + resourceName;
   }
}
