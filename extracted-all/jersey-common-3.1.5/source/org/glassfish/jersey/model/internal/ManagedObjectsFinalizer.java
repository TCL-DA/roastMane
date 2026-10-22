package org.glassfish.jersey.model.internal;

import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.internal.inject.InjectionManager;

@Singleton
public class ManagedObjectsFinalizer {
   private final InjectionManager injectionManager;
   private final Set<Object> managedObjects = new HashSet<>();

   public ManagedObjectsFinalizer(InjectionManager injectionManager) {
      this.injectionManager = injectionManager;
   }

   public void registerForPreDestroyCall(Object object) {
      this.managedObjects.add(object);
   }

   @PreDestroy
   public void preDestroy() {
      try {
         for (Object o : this.managedObjects) {
            this.injectionManager.preDestroy(o);
         }
      } finally {
         this.managedObjects.clear();
      }
   }
}
