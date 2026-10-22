package org.glassfish.hk2.utilities;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.internal.HandleAndService;
import org.glassfish.hk2.internal.ImmediateLocalLocatorFilter;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ImmediateContext implements Context<Immediate> {
   private final HashMap<ActiveDescriptor<?>, HandleAndService> currentImmediateServices = new HashMap<>();
   private final HashMap<ActiveDescriptor<?>, Long> creating = new HashMap<>();
   private final ServiceLocator locator;
   private final Filter validationFilter;

   @Inject
   private ImmediateContext(ServiceLocator locator) {
      this.locator = locator;
      this.validationFilter = new ImmediateLocalLocatorFilter(locator.getLocatorId());
   }

   @Override
   public Class<? extends Annotation> getScope() {
      return Immediate.class;
   }

   @Override
   public <U> U findOrCreate(ActiveDescriptor<U> activeDescriptor, ServiceHandle<?> root) {
      U retVal = null;
      synchronized (this) {
         HandleAndService has = this.currentImmediateServices.get(activeDescriptor);
         if (has != null) {
            return (U)has.getService();
         }

         while (this.creating.containsKey(activeDescriptor)) {
            long alreadyCreatingThread = this.creating.get(activeDescriptor);
            if (alreadyCreatingThread == Thread.currentThread().getId()) {
               throw new MultiException(
                  new IllegalStateException(
                     "A circular dependency involving Immediate service "
                        + activeDescriptor.getImplementation()
                        + " was found.  Full descriptor is "
                        + activeDescriptor
                  )
               );
            }

            try {
               this.wait();
            } catch (InterruptedException var22) {
               throw new MultiException(var22);
            }
         }

         has = this.currentImmediateServices.get(activeDescriptor);
         if (has != null) {
            return (U)has.getService();
         }

         this.creating.put(activeDescriptor, Thread.currentThread().getId());
      }

      try {
         retVal = activeDescriptor.create(root);
      } finally {
         synchronized (this) {
            ServiceHandle<?> discoveredRoot = null;
            if (root != null && root.getActiveDescriptor().equals(activeDescriptor)) {
               discoveredRoot = root;
            }

            if (retVal != null) {
               this.currentImmediateServices.put(activeDescriptor, new HandleAndService(discoveredRoot, retVal));
            }

            this.creating.remove(activeDescriptor);
            this.notifyAll();
         }
      }

      return retVal;
   }

   @Override
   public synchronized boolean containsKey(ActiveDescriptor<?> descriptor) {
      return this.currentImmediateServices.containsKey(descriptor);
   }

   @Override
   public void destroyOne(ActiveDescriptor<?> descriptor) {
      this.destroyOne(descriptor, null);
   }

   private void destroyOne(ActiveDescriptor<?> descriptor, List<ImmediateErrorHandler> errorHandlers) {
      if (errorHandlers == null) {
         errorHandlers = this.locator.getAllServices(ImmediateErrorHandler.class);
      }

      synchronized (this) {
         HandleAndService has = this.currentImmediateServices.remove(descriptor);
         Object instance = has.getService();

         try {
            ((ActiveDescriptor<Object>)descriptor).dispose(instance);
         } catch (Throwable var12) {
            Throwable th = var12;

            for (ImmediateErrorHandler ieh : errorHandlers) {
               try {
                  ieh.preDestroyFailed(descriptor, th);
               } catch (Throwable var11) {
               }
            }
         }
      }
   }

   @Override
   public boolean supportsNullCreation() {
      return false;
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public void shutdown() {
      List<ImmediateErrorHandler> errorHandlers = this.locator.getAllServices(ImmediateErrorHandler.class);
      synchronized (this) {
         for (Entry<ActiveDescriptor<?>, HandleAndService> entry : new HashSet<>(this.currentImmediateServices.entrySet())) {
            HandleAndService has = entry.getValue();
            ServiceHandle<?> handle = has.getHandle();
            if (handle != null) {
               handle.destroy();
            } else {
               this.destroyOne(entry.getKey(), errorHandlers);
            }
         }
      }
   }

   private List<ActiveDescriptor<?>> getImmediateServices() {
      List<ActiveDescriptor<?>> inScopeAndInThisLocator;
      try {
         inScopeAndInThisLocator = this.locator.getDescriptors(this.validationFilter);
      } catch (IllegalStateException var3) {
         inScopeAndInThisLocator = Collections.emptyList();
      }

      return inScopeAndInThisLocator;
   }

   public Filter getValidationFilter() {
      return this.validationFilter;
   }

   public void doWork() {
      List<ActiveDescriptor<?>> inScopeAndInThisLocator = this.getImmediateServices();

      List<ImmediateErrorHandler> errorHandlers;
      try {
         errorHandlers = this.locator.getAllServices(ImmediateErrorHandler.class);
      } catch (IllegalStateException var14) {
         return;
      }

      LinkedHashSet<ActiveDescriptor<?>> newFullSet = new LinkedHashSet<>(inScopeAndInThisLocator);
      LinkedHashSet<ActiveDescriptor<?>> addMe = new LinkedHashSet<>();
      synchronized (this) {
         while (this.creating.size() > 0) {
            try {
               this.wait();
            } catch (InterruptedException var13) {
               throw new RuntimeException(var13);
            }
         }

         LinkedHashSet<ActiveDescriptor<?>> oldSet = new LinkedHashSet<>(this.currentImmediateServices.keySet());

         for (ActiveDescriptor<?> ad : inScopeAndInThisLocator) {
            if (!oldSet.contains(ad)) {
               addMe.add(ad);
            }
         }

         oldSet.removeAll(newFullSet);

         for (ActiveDescriptor<?> gone : oldSet) {
            HandleAndService has = this.currentImmediateServices.get(gone);
            ServiceHandle<?> handle = has.getHandle();
            if (handle != null) {
               handle.destroy();
            } else {
               this.destroyOne(gone, errorHandlers);
            }
         }
      }

      for (ActiveDescriptor<?> adx : addMe) {
         try {
            this.locator.getServiceHandle(adx).getService();
         } catch (Throwable var15) {
            Throwable th = var15;

            for (ImmediateErrorHandler ieh : errorHandlers) {
               try {
                  ieh.postConstructFailed(adx, th);
               } catch (Throwable var12) {
               }
            }
         }
      }
   }
}
