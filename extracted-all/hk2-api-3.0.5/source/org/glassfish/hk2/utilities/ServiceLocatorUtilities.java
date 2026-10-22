package org.glassfish.hk2.utilities;

import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.ImmediateController;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.InheritableThread;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PerThread;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.internal.ImmediateHelper;
import org.glassfish.hk2.internal.InheritableThreadContext;
import org.glassfish.hk2.internal.PerThreadContext;

public abstract class ServiceLocatorUtilities {
   private static final String DEFAULT_LOCATOR_NAME = "default";
   private static final Singleton SINGLETON = new ServiceLocatorUtilities.SingletonImpl();
   private static final PerLookup PER_LOOKUP = new ServiceLocatorUtilities.PerLookupImpl();
   private static final PerThread PER_THREAD = new ServiceLocatorUtilities.PerThreadImpl();
   private static final InheritableThread INHERITABLE_THREAD = new ServiceLocatorUtilities.InheritableThreadImpl();
   private static final Immediate IMMEDIATE = new ServiceLocatorUtilities.ImmediateImpl();

   public static void enablePerThreadScope(ServiceLocator locator) {
      try {
         addClasses(locator, true, PerThreadContext.class);
      } catch (MultiException var2) {
         if (!isDupException(var2)) {
            throw var2;
         }
      }
   }

   public static void enableInheritableThreadScope(ServiceLocator locator) {
      try {
         addClasses(locator, true, InheritableThreadContext.class);
      } catch (MultiException var2) {
         if (!isDupException(var2)) {
            throw var2;
         }
      }
   }

   public static void enableImmediateScope(ServiceLocator locator) {
      ImmediateController controller = enableImmediateScopeSuspended(locator);
      controller.setImmediateState(ImmediateController.ImmediateServiceState.RUNNING);
   }

   public static ImmediateController enableImmediateScopeSuspended(ServiceLocator locator) {
      try {
         addClasses(locator, true, ImmediateContext.class, ImmediateHelper.class);
      } catch (MultiException var2) {
         if (!isDupException(var2)) {
            throw var2;
         }
      }

      return locator.getService(ImmediateController.class);
   }

   public static void bind(ServiceLocator locator, Binder... binders) {
      DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
      DynamicConfiguration config = dcs.createDynamicConfiguration();

      for (Binder binder : binders) {
         binder.bind(config);
      }

      config.commit();
   }

   public static ServiceLocator bind(String name, Binder... binders) {
      ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
      ServiceLocator locator = factory.create(name);
      bind(locator, binders);
      return locator;
   }

   public static ServiceLocator bind(Binder... binders) {
      return bind("default", binders);
   }

   public static <T> ActiveDescriptor<T> addOneConstant(ServiceLocator locator, Object constant) {
      if (locator != null && constant != null) {
         return addOneDescriptor(locator, BuilderHelper.createConstantDescriptor(constant), false);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static List<FactoryDescriptors> addFactoryConstants(ServiceLocator locator, Factory<?>... constants) {
      if (locator == null) {
         throw new IllegalArgumentException();
      } else {
         DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
         DynamicConfiguration cd = dcs.createDynamicConfiguration();
         LinkedList<FactoryDescriptors> intermediateState = new LinkedList<>();

         for (Factory<?> factoryConstant : constants) {
            if (factoryConstant == null) {
               throw new IllegalArgumentException("One of the factories in " + Arrays.toString((Object[])constants) + " is null");
            }

            FactoryDescriptors fds = cd.addActiveFactoryDescriptor(factoryConstant.getClass());
            intermediateState.add(fds);
         }

         cd = dcs.createDynamicConfiguration();
         LinkedList<FactoryDescriptors> retVal = new LinkedList<>();
         int lcv = 0;

         for (FactoryDescriptors fds : intermediateState) {
            ActiveDescriptor<?> provideMethod = (ActiveDescriptor<?>)fds.getFactoryAsAFactory();
            Factory<?> constant = constants[lcv++];
            Descriptor constantDescriptor = BuilderHelper.createConstantDescriptor(constant);
            Descriptor addProvideMethod = new DescriptorImpl(provideMethod);
            FactoryDescriptorsImpl fdi = new FactoryDescriptorsImpl(constantDescriptor, addProvideMethod);
            retVal.add(cd.bind(fdi));
         }

         cd.commit();
         return retVal;
      }
   }

   public static <T> ActiveDescriptor<T> addOneConstant(ServiceLocator locator, Object constant, String name, Type... contracts) {
      if (locator != null && constant != null) {
         return addOneDescriptor(locator, BuilderHelper.createConstantDescriptor(constant, name, contracts), false);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static <T> ActiveDescriptor<T> addOneDescriptor(ServiceLocator locator, Descriptor descriptor) {
      return addOneDescriptor(locator, descriptor, true);
   }

   public static <T> ActiveDescriptor<T> addOneDescriptor(ServiceLocator locator, Descriptor descriptor, boolean requiresDeepCopy) {
      DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
      DynamicConfiguration config = dcs.createDynamicConfiguration();
      ActiveDescriptor<T> retVal;
      if (descriptor instanceof ActiveDescriptor) {
         ActiveDescriptor<T> active = (ActiveDescriptor<T>)descriptor;
         if (active.isReified()) {
            retVal = config.addActiveDescriptor(active, requiresDeepCopy);
         } else {
            retVal = config.bind(descriptor, requiresDeepCopy);
         }
      } else {
         retVal = config.bind(descriptor, requiresDeepCopy);
      }

      config.commit();
      return retVal;
   }

   public static List<FactoryDescriptors> addFactoryDescriptors(ServiceLocator locator, FactoryDescriptors... factories) {
      return addFactoryDescriptors(locator, true, factories);
   }

   public static List<FactoryDescriptors> addFactoryDescriptors(ServiceLocator locator, boolean requiresDeepCopy, FactoryDescriptors... factories) {
      if (factories != null && locator != null) {
         List<FactoryDescriptors> retVal = new ArrayList<>(factories.length);
         DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
         DynamicConfiguration config = dcs.createDynamicConfiguration();

         for (FactoryDescriptors factory : factories) {
            FactoryDescriptors addMe = config.bind(factory, requiresDeepCopy);
            retVal.add(addMe);
         }

         config.commit();
         return retVal;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static List<ActiveDescriptor<?>> addClasses(ServiceLocator locator, boolean idempotent, Class<?>... toAdd) {
      DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
      DynamicConfiguration config = dcs.createDynamicConfiguration();
      LinkedList<ActiveDescriptor<?>> retVal = new LinkedList<>();

      for (Class<?> addMe : toAdd) {
         if (Factory.class.isAssignableFrom(addMe)) {
            FactoryDescriptors fds = config.addActiveFactoryDescriptor(addMe);
            if (idempotent) {
               config.addIdempotentFilter(BuilderHelper.createDescriptorFilter(fds.getFactoryAsAService(), false));
               config.addIdempotentFilter(BuilderHelper.createDescriptorFilter(fds.getFactoryAsAFactory(), false));
            }

            retVal.add((ActiveDescriptor<?>)fds.getFactoryAsAService());
            retVal.add((ActiveDescriptor<?>)fds.getFactoryAsAFactory());
         } else {
            ActiveDescriptor<?> ad = config.addActiveDescriptor(addMe);
            if (idempotent) {
               config.addIdempotentFilter(BuilderHelper.createDescriptorFilter(ad, false));
            }

            retVal.add(ad);
         }
      }

      config.commit();
      return retVal;
   }

   public static List<ActiveDescriptor<?>> addClasses(ServiceLocator locator, Class<?>... toAdd) {
      return addClasses(locator, false, toAdd);
   }

   static String getBestContract(Descriptor d) {
      String impl = d.getImplementation();
      Set<String> contracts = d.getAdvertisedContracts();
      if (contracts.contains(impl)) {
         return impl;
      } else {
         Iterator var3 = contracts.iterator();
         return var3.hasNext() ? (String)var3.next() : impl;
      }
   }

   public static <T> ActiveDescriptor<T> findOneDescriptor(ServiceLocator locator, Descriptor descriptor) {
      if (locator != null && descriptor != null) {
         if (descriptor.getServiceId() != null && descriptor.getLocatorId() != null) {
            ActiveDescriptor<T> retVal = (ActiveDescriptor<T>)locator.getBestDescriptor(BuilderHelper.createSpecificDescriptorFilter(descriptor));
            if (retVal != null) {
               return retVal;
            }
         }

         final DescriptorImpl di;
         if (descriptor instanceof DescriptorImpl) {
            di = (DescriptorImpl)descriptor;
         } else {
            di = new DescriptorImpl(descriptor);
         }

         final String contract = getBestContract(descriptor);
         final String name = descriptor.getName();
         return (ActiveDescriptor<T>)locator.getBestDescriptor(new IndexedFilter() {
            @Override
            public boolean matches(Descriptor d) {
               return di.equals(d);
            }

            @Override
            public String getAdvertisedContract() {
               return contract;
            }

            @Override
            public String getName() {
               return name;
            }
         });
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static void removeOneDescriptor(ServiceLocator locator, Descriptor descriptor) {
      removeOneDescriptor(locator, descriptor, false);
   }

   public static void removeOneDescriptor(ServiceLocator locator, Descriptor descriptor, boolean includeAliasDescriptors) {
      if (locator != null && descriptor != null) {
         DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
         DynamicConfiguration config = dcs.createDynamicConfiguration();
         if (descriptor.getLocatorId() != null && descriptor.getServiceId() != null) {
            Filter destructionFilter = BuilderHelper.createSpecificDescriptorFilter(descriptor);
            config.addUnbindFilter(destructionFilter);
            if (includeAliasDescriptors) {
               List<ActiveDescriptor<?>> goingToDie = locator.getDescriptors(destructionFilter);
               if (!goingToDie.isEmpty()) {
                  ServiceLocatorUtilities.AliasFilter af = new ServiceLocatorUtilities.AliasFilter(goingToDie);
                  config.addUnbindFilter(af);
               }
            }

            config.commit();
         } else {
            final DescriptorImpl di;
            if (descriptor instanceof DescriptorImpl) {
               di = (DescriptorImpl)descriptor;
            } else {
               di = new DescriptorImpl(descriptor);
            }

            Filter destructionFilter = new Filter() {
               @Override
               public boolean matches(Descriptor d) {
                  return di.equals(d);
               }
            };
            config.addUnbindFilter(destructionFilter);
            if (includeAliasDescriptors) {
               List<ActiveDescriptor<?>> goingToDie = locator.getDescriptors(destructionFilter);
               if (!goingToDie.isEmpty()) {
                  ServiceLocatorUtilities.AliasFilter af = new ServiceLocatorUtilities.AliasFilter(goingToDie);
                  config.addUnbindFilter(af);
               }
            }

            config.commit();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static void removeFilter(ServiceLocator locator, Filter filter) {
      removeFilter(locator, filter, false);
   }

   public static void removeFilter(ServiceLocator locator, Filter filter, boolean includeAliasDescriptors) {
      if (locator != null && filter != null) {
         DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
         DynamicConfiguration config = dcs.createDynamicConfiguration();
         config.addUnbindFilter(filter);
         if (includeAliasDescriptors) {
            List<ActiveDescriptor<?>> goingToDie = locator.getDescriptors(filter);
            if (!goingToDie.isEmpty()) {
               ServiceLocatorUtilities.AliasFilter af = new ServiceLocatorUtilities.AliasFilter(goingToDie);
               config.addUnbindFilter(af);
            }
         }

         config.commit();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static <T> T getService(ServiceLocator locator, String className) {
      if (locator != null && className != null) {
         ActiveDescriptor<T> ad = (ActiveDescriptor<T>)locator.getBestDescriptor(BuilderHelper.createContractFilter(className));
         return ad == null ? null : locator.getServiceHandle(ad).getService();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static <T> T getService(ServiceLocator locator, Descriptor descriptor) {
      if (locator != null && descriptor != null) {
         Long locatorId = descriptor.getLocatorId();
         if (locatorId != null && locatorId == locator.getLocatorId() && descriptor instanceof ActiveDescriptor) {
            return locator.<T>getServiceHandle((ActiveDescriptor<T>)descriptor).getService();
         } else {
            ActiveDescriptor<T> found = findOneDescriptor(locator, descriptor);
            return found == null ? null : locator.getServiceHandle(found).getService();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static DynamicConfiguration createDynamicConfiguration(ServiceLocator locator) throws IllegalStateException {
      if (locator == null) {
         throw new IllegalArgumentException();
      } else {
         DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
         if (dcs == null) {
            throw new IllegalStateException();
         } else {
            return dcs.createDynamicConfiguration();
         }
      }
   }

   public static <T> T findOrCreateService(ServiceLocator locator, Class<T> type, Annotation... qualifiers) throws MultiException {
      if (locator != null && type != null) {
         ServiceHandle<T> retVal = locator.getServiceHandle(type, qualifiers);
         return retVal == null ? locator.createAndInitialize(type) : retVal.getService();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static String getOneMetadataField(Descriptor d, String field) {
      Map<String, List<String>> metadata = d.getMetadata();
      List<String> values = metadata.get(field);
      return values != null && !values.isEmpty() ? values.get(0) : null;
   }

   public static String getOneMetadataField(ServiceHandle<?> h, String field) {
      return getOneMetadataField(h.getActiveDescriptor(), field);
   }

   public static ServiceLocator createAndPopulateServiceLocator(String name) throws MultiException {
      ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(name);
      DynamicConfigurationService dcs = retVal.getService(DynamicConfigurationService.class);
      Populator populator = dcs.getPopulator();

      try {
         populator.populate();
         return retVal;
      } catch (IOException var5) {
         throw new MultiException(var5);
      }
   }

   public static ServiceLocator createAndPopulateServiceLocator() {
      return createAndPopulateServiceLocator(null);
   }

   public static void enableLookupExceptions(ServiceLocator locator) {
      if (locator == null) {
         throw new IllegalArgumentException();
      } else {
         try {
            addClasses(locator, true, RethrowErrorService.class);
         } catch (MultiException var2) {
            if (!isDupException(var2)) {
               throw var2;
            }
         }
      }
   }

   public static void enableGreedyResolution(ServiceLocator locator) {
      if (locator == null) {
         throw new IllegalArgumentException();
      } else {
         try {
            addClasses(locator, true, GreedyResolver.class);
         } catch (MultiException var2) {
            if (!isDupException(var2)) {
               throw var2;
            }
         }
      }
   }

   public static void dumpAllDescriptors(ServiceLocator locator) {
      dumpAllDescriptors(locator, System.err);
   }

   public static void dumpAllDescriptors(ServiceLocator locator, PrintStream output) {
      if (locator != null && output != null) {
         for (ActiveDescriptor<?> d : locator.getDescriptors(BuilderHelper.allFilter())) {
            output.println(d.toString());
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static Singleton getSingletonAnnotation() {
      return SINGLETON;
   }

   public static PerLookup getPerLookupAnnotation() {
      return PER_LOOKUP;
   }

   public static PerThread getPerThreadAnnotation() {
      return PER_THREAD;
   }

   public static InheritableThread getInheritableThreadAnnotation() {
      return INHERITABLE_THREAD;
   }

   public static Immediate getImmediateAnnotation() {
      return IMMEDIATE;
   }

   private static boolean isDupException(MultiException me) {
      boolean atLeastOne = false;

      for (Throwable error : me.getErrors()) {
         atLeastOne = true;
         if (!(error instanceof DuplicateServiceException)) {
            return false;
         }
      }

      return atLeastOne;
   }

   private static class AliasFilter implements Filter {
      private final Set<String> values = new HashSet<>();

      private AliasFilter(List<ActiveDescriptor<?>> bases) {
         for (ActiveDescriptor<?> base : bases) {
            String val = base.getLocatorId() + "." + base.getServiceId();
            this.values.add(val);
         }
      }

      @Override
      public boolean matches(Descriptor d) {
         List<String> mAliasVals = d.getMetadata().get("__AliasOf");
         if (mAliasVals != null && !mAliasVals.isEmpty()) {
            String aliasVal = mAliasVals.get(0);
            return this.values.contains(aliasVal);
         } else {
            return false;
         }
      }
   }

   private static class ImmediateImpl extends AnnotationLiteral<Immediate> implements Immediate {
      private static final long serialVersionUID = -4189466670823669605L;
   }

   private static class InheritableThreadImpl extends AnnotationLiteral<InheritableThread> implements InheritableThread {
      private static final long serialVersionUID = -3955786566272090916L;
   }

   private static class PerLookupImpl extends AnnotationLiteral<PerLookup> implements PerLookup {
      private static final long serialVersionUID = 6554011929159736762L;
   }

   private static class PerThreadImpl extends AnnotationLiteral<PerThread> implements PerThread {
      private static final long serialVersionUID = 521793185589873261L;
   }

   private static class SingletonImpl extends AnnotationLiteral<Singleton> implements Singleton {
      private static final long serialVersionUID = -2425625604832777314L;
   }
}
