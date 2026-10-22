package org.eclipse.jetty.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeprecationWarning implements Decorator {
   private static final Logger LOG = LoggerFactory.getLogger(DeprecationWarning.class);

   @Override
   public <T> T decorate(T o) {
      if (o == null) {
         return null;
      } else {
         Class<?> clazz = o.getClass();

         try {
            Deprecated depr = clazz.getAnnotation(Deprecated.class);
            if (depr != null) {
               LOG.warn("Using @Deprecated Class {}", clazz.getName());
            }
         } catch (Throwable var7) {
            LOG.trace("IGNORED", var7);
         }

         this.verifyIndirectTypes(clazz.getSuperclass(), clazz, "Class");

         for (Class<?> ifaceClazz : clazz.getInterfaces()) {
            this.verifyIndirectTypes(ifaceClazz, clazz, "Interface");
         }

         return o;
      }
   }

   private void verifyIndirectTypes(Class<?> superClazz, Class<?> clazz, String typeName) {
      try {
         while (superClazz != null && superClazz != Object.class) {
            Deprecated supDepr = superClazz.getAnnotation(Deprecated.class);
            if (supDepr != null) {
               LOG.warn("Using indirect @Deprecated {} {} - (seen from {})", new Object[]{typeName, superClazz.getName(), clazz});
            }

            superClazz = superClazz.getSuperclass();
         }
      } catch (Throwable var5) {
         LOG.trace("IGNORED", var5);
      }
   }

   @Override
   public void destroy(Object o) {
   }
}
