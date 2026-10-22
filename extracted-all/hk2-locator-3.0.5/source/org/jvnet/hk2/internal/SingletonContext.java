package org.jvnet.hk2.internal;

import jakarta.inject.Singleton;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ContextualInput;
import org.glassfish.hk2.utilities.cache.Cache;
import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.cache.Cache.CycleHandler;
import org.glassfish.hk2.utilities.reflection.Logger;

@Singleton
public class SingletonContext implements Context<Singleton> {
   private int generationNumber = Integer.MIN_VALUE;
   private final ServiceLocatorImpl locator;
   private final Cache<ContextualInput<Object>, Object> valueCache = new Cache(
      new Computable<ContextualInput<Object>, Object>() {
         public Object compute(ContextualInput<Object> a) {
            ActiveDescriptor<Object> activeDescriptor = a.getDescriptor();
            Object cachedVal = activeDescriptor.getCache();
            if (cachedVal != null) {
               return cachedVal;
            } else {
               Object createdVal = activeDescriptor.create(a.getRoot());
               activeDescriptor.setCache(createdVal);
               if (activeDescriptor instanceof SystemDescriptor) {
                  ((SystemDescriptor)activeDescriptor).setSingletonGeneration(SingletonContext.this.generationNumber++);
               }

               return createdVal;
            }
         }
      },
      new CycleHandler<ContextualInput<Object>>() {
         public void handleCycle(ContextualInput<Object> key) {
            throw new MultiException(
               new IllegalStateException(
                  "A circular dependency involving Singleton service "
                     + key.getDescriptor().getImplementation()
                     + " was found.  Full descriptor is "
                     + key.getDescriptor()
               )
            );
         }
      }
   );

   SingletonContext(ServiceLocatorImpl impl) {
      this.locator = impl;
   }

   public Class<? extends Annotation> getScope() {
      return Singleton.class;
   }

   public <T> T findOrCreate(ActiveDescriptor<T> activeDescriptor, ServiceHandle<?> root) {
      try {
         return (T)this.valueCache.compute(new ContextualInput(activeDescriptor, root));
      } catch (Throwable var4) {
         if (var4 instanceof MultiException) {
            throw (MultiException)var4;
         } else {
            throw new MultiException(var4);
         }
      }
   }

   public boolean containsKey(ActiveDescriptor<?> descriptor) {
      return this.valueCache.containsKey(new ContextualInput(descriptor, null));
   }

   public boolean isActive() {
      return true;
   }

   public boolean supportsNullCreation() {
      return false;
   }

   public void shutdown() {
      List<ActiveDescriptor<?>> all = this.locator.getDescriptors(BuilderHelper.allFilter());
      long myLocatorId = this.locator.getLocatorId();
      TreeSet<SystemDescriptor<Object>> singlesOnly = new TreeSet<>(new SingletonContext.GenerationComparator());

      for (ActiveDescriptor<?> one : all) {
         if (one.getScope() != null && one.getScope().equals(Singleton.class.getName())) {
            synchronized (this) {
               if (one.getCache() == null) {
                  continue;
               }
            }

            if (one.getLocatorId() != null && one.getLocatorId() == myLocatorId) {
               SystemDescriptor<Object> oneAsObject = (SystemDescriptor<Object>)one;
               singlesOnly.add(oneAsObject);
            }
         }
      }

      for (SystemDescriptor<Object> onex : singlesOnly) {
         this.destroyOne(onex);
      }
   }

   public void destroyOne(ActiveDescriptor<?> one) {
      this.valueCache.remove(new ContextualInput(one, null));
      Object value = one.getCache();
      one.releaseCache();
      if (value != null) {
         try {
            one.dispose(value);
         } catch (Throwable var4) {
            Logger.getLogger().debug("SingletonContext", "releaseOne", var4);
         }
      }
   }

   private static class GenerationComparator implements Comparator<SystemDescriptor<Object>>, Serializable {
      private static final long serialVersionUID = -6931828935035131179L;

      public int compare(SystemDescriptor<Object> o1, SystemDescriptor<Object> o2) {
         if (o1.getSingletonGeneration() > o2.getSingletonGeneration()) {
            return -1;
         } else {
            return o1.getSingletonGeneration() == o2.getSingletonGeneration() ? 0 : 1;
         }
      }
   }
}
