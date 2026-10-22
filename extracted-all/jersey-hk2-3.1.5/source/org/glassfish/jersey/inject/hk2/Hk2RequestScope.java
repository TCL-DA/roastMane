package org.glassfish.jersey.inject.hk2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.jersey.internal.util.ExtendedLogger;
import org.glassfish.jersey.internal.util.LazyUid;
import org.glassfish.jersey.process.internal.RequestScope;

public class Hk2RequestScope extends RequestScope {
   public org.glassfish.jersey.process.internal.RequestContext createContext() {
      return new Hk2RequestScope.Instance();
   }

   public static final class Instance implements org.glassfish.jersey.process.internal.RequestContext {
      private static final ExtendedLogger logger = new ExtendedLogger(Logger.getLogger(Hk2RequestScope.Instance.class.getName()), Level.FINEST);
      private final LazyUid id = new LazyUid();
      private final Map<ForeignDescriptor, Object> store = new HashMap<>();
      private final AtomicInteger referenceCounter = new AtomicInteger(1);

      private Instance() {
      }

      public Hk2RequestScope.Instance getReference() {
         this.referenceCounter.incrementAndGet();
         return this;
      }

      public <T> T get(ForeignDescriptor descriptor) {
         return (T)this.store.get(descriptor);
      }

      public <T> T put(ForeignDescriptor descriptor, T value) {
         Preconditions.checkState(
            !this.store.containsKey(descriptor),
            "An instance for the descriptor %s was already seeded in this scope. Old instance: %s New instance: %s",
            new Object[]{descriptor, this.store.get(descriptor), value}
         );
         return (T)this.store.put(descriptor, value);
      }

      public <T> void remove(ForeignDescriptor descriptor) {
         T removed = (T)this.store.remove(descriptor);
         if (removed != null) {
            descriptor.dispose(removed);
         }
      }

      public boolean contains(ForeignDescriptor provider) {
         return this.store.containsKey(provider);
      }

      public void release() {
         if (this.referenceCounter.decrementAndGet() < 1) {
            try {
               new HashSet<>(this.store.keySet()).forEach(this::remove);
            } finally {
               logger.debugLog("Released scope instance {0}", new Object[]{this});
            }
         }
      }

      @Override
      public String toString() {
         return "Instance{id=" + this.id + ", referenceCounter=" + this.referenceCounter + ", store size=" + this.store.size() + '}';
      }
   }
}
