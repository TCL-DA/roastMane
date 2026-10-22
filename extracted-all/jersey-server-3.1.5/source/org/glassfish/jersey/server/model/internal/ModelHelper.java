package org.glassfish.jersey.server.model.internal;

import jakarta.ws.rs.Path;

public final class ModelHelper {
   public static Class<?> getAnnotatedResourceClass(Class<?> resourceClass) {
      Class<?> foundInterface = null;
      Class<?> cls = resourceClass;

      while (!cls.isAnnotationPresent(Path.class)) {
         if (foundInterface == null) {
            for (Class<?> i : cls.getInterfaces()) {
               if (i.isAnnotationPresent(Path.class)) {
                  foundInterface = i;
                  break;
               }
            }
         }

         if ((cls = cls.getSuperclass()) == null) {
            if (foundInterface != null) {
               return foundInterface;
            }

            return resourceClass;
         }
      }

      return cls;
   }

   private ModelHelper() {
      throw new AssertionError("Instantiation not allowed.");
   }
}
