package org.jvnet.hk2.internal;

import java.util.Arrays;
import java.util.LinkedList;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;
import org.glassfish.hk2.utilities.reflection.Pretty;

public class DynamicConfigurationImpl implements DynamicConfiguration {
   private final ServiceLocatorImpl locator;
   private final LinkedList<SystemDescriptor<?>> allDescriptors = new LinkedList<>();
   private final LinkedList<Filter> allUnbindFilters = new LinkedList<>();
   private final LinkedList<Filter> allIdempotentFilters = new LinkedList<>();
   private final LinkedList<TwoPhaseResource> allResources = new LinkedList<>();
   private final Object lock = new Object();
   private boolean committed = false;

   public DynamicConfigurationImpl(ServiceLocatorImpl locator) {
      this.locator = locator;
   }

   public <T> ActiveDescriptor<T> bind(Descriptor key) {
      return this.bind(key, true);
   }

   public <T> ActiveDescriptor<T> bind(Descriptor key, boolean requiresDeepCopy) {
      this.checkState();
      checkDescriptor(key);
      SystemDescriptor<T> sd = new SystemDescriptor<>(key, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
      this.allDescriptors.add(sd);
      return sd;
   }

   public FactoryDescriptors bind(FactoryDescriptors factoryDescriptors) {
      return this.bind(factoryDescriptors, true);
   }

   public FactoryDescriptors bind(FactoryDescriptors factoryDescriptors, boolean requiresDeepCopy) {
      if (factoryDescriptors == null) {
         throw new IllegalArgumentException("factoryDescriptors is null");
      } else {
         Descriptor asService = factoryDescriptors.getFactoryAsAService();
         Descriptor asFactory = factoryDescriptors.getFactoryAsAFactory();
         checkDescriptor(asService);
         checkDescriptor(asFactory);
         String implClassService = asService.getImplementation();
         String implClassFactory = asFactory.getImplementation();
         if (!implClassService.equals(implClassFactory)) {
            throw new IllegalArgumentException("The implementation classes must match (" + implClassService + "/" + implClassFactory + ")");
         } else if (!asService.getDescriptorType().equals(DescriptorType.CLASS)) {
            throw new IllegalArgumentException("The getFactoryAsService descriptor must be of type CLASS");
         } else if (!asFactory.getDescriptorType().equals(DescriptorType.PROVIDE_METHOD)) {
            throw new IllegalArgumentException("The getFactoryAsFactory descriptor must be of type PROVIDE_METHOD");
         } else {
            SystemDescriptor<?> boundAsService = new SystemDescriptor(asService, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
            SystemDescriptor<?> boundAsFactory = new SystemDescriptor(asFactory, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
            if (asService instanceof ActiveDescriptor) {
               boundAsFactory.setFactoryIds(boundAsService.getLocatorId(), boundAsService.getServiceId());
            }

            this.allDescriptors.add(boundAsFactory);
            this.allDescriptors.add(boundAsService);
            return new FactoryDescriptorsImpl(boundAsService, boundAsFactory);
         }
      }
   }

   public <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> activeDescriptor) throws IllegalArgumentException {
      return this.addActiveDescriptor(activeDescriptor, true);
   }

   public <T> ActiveDescriptor<T> addActiveDescriptor(ActiveDescriptor<T> activeDescriptor, boolean requiresDeepCopy) throws IllegalArgumentException {
      this.checkState();
      checkDescriptor(activeDescriptor);
      if (!activeDescriptor.isReified()) {
         throw new IllegalArgumentException();
      } else {
         checkReifiedDescriptor(activeDescriptor);
         SystemDescriptor<T> retVal = new SystemDescriptor<>(activeDescriptor, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
         this.allDescriptors.add(retVal);
         return retVal;
      }
   }

   public <T> ActiveDescriptor<T> addActiveDescriptor(Class<T> rawClass) throws IllegalArgumentException {
      AutoActiveDescriptor<T> ad = Utilities.createAutoDescriptor(rawClass, this.locator);
      checkReifiedDescriptor(ad);
      ActiveDescriptor<T> retVal = this.addActiveDescriptor(ad, false);
      ad.resetSelfDescriptor(retVal);
      return retVal;
   }

   public <T> FactoryDescriptors addActiveFactoryDescriptor(Class<? extends Factory<T>> rawFactoryClass) throws MultiException, IllegalArgumentException {
      Collector collector = new Collector();
      Utilities.checkFactoryType(rawFactoryClass, collector);
      collector.throwIfErrors();
      ActiveDescriptor<?> factoryDescriptor = this.addActiveDescriptor(rawFactoryClass);
      ActiveDescriptor<?> userMethodDescriptor = Utilities.createAutoFactoryDescriptor(rawFactoryClass, factoryDescriptor, this.locator);
      ActiveDescriptor<?> methodDescriptor = this.addActiveDescriptor((ActiveDescriptor<T>)userMethodDescriptor);
      return new FactoryDescriptorsImpl(factoryDescriptor, methodDescriptor);
   }

   public void addUnbindFilter(Filter unbindFilter) throws IllegalArgumentException {
      if (unbindFilter == null) {
         throw new IllegalArgumentException();
      } else {
         this.checkState();
         this.allUnbindFilters.add(unbindFilter);
      }
   }

   public void addIdempotentFilter(Filter... idempotentFilter) throws IllegalArgumentException {
      if (idempotentFilter == null) {
         throw new IllegalArgumentException();
      } else {
         this.checkState();

         for (Filter iFilter : idempotentFilter) {
            if (iFilter == null) {
               throw new IllegalArgumentException();
            }
         }

         this.allIdempotentFilters.addAll(Arrays.asList(idempotentFilter));
      }
   }

   public void registerTwoPhaseResources(TwoPhaseResource... resources) {
      this.checkState();
      if (resources != null) {
         for (TwoPhaseResource resource : resources) {
            if (resource != null) {
               this.allResources.add(resource);
            }
         }
      }
   }

   public void commit() throws MultiException {
      synchronized (this.lock) {
         this.checkState();
         this.committed = true;
      }

      this.locator.addConfiguration(this);
   }

   private void checkState() {
      synchronized (this.lock) {
         if (this.committed) {
            throw new IllegalStateException();
         }
      }
   }

   private static void checkDescriptor(Descriptor d) {
      if (d == null) {
         throw new IllegalArgumentException();
      } else if (d.getImplementation() == null) {
         throw new IllegalArgumentException();
      } else if (d.getAdvertisedContracts() == null) {
         throw new IllegalArgumentException();
      } else if (d.getDescriptorType() == null) {
         throw new IllegalArgumentException();
      } else if (d.getDescriptorVisibility() == null) {
         throw new IllegalArgumentException();
      } else if (d.getMetadata() == null) {
         throw new IllegalArgumentException();
      } else if (d.getQualifiers() == null) {
         throw new IllegalArgumentException();
      }
   }

   private static void checkReifiedDescriptor(ActiveDescriptor<?> d) {
      if (d.isProxiable() != null) {
         if (d.isProxiable()) {
            if (Utilities.isUnproxiableScope(d.getScopeAnnotation())) {
               throw new IllegalArgumentException();
            }
         }
      }
   }

   LinkedList<SystemDescriptor<?>> getAllDescriptors() {
      return this.allDescriptors;
   }

   LinkedList<Filter> getUnbindFilters() {
      return this.allUnbindFilters;
   }

   LinkedList<Filter> getIdempotentFilters() {
      return this.allIdempotentFilters;
   }

   LinkedList<TwoPhaseResource> getResources() {
      return this.allResources;
   }

   @Override
   public String toString() {
      return "DynamicConfigurationImpl("
         + this.locator
         + ","
         + Pretty.collection(this.allDescriptors)
         + ","
         + Pretty.collection(this.allUnbindFilters)
         + ","
         + Pretty.collection(this.allResources)
         + ","
         + System.identityHashCode(this)
         + ")";
   }
}
