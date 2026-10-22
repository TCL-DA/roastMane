package org.jvnet.hk2.internal;

import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.api.MethodParameter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Operation;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.PreDestroy;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.api.ServiceLocatorState;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.api.TwoPhaseTransactionData;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.InjecteeImpl;
import org.glassfish.hk2.utilities.RethrowErrorService;
import org.glassfish.hk2.utilities.cache.Cache;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import org.glassfish.hk2.utilities.cache.CacheUtilities;
import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.cache.ComputationErrorException;
import org.glassfish.hk2.utilities.cache.WeakCARCache;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.Logger;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;

public class ServiceLocatorImpl implements ServiceLocator {
   private static final String BIND_TRACING_PATTERN_PROPERTY = "org.jvnet.hk2.properties.bind.tracing.pattern";
   private static final String BIND_TRACING_PATTERN = AccessController.doPrivileged(new PrivilegedAction<String>() {
      public String run() {
         return System.getProperty("org.jvnet.hk2.properties.bind.tracing.pattern");
      }
   });
   private static final String BIND_TRACING_STACKS_PROPERTY = "org.jvnet.hk2.properties.bind.tracing.stacks";
   private static final boolean BIND_TRACING_STACKS = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.bind.tracing.stacks", "false"));
      }
   });
   private static final int CACHE_SIZE = 20000;
   private static final Object sLock = new Object();
   private static long currentLocatorId = 0L;
   static final DescriptorComparator DESCRIPTOR_COMPARATOR = new DescriptorComparator();
   private static final ServiceHandleComparator HANDLE_COMPARATOR = new ServiceHandleComparator();
   private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private final WriteLock wLock = this.readWriteLock.writeLock();
   private final ReadLock rLock = this.readWriteLock.readLock();
   private final AtomicLong nextServiceId = new AtomicLong();
   private final String locatorName;
   private final long id;
   private final ServiceLocatorImpl parent;
   private volatile boolean neutralContextClassLoader = true;
   private final ClassReflectionHelper classReflectionHelper = new ClassReflectionHelperImpl();
   private final PerLocatorUtilities perLocatorUtilities = new PerLocatorUtilities(this);
   private final IndexedListData allDescriptors = new IndexedListData();
   private final HashMap<String, IndexedListData> descriptorsByAdvertisedContract = new HashMap<>();
   private final HashMap<String, IndexedListData> descriptorsByName = new HashMap<>();
   private final Context<Singleton> singletonContext = new SingletonContext(this);
   private final Context<PerLookup> perLookupContext = new PerLookupContext();
   private final LinkedHashSet<ValidationService> allValidators = new LinkedHashSet<>();
   private final LinkedList<ErrorService> errorHandlers = new LinkedList<>(Collections.singletonList(new RethrowErrorService()));
   private final LinkedList<ServiceHandle<?>> configListeners = new LinkedList<>();
   private volatile boolean hasInterceptionServices = false;
   private final LinkedList<InterceptionService> interceptionServices = new LinkedList<>();
   private final Cache<Class<? extends Annotation>, Context<?>> contextCache = new Cache(new Computable<Class<? extends Annotation>, Context<?>>() {
      public Context<?> compute(Class<? extends Annotation> a) {
         return ServiceLocatorImpl.this._resolveContext(a);
      }
   });
   private final Map<ServiceLocatorImpl, ServiceLocatorImpl> children = new WeakHashMap<>();
   private final Object classAnalyzerLock = new Object();
   private final HashMap<String, ClassAnalyzer> classAnalyzers = new HashMap<>();
   private String defaultClassAnalyzer = "default";
   private volatile Unqualified defaultUnqualified = null;
   private ConcurrentHashMap<Class<? extends Annotation>, InjectionResolver<?>> allResolvers = new ConcurrentHashMap<>();
   private final Cache<SystemInjecteeImpl, InjectionResolver<?>> injecteeToResolverCache = new Cache(
      new Computable<SystemInjecteeImpl, InjectionResolver<?>>() {
         public InjectionResolver<?> compute(SystemInjecteeImpl key) {
            return ServiceLocatorImpl.this.perLocatorUtilities.getInjectionResolver(ServiceLocatorImpl.this.getMe(), key);
         }
      }
   );
   private ServiceLocatorState state = ServiceLocatorState.RUNNING;
   private final WeakCARCache<ServiceLocatorImpl.IgdCacheKey, ServiceLocatorImpl.IgdValue> igdCache = CacheUtilities.createWeakCARCache(
      new Computable<ServiceLocatorImpl.IgdCacheKey, ServiceLocatorImpl.IgdValue>() {
         public ServiceLocatorImpl.IgdValue compute(ServiceLocatorImpl.IgdCacheKey key) {
            return ServiceLocatorImpl.this.igdCacheCompute(key);
         }
      }, 20000, false
   );
   private final WeakCARCache<ServiceLocatorImpl.IgdCacheKey, ServiceLocatorImpl.IgdValue> igashCache = CacheUtilities.createWeakCARCache(
      new Computable<ServiceLocatorImpl.IgdCacheKey, ServiceLocatorImpl.IgdValue>() {
         public ServiceLocatorImpl.IgdValue compute(ServiceLocatorImpl.IgdCacheKey key) {
            List<SystemDescriptor<?>> candidates = ServiceLocatorImpl.this.getDescriptors(key.filter, null, true, false, true);
            ImmediateResults immediate = ServiceLocatorImpl.this.narrow(
               ServiceLocatorImpl.this, candidates, key.contractOrImpl, null, null, false, true, null, key.filter, key.qualifiers
            );
            NarrowResults results = immediate.getTimelessResults();
            if (!results.getErrors().isEmpty()) {
               Utilities.handleErrors(results, new LinkedList<>(ServiceLocatorImpl.this.errorHandlers));
               throw new ComputationErrorException(ServiceLocatorImpl.this.new IgdValue(results, immediate));
            } else {
               return ServiceLocatorImpl.this.new IgdValue(results, immediate);
            }
         }
      },
      20000,
      false
   );

   private static long getAndIncrementLocatorId() {
      synchronized (sLock) {
         return currentLocatorId++;
      }
   }

   public ServiceLocatorImpl(String name, ServiceLocatorImpl parent) {
      this.locatorName = name;
      this.parent = parent;
      if (parent != null) {
         parent.addChild(this);
      }

      this.id = getAndIncrementLocatorId();
      Logger.getLogger().debug("Created ServiceLocator " + this);
      if (BIND_TRACING_PATTERN != null) {
         Logger.getLogger().debug("HK2 will trace binds and unbinds of " + BIND_TRACING_PATTERN + " with stacks " + BIND_TRACING_STACKS + " in " + this);
      }
   }

   private boolean callValidate(ValidationService vs, ValidationInformation vi) {
      try {
         return vs.getValidator().validate(vi);
      } catch (Throwable var11) {
         List<ErrorService> localErrorServices = new LinkedList<>(this.errorHandlers);
         MultiException useException;
         if (var11 instanceof MultiException) {
            useException = (MultiException)var11;
         } else {
            useException = new MultiException(var11);
         }

         ErrorInformationImpl ei = new ErrorInformationImpl(ErrorType.VALIDATE_FAILURE, vi.getCandidate(), vi.getInjectee(), useException);

         for (ErrorService errorService : localErrorServices) {
            try {
               errorService.onFailure(ei);
            } catch (Throwable var10) {
               Logger.getLogger().debug("ServiceLocatorImpl", "callValidate", var10);
            }
         }

         return false;
      }
   }

   private boolean validate(SystemDescriptor<?> descriptor, Injectee onBehalfOf, Filter filter) {
      for (ValidationService vs : this.getAllValidators()) {
         if (descriptor.isValidating(vs) && !this.callValidate(vs, new ValidationInformationImpl(Operation.LOOKUP, descriptor, onBehalfOf, filter))) {
            return false;
         }
      }

      return true;
   }

   private List<SystemDescriptor<?>> getDescriptors(Filter filter, Injectee onBehalfOf, boolean getParents, boolean doValidation, boolean getLocals) {
      if (filter == null) {
         throw new IllegalArgumentException("filter is null");
      } else {
         this.rLock.lock();

         LinkedList<SystemDescriptor<?>> retVal;
         try {
            Collection<SystemDescriptor<?>> sortMeOut;
            if (filter instanceof IndexedFilter) {
               IndexedFilter df = (IndexedFilter)filter;
               if (df.getName() != null) {
                  String name = df.getName();
                  IndexedListData ild = this.descriptorsByName.get(name);
                  Collection<SystemDescriptor<?>> scopedByName = ild == null ? null : ild.getSortedList();
                  if (scopedByName == null) {
                     scopedByName = Collections.emptyList();
                  }

                  if (df.getAdvertisedContract() != null) {
                     sortMeOut = new LinkedList<>();

                     for (SystemDescriptor<?> candidate : scopedByName) {
                        if (candidate.getAdvertisedContracts().contains(df.getAdvertisedContract())) {
                           sortMeOut.add(candidate);
                        }
                     }
                  } else {
                     sortMeOut = scopedByName;
                  }
               } else if (df.getAdvertisedContract() != null) {
                  String advertisedContract = df.getAdvertisedContract();
                  IndexedListData ildx = this.descriptorsByAdvertisedContract.get(advertisedContract);
                  sortMeOut = ildx == null ? null : ildx.getSortedList();
                  if (sortMeOut == null) {
                     sortMeOut = Collections.emptyList();
                  }
               } else {
                  sortMeOut = this.allDescriptors.getSortedList();
               }
            } else {
               sortMeOut = this.allDescriptors.getSortedList();
            }

            retVal = new LinkedList<>();

            for (SystemDescriptor<?> candidatex : sortMeOut) {
               if ((getLocals || !DescriptorVisibility.LOCAL.equals(candidatex.getDescriptorVisibility()))
                  && (!doValidation || this.validate(candidatex, onBehalfOf, filter))
                  && filter.matches(candidatex)) {
                  retVal.add(candidatex);
               }
            }
         } finally {
            this.rLock.unlock();
         }

         if (getParents && this.parent != null) {
            TreeSet<SystemDescriptor<?>> sorter = new TreeSet<>(DESCRIPTOR_COMPARATOR);
            sorter.addAll(retVal);
            sorter.addAll(this.parent.getDescriptors(filter, onBehalfOf, getParents, doValidation, false));
            retVal.clear();
            retVal.addAll(sorter);
         }

         return retVal;
      }
   }

   private List<ActiveDescriptor<?>> protectedGetDescriptors(final Filter filter) {
      return AccessController.doPrivileged(new PrivilegedAction<List<ActiveDescriptor<?>>>() {
         public List<ActiveDescriptor<?>> run() {
            return ServiceLocatorImpl.this.getDescriptors(filter);
         }
      });
   }

   public List<ActiveDescriptor<?>> getDescriptors(Filter filter) {
      this.checkState();
      return (List<ActiveDescriptor<?>>)ReflectionHelper.cast(this.getDescriptors(filter, null, true, true, true));
   }

   public ActiveDescriptor<?> getBestDescriptor(Filter filter) {
      if (filter == null) {
         throw new IllegalArgumentException("filter is null");
      } else {
         this.checkState();
         List<ActiveDescriptor<?>> sorted = this.getDescriptors(filter);
         return Utilities.getFirstThingInList(sorted);
      }
   }

   public ActiveDescriptor<?> reifyDescriptor(Descriptor descriptor, Injectee injectee) throws MultiException {
      this.checkState();
      if (descriptor == null) {
         throw new IllegalArgumentException();
      } else if (!(descriptor instanceof ActiveDescriptor)) {
         SystemDescriptor<?> sd = new SystemDescriptor(descriptor, true, this, null);
         Class<?> implClass = this.loadClass(descriptor, injectee);
         Collector collector = new Collector();
         sd.reify(implClass, collector);
         collector.throwIfErrors();
         return sd;
      } else {
         ActiveDescriptor<?> active = (ActiveDescriptor<?>)descriptor;
         if (active.isReified()) {
            return active;
         } else {
            SystemDescriptor<?> sd;
            if (active instanceof SystemDescriptor) {
               sd = (SystemDescriptor<?>)active;
            } else {
               sd = new SystemDescriptor(descriptor, true, this, null);
            }

            Class<?> implClass = sd.getPreAnalyzedClass();
            if (implClass == null) {
               implClass = this.loadClass(descriptor, injectee);
            }

            Collector collector = new Collector();
            sd.reify(implClass, collector);
            collector.throwIfErrors();
            return sd;
         }
      }
   }

   public ActiveDescriptor<?> reifyDescriptor(Descriptor descriptor) throws MultiException {
      this.checkState();
      return this.reifyDescriptor(descriptor, null);
   }

   private ActiveDescriptor<?> secondChanceResolve(Injectee injectee) {
      Collector collector = new Collector();
      List<ServiceHandle<JustInTimeInjectionResolver>> jitResolvers = (List<ServiceHandle<JustInTimeInjectionResolver>>)ReflectionHelper.cast(
         this.getAllServiceHandles(JustInTimeInjectionResolver.class)
      );

      Object var20;
      try {
         boolean modified = false;
         boolean aJITFailed = false;

         for (ServiceHandle<JustInTimeInjectionResolver> handle : jitResolvers) {
            if (injectee.getInjecteeClass() == null || !injectee.getInjecteeClass().getName().equals(handle.getActiveDescriptor().getImplementation())) {
               JustInTimeInjectionResolver jitResolver;
               try {
                  jitResolver = (JustInTimeInjectionResolver)handle.getService();
               } catch (MultiException var18) {
                  Logger.getLogger().debug(handle.toString(), "secondChanceResolver", var18);
                  continue;
               }

               boolean jitModified = false;

               try {
                  jitModified = jitResolver.justInTimeResolution(injectee);
               } catch (Throwable var17) {
                  collector.addThrowable(var17);
                  aJITFailed = true;
               }

               modified = jitModified || modified;
            }
         }

         if (aJITFailed) {
            collector.throwIfErrors();
         }

         if (modified) {
            return this.internalGetInjecteeDescriptor(injectee, true);
         }

         var20 = null;
      } finally {
         Iterator var12 = jitResolvers.iterator();

         while (true) {
            if (!var12.hasNext()) {
               ;
            } else {
               ServiceHandle<JustInTimeInjectionResolver> jitResolver = (ServiceHandle<JustInTimeInjectionResolver>)var12.next();
               if (jitResolver.getActiveDescriptor().getScope() == null || PerLookup.class.getName().equals(jitResolver.getActiveDescriptor().getScope())) {
                  jitResolver.destroy();
               }
            }
         }
      }

      return (ActiveDescriptor<?>)var20;
   }

   private ActiveDescriptor<?> internalGetInjecteeDescriptor(Injectee injectee, boolean calledFromSecondChanceResolveMethod) {
      if (injectee == null) {
         throw new IllegalArgumentException();
      } else {
         this.checkState();
         Type requiredType = injectee.getRequiredType();
         Class<?> rawType = ReflectionHelper.getRawClass(requiredType);
         if (rawType == null) {
            throw new MultiException(
               new IllegalArgumentException("Invalid injectee with required type of " + injectee.getRequiredType() + " passed to getInjecteeDescriptor")
            );
         } else if (Provider.class.equals(rawType) || Iterable.class.equals(rawType) || IterableProvider.class.equals(rawType)) {
            boolean isIterable = IterableProvider.class.equals(rawType);
            IterableProviderImpl<?> value = new IterableProviderImpl(
               this, ReflectionHelper.getFirstTypeArgument(requiredType), injectee.getRequiredQualifiers(), injectee.getUnqualified(), injectee, isIterable
            );
            return new ConstantActiveDescriptor<IterableProviderImpl<?>>(value, this);
         } else if (Optional.class.equals(rawType)) {
            InjecteeImpl optInjectee = new InjecteeImpl(injectee);
            optInjectee.setOptional(true);
            ActiveDescriptor<?> descriptor = this.internalGetDescriptor(
               optInjectee,
               optInjectee.getRequiredType(),
               ReflectionHelper.getNameFromAllQualifiers(optInjectee.getRequiredQualifiers(), optInjectee.getParent()),
               optInjectee.getUnqualified(),
               false,
               optInjectee.getRequiredQualifiers().toArray(new Annotation[0])
            );
            if (descriptor == null) {
               descriptor = new OptionalActiveDescriptor(optInjectee, this);
            }

            return descriptor;
         } else if (Topic.class.equals(rawType)) {
            TopicImpl<?> value = new TopicImpl(this, ReflectionHelper.getFirstTypeArgument(requiredType), injectee.getRequiredQualifiers());
            return new ConstantActiveDescriptor<TopicImpl<?>>(value, this);
         } else {
            Set<Annotation> qualifiersAsSet = injectee.getRequiredQualifiers();
            String name = ReflectionHelper.getNameFromAllQualifiers(qualifiersAsSet, injectee.getParent());
            Annotation[] qualifiers = qualifiersAsSet.toArray(new Annotation[qualifiersAsSet.size()]);
            return this.internalGetDescriptor(injectee, requiredType, name, injectee.getUnqualified(), false, calledFromSecondChanceResolveMethod, qualifiers);
         }
      }
   }

   public ActiveDescriptor<?> getInjecteeDescriptor(Injectee injectee) throws MultiException {
      return this.internalGetInjecteeDescriptor(injectee, false);
   }

   public <T> ServiceHandle<T> getServiceHandle(ActiveDescriptor<T> activeDescriptor, Injectee injectee) throws MultiException {
      if (activeDescriptor != null) {
         if (!(activeDescriptor instanceof SystemDescriptor) && !(activeDescriptor instanceof ConstantActiveDescriptor)) {
            throw new IllegalArgumentException(
               "The descriptor passed to getServiceHandle must have been bound into a ServiceLocator.  The descriptor is of type "
                  + activeDescriptor.getClass().getName()
            );
         }

         Long sdLocator = activeDescriptor.getLocatorId();
         if (sdLocator == null) {
            throw new IllegalArgumentException("The descriptor passed to getServiceHandle is not associated with any ServiceLocator");
         }

         if (sdLocator != this.id) {
            if (this.parent != null) {
               return this.parent.getServiceHandle(activeDescriptor, injectee);
            }

            throw new IllegalArgumentException(
               "The descriptor passed to getServiceHandle is not associated with this ServiceLocator (id="
                  + this.id
                  + ").  It is associated ServiceLocator id="
                  + sdLocator
            );
         }

         Long sdSID = activeDescriptor.getServiceId();
         if (activeDescriptor instanceof SystemDescriptor && sdSID == null) {
            throw new IllegalArgumentException("The descriptor passed to getServiceHandle was never added to this ServiceLocator (id=" + this.id + ")");
         }
      }

      return this.getServiceHandleImpl(activeDescriptor, injectee);
   }

   private <T> ServiceHandleImpl<T> getServiceHandleImpl(ActiveDescriptor<T> activeDescriptor, Injectee injectee) throws MultiException {
      if (activeDescriptor == null) {
         throw new IllegalArgumentException();
      } else {
         this.checkState();
         return new ServiceHandleImpl<>(this, activeDescriptor, injectee);
      }
   }

   public <T> ServiceHandle<T> getServiceHandle(ActiveDescriptor<T> activeDescriptor) throws MultiException {
      return this.getServiceHandle(activeDescriptor, null);
   }

   private <T> ServiceHandleImpl<T> internalGetServiceHandle(ActiveDescriptor<T> activeDescriptor, Type requestedType, Injectee originalRequest) {
      if (activeDescriptor == null) {
         throw new IllegalArgumentException();
      } else {
         this.checkState();
         if (requestedType == null) {
            return this.getServiceHandleImpl(activeDescriptor, null);
         } else {
            Injectee useInjectee = (Injectee)(originalRequest != null ? originalRequest : new InjecteeImpl(requestedType));
            return this.getServiceHandleImpl(activeDescriptor, useInjectee);
         }
      }
   }

   @Deprecated
   public <T> T getService(ActiveDescriptor<T> activeDescriptor, ServiceHandle<?> root) throws MultiException {
      return this.getService(activeDescriptor, root, null);
   }

   public <T> T getService(ActiveDescriptor<T> activeDescriptor, ServiceHandle<?> root, Injectee originalRequest) throws MultiException {
      this.checkState();
      Type contractOrImpl = originalRequest == null ? null : originalRequest.getRequiredType();
      Class<?> rawClass = ReflectionHelper.getRawClass(contractOrImpl);
      if (root == null) {
         ServiceHandleImpl<T> tmpRoot = new ServiceHandleImpl<>(this, activeDescriptor, originalRequest);
         return Utilities.createService(activeDescriptor, originalRequest, this, tmpRoot, rawClass);
      } else {
         ServiceHandleImpl<?> rootImpl = (ServiceHandleImpl<?>)root;
         ServiceHandleImpl<T> subHandle = this.internalGetServiceHandle(activeDescriptor, contractOrImpl, originalRequest);
         if (PerLookup.class.equals(activeDescriptor.getScopeAnnotation())) {
            rootImpl.addSubHandle(subHandle);
         }

         rootImpl.pushInjectee(originalRequest);

         Object var8;
         try {
            var8 = subHandle.getService((ServiceHandle<T>)root);
         } finally {
            rootImpl.popInjectee();
         }

         return (T)var8;
      }
   }

   public <T> T getService(Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
      return this.internalGetService(contractOrImpl, null, null, qualifiers);
   }

   public <T> T getService(Type contractOrImpl, Annotation... qualifiers) throws MultiException {
      return this.internalGetService(contractOrImpl, null, null, qualifiers);
   }

   public <T> T getService(Class<T> contractOrImpl, String name, Annotation... qualifiers) throws MultiException {
      return this.internalGetService(contractOrImpl, name, null, qualifiers);
   }

   public <T> T getService(Type contractOrImpl, String name, Annotation... qualifiers) throws MultiException {
      return this.internalGetService(contractOrImpl, name, null, qualifiers);
   }

   private <T> T internalGetService(Type contractOrImpl, String name, Unqualified unqualified, Annotation... qualifiers) {
      return this.internalGetService(contractOrImpl, name, unqualified, false, qualifiers);
   }

   private <T> T internalGetService(
      Type contractOrImpl, String name, Unqualified unqualified, boolean calledFromSecondChanceResolveMethod, Annotation... qualifiers
   ) {
      this.checkState();
      Class<?> rawType = ReflectionHelper.getRawClass(contractOrImpl);
      if (rawType != null && (Provider.class.equals(rawType) || IterableProvider.class.equals(rawType))) {
         boolean isIterable = IterableProvider.class.equals(rawType);
         Type requiredType = ReflectionHelper.getFirstTypeArgument(contractOrImpl);
         HashSet<Annotation> requiredQualifiers = new HashSet<>();

         for (Annotation qualifier : qualifiers) {
            requiredQualifiers.add(qualifier);
         }

         InjecteeImpl injectee = new InjecteeImpl(requiredType);
         injectee.setRequiredQualifiers(requiredQualifiers);
         injectee.setUnqualified(unqualified);
         return (T)(new IterableProviderImpl(this, requiredType, requiredQualifiers, unqualified, injectee, isIterable));
      } else {
         ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, name, unqualified, false, calledFromSecondChanceResolveMethod, qualifiers);
         return ad == null ? null : Utilities.createService(ad, null, this, null, rawType);
      }
   }

   <T> T getUnqualifiedService(Type contractOrImpl, Unqualified unqualified, boolean isIterable, Annotation... qualifiers) throws MultiException {
      return this.internalGetService(contractOrImpl, null, unqualified, true, qualifiers);
   }

   private <T> List<T> protectedGetAllServices(final Type contractOrImpl, final Annotation... qualifiers) {
      return AccessController.doPrivileged(new PrivilegedAction<List<T>>() {
         public List<T> run() {
            return ServiceLocatorImpl.this.getAllServices(contractOrImpl, qualifiers);
         }
      });
   }

   public <T> List<T> getAllServices(Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
      return this.getAllServices(contractOrImpl, qualifiers);
   }

   public <T> List<T> getAllServices(Type contractOrImpl, Annotation... qualifiers) throws MultiException {
      this.checkState();
      return (List<T>)this.internalGetAllServiceHandles(contractOrImpl, null, false, false, qualifiers);
   }

   public <T> List<T> getAllServices(Annotation qualifier, Annotation... qualifiers) throws MultiException {
      this.checkState();
      List<ServiceHandle<?>> services = this.getAllServiceHandles(qualifier, qualifiers);
      List<T> retVal = new LinkedList<>();

      for (ServiceHandle<?> service : services) {
         retVal.add((T)service.getService());
      }

      return retVal;
   }

   public List<?> getAllServices(Filter searchCriteria) throws MultiException {
      this.checkState();
      List<ServiceHandle<?>> handleSet = this.getAllServiceHandles(searchCriteria);
      List<Object> retVal = new LinkedList<>();

      for (ServiceHandle<?> handle : handleSet) {
         retVal.add(handle.getService());
      }

      return retVal;
   }

   public String getName() {
      return this.locatorName;
   }

   public ServiceLocatorState getState() {
      this.rLock.lock();

      ServiceLocatorState var1;
      try {
         var1 = this.state;
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   public boolean isShutdown() {
      return this.state.equals(ServiceLocatorState.SHUTDOWN);
   }

   public void shutdown() {
      this.wLock.lock();

      try {
         if (this.state.equals(ServiceLocatorState.SHUTDOWN)) {
            return;
         }

         synchronized (this.children) {
            Iterator<ServiceLocatorImpl> childIterator = this.children.keySet().iterator();

            while (childIterator.hasNext()) {
               ServiceLocatorImpl child = childIterator.next();
               childIterator.remove();
               child.shutdown();
            }
         }

         if (this.parent != null) {
            this.parent.removeChild(this);
         }
      } finally {
         this.wLock.unlock();
      }

      for (ServiceHandle<?> handle : this.getAllServiceHandles(new IndexedFilter() {
         public boolean matches(Descriptor d) {
            return d.getLocatorId().equals(ServiceLocatorImpl.this.id);
         }

         public String getAdvertisedContract() {
            return Context.class.getName();
         }

         public String getName() {
            return null;
         }
      })) {
         if (handle.isActive()) {
            Context<?> context = (Context<?>)handle.getService();
            context.shutdown();
         }
      }

      this.singletonContext.shutdown();
      this.wLock.lock();

      try {
         this.state = ServiceLocatorState.SHUTDOWN;
         this.allDescriptors.clear();
         this.descriptorsByAdvertisedContract.clear();
         this.descriptorsByName.clear();
         this.allResolvers.clear();
         this.injecteeToResolverCache.clear();
         this.allValidators.clear();
         this.errorHandlers.clear();
         this.igdCache.clear();
         this.igashCache.clear();
         this.classReflectionHelper.dispose();
         this.contextCache.clear();
         this.perLocatorUtilities.shutdown();
         synchronized (this.children) {
            this.children.clear();
         }

         Logger.getLogger().debug("Shutdown ServiceLocator " + this);
      } finally {
         this.wLock.unlock();
      }

      ServiceLocatorFactory.getInstance().destroy(this);
      Logger.getLogger().debug("ServiceLocator " + this + " has been shutdown");
   }

   public <T> T create(Class<T> createMe) {
      return this.create(createMe, null);
   }

   public <T> T create(Class<T> createMe, String strategy) {
      this.checkState();
      return Utilities.justCreate(createMe, this, strategy);
   }

   public void inject(Object injectMe) {
      this.inject(injectMe, null);
   }

   public Object assistedInject(Object injectMe, Method method, MethodParameter... params) {
      return this.assistedInject(injectMe, method, null, params);
   }

   public Object assistedInject(Object injectMe, Method method, ServiceHandle<?> root, MethodParameter... params) {
      this.checkState();
      return Utilities.justAssistedInject(injectMe, method, this, root, params);
   }

   public void inject(Object injectMe, String strategy) {
      this.checkState();
      Utilities.justInject(injectMe, this, strategy);
   }

   public void postConstruct(Object postConstructMe) {
      this.postConstruct(postConstructMe, null);
   }

   public void postConstruct(Object postConstructMe, String strategy) {
      this.checkState();
      if (postConstructMe == null) {
         throw new IllegalArgumentException();
      } else {
         if ((strategy == null || strategy.equals("default")) && postConstructMe instanceof PostConstruct) {
            ((PostConstruct)postConstructMe).postConstruct();
         } else {
            Utilities.justPostConstruct(postConstructMe, this, strategy);
         }
      }
   }

   public void preDestroy(Object preDestroyMe) {
      this.preDestroy(preDestroyMe, null);
   }

   public void preDestroy(Object preDestroyMe, String strategy) {
      this.checkState();
      if (preDestroyMe == null) {
         throw new IllegalArgumentException();
      } else {
         if ((strategy == null || strategy.equals("default")) && preDestroyMe instanceof PreDestroy) {
            ((PreDestroy)preDestroyMe).preDestroy();
         } else {
            Utilities.justPreDestroy(preDestroyMe, this, strategy);
         }
      }
   }

   public <U> U createAndInitialize(Class<U> createMe) {
      return this.createAndInitialize(createMe, null);
   }

   public <U> U createAndInitialize(Class<U> createMe, String strategy) {
      U retVal = this.create(createMe, strategy);
      this.inject(retVal, strategy);
      this.postConstruct(retVal, strategy);
      return retVal;
   }

   private static String getName(String name, Annotation... qualifiers) {
      if (name != null) {
         return name;
      } else {
         for (Annotation qualifier : qualifiers) {
            if (qualifier instanceof Named) {
               Named named = (Named)qualifier;
               if (named.value() != null && !named.value().isEmpty()) {
                  return named.value();
               }
            }
         }

         return null;
      }
   }

   private ServiceLocatorImpl.IgdValue igdCacheCompute(ServiceLocatorImpl.IgdCacheKey key) {
      List<SystemDescriptor<?>> candidates = this.getDescriptors(key.filter, key.onBehalfOf, true, false, true);
      ImmediateResults immediate = this.narrow(this, candidates, key.contractOrImpl, key.name, key.onBehalfOf, true, true, null, key.filter, key.qualifiers);
      NarrowResults results = immediate.getTimelessResults();
      if (!results.getErrors().isEmpty()) {
         Utilities.handleErrors(results, new LinkedList<>(this.errorHandlers));
         throw new ComputationErrorException(new ServiceLocatorImpl.IgdValue(results, immediate));
      } else {
         return new ServiceLocatorImpl.IgdValue(results, immediate);
      }
   }

   private Unqualified getEffectiveUnqualified(Unqualified givenUnqualified, boolean isIterable, Annotation[] qualifiers) {
      if (givenUnqualified != null) {
         return givenUnqualified;
      } else if (qualifiers.length > 0) {
         return null;
      } else {
         return isIterable ? null : this.defaultUnqualified;
      }
   }

   private <T> ActiveDescriptor<T> internalGetDescriptor(
      Injectee onBehalfOf, Type contractOrImpl, String name, Unqualified unqualified, boolean isIterable, Annotation... qualifiers
   ) throws MultiException {
      return this.internalGetDescriptor(onBehalfOf, contractOrImpl, name, unqualified, isIterable, false, qualifiers);
   }

   private <T> ActiveDescriptor<T> internalGetDescriptor(
      Injectee onBehalfOf,
      Type contractOrImpl,
      String name,
      Unqualified unqualified,
      boolean isIterable,
      boolean calledFromSecondChanceResolveMethod,
      Annotation... qualifiers
   ) throws MultiException {
      if (contractOrImpl == null) {
         throw new IllegalArgumentException();
      } else {
         Class<?> rawClass = ReflectionHelper.getRawClass(contractOrImpl);
         if (rawClass == null) {
            return null;
         } else {
            Utilities.checkLookupType(rawClass);
            rawClass = Utilities.translatePrimitiveType(rawClass);
            name = getName(name, qualifiers);
            NarrowResults results = null;
            LinkedList<ErrorService> currentErrorHandlers = null;
            ImmediateResults immediate = null;
            unqualified = this.getEffectiveUnqualified(unqualified, isIterable, qualifiers);
            CacheKey cacheKey = new CacheKey(contractOrImpl, name, unqualified, qualifiers);
            Filter filter = new ServiceLocatorImpl.UnqualifiedIndexedFilter(rawClass.getName(), name, unqualified);
            ServiceLocatorImpl.IgdCacheKey igdCacheKey = new ServiceLocatorImpl.IgdCacheKey(
               cacheKey, name, onBehalfOf, contractOrImpl, rawClass, qualifiers, filter
            );
            this.rLock.lock();

            try {
               ServiceLocatorImpl.IgdValue value = (ServiceLocatorImpl.IgdValue)this.igdCache.compute(igdCacheKey);
               boolean freshOne = value.freshnessKeeper.compareAndSet(1, 2);
               if (!freshOne) {
                  immediate = this.narrow(this, null, contractOrImpl, name, onBehalfOf, true, true, value.results, filter, qualifiers);
                  results = immediate.getTimelessResults();
               } else {
                  results = value.results;
                  immediate = value.immediate;
               }

               if (!results.getErrors().isEmpty()) {
                  currentErrorHandlers = new LinkedList<>(this.errorHandlers);
               }
            } finally {
               this.rLock.unlock();
            }

            if (currentErrorHandlers != null) {
               Utilities.handleErrors(results, currentErrorHandlers);
            }

            ActiveDescriptor<T> postValidateResult = immediate.getImmediateResults().isEmpty()
               ? null
               : (ActiveDescriptor)immediate.getImmediateResults().get(0);
            if (!calledFromSecondChanceResolveMethod && postValidateResult == null) {
               Injectee injectee;
               if (onBehalfOf != null) {
                  injectee = onBehalfOf;
               } else {
                  HashSet<Annotation> requiredQualifiers = new HashSet<>();
                  if (qualifiers != null && qualifiers.length > 0) {
                     for (Annotation qualifier : qualifiers) {
                        if (qualifier != null) {
                           requiredQualifiers.add(qualifier);
                        }
                     }
                  }

                  InjecteeImpl injecteeImpl = new InjecteeImpl(contractOrImpl);
                  injecteeImpl.setRequiredQualifiers(requiredQualifiers);
                  injecteeImpl.setUnqualified(unqualified);
                  injectee = injecteeImpl;
               }

               postValidateResult = (ActiveDescriptor<T>)this.secondChanceResolve(injectee);
            }

            return postValidateResult;
         }
      }
   }

   public <T> ServiceHandle<T> getServiceHandle(Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
      return this.getServiceHandle(contractOrImpl, qualifiers);
   }

   public <T> ServiceHandle<T> getServiceHandle(Type contractOrImpl, Annotation... qualifiers) throws MultiException {
      this.checkState();
      ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, null, null, false, qualifiers);
      return ad == null ? null : this.getServiceHandle(ad, new InjecteeImpl(contractOrImpl));
   }

   <T> ServiceHandle<T> getUnqualifiedServiceHandle(Type contractOrImpl, Unqualified unqualified, boolean isIterable, Annotation... qualifiers) throws MultiException {
      this.checkState();
      ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, null, unqualified, isIterable, qualifiers);
      return ad == null ? null : this.getServiceHandle(ad, new InjecteeImpl(contractOrImpl));
   }

   private List<ServiceHandle<?>> protectedGetAllServiceHandles(final Type contractOrImpl, final Annotation... qualifiers) {
      return AccessController.doPrivileged(new PrivilegedAction<List<ServiceHandle<?>>>() {
         public List<ServiceHandle<?>> run() {
            return ServiceLocatorImpl.this.getAllServiceHandles(contractOrImpl, qualifiers);
         }
      });
   }

   public <T> List<ServiceHandle<T>> getAllServiceHandles(Class<T> contractOrImpl, Annotation... qualifiers) throws MultiException {
      return (List<ServiceHandle<T>>)ReflectionHelper.cast(this.getAllServiceHandles(contractOrImpl, qualifiers));
   }

   public List<ServiceHandle<?>> getAllServiceHandles(Type contractOrImpl, Annotation... qualifiers) throws MultiException {
      return (List<ServiceHandle<?>>)this.internalGetAllServiceHandles(contractOrImpl, null, true, false, qualifiers);
   }

   List<ServiceHandle<?>> getAllUnqualifiedServiceHandles(Type contractOrImpl, Unqualified unqualified, boolean isIterable, Annotation... qualifiers) throws MultiException {
      return (List<ServiceHandle<?>>)this.internalGetAllServiceHandles(contractOrImpl, unqualified, true, isIterable, qualifiers);
   }

   private List<?> internalGetAllServiceHandles(Type contractOrImpl, Unqualified unqualified, boolean getHandles, boolean isIterable, Annotation... qualifiers) throws MultiException {
      if (contractOrImpl == null) {
         throw new IllegalArgumentException();
      } else {
         this.checkState();
         Class<?> rawClass = ReflectionHelper.getRawClass(contractOrImpl);
         if (rawClass == null) {
            throw new MultiException(new IllegalArgumentException("Type must be a class or parameterized type, it was " + contractOrImpl));
         } else {
            String name = rawClass.getName();
            NarrowResults results = null;
            LinkedList<ErrorService> currentErrorHandlers = null;
            ImmediateResults immediate = null;
            unqualified = this.getEffectiveUnqualified(unqualified, isIterable, qualifiers);
            CacheKey cacheKey = new CacheKey(contractOrImpl, null, unqualified, qualifiers);
            Filter filter = new ServiceLocatorImpl.UnqualifiedIndexedFilter(name, null, unqualified);
            ServiceLocatorImpl.IgdCacheKey igdCacheKey = new ServiceLocatorImpl.IgdCacheKey(cacheKey, name, null, contractOrImpl, rawClass, qualifiers, filter);
            this.rLock.lock();

            try {
               ServiceLocatorImpl.IgdValue value = (ServiceLocatorImpl.IgdValue)this.igashCache.compute(igdCacheKey);
               boolean freshOne = value.freshnessKeeper.compareAndSet(1, 2);
               if (!freshOne) {
                  immediate = this.narrow(this, null, contractOrImpl, null, null, false, true, value.results, filter, qualifiers);
                  results = immediate.getTimelessResults();
               } else {
                  results = value.results;
                  immediate = value.immediate;
               }

               if (!results.getErrors().isEmpty()) {
                  currentErrorHandlers = new LinkedList<>(this.errorHandlers);
               }
            } finally {
               this.rLock.unlock();
            }

            if (currentErrorHandlers != null) {
               Utilities.handleErrors(results, currentErrorHandlers);
            }

            LinkedList<Object> retVal = new LinkedList<>();

            for (ActiveDescriptor<?> candidate : immediate.getImmediateResults()) {
               if (getHandles) {
                  retVal.add(this.internalGetServiceHandle(candidate, contractOrImpl, null));
               } else {
                  Object service = Utilities.createService(candidate, null, this, null, rawClass);
                  retVal.add(service);
               }
            }

            return retVal;
         }
      }
   }

   public <T> ServiceHandle<T> getServiceHandle(Class<T> contractOrImpl, String name, Annotation... qualifiers) throws MultiException {
      return this.getServiceHandle(contractOrImpl, name, qualifiers);
   }

   public <T> ServiceHandle<T> getServiceHandle(Type contractOrImpl, String name, Annotation... qualifiers) throws MultiException {
      this.checkState();
      ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, name, null, false, qualifiers);
      return ad == null ? null : this.internalGetServiceHandle(ad, contractOrImpl, null);
   }

   public List<ServiceHandle<?>> getAllServiceHandles(Filter searchCriteria) throws MultiException {
      this.checkState();
      LinkedList<ErrorService> currentErrorHandlers = null;
      List<SystemDescriptor<?>> candidates = (List<SystemDescriptor<?>>)ReflectionHelper.cast(this.getDescriptors(searchCriteria));
      ImmediateResults immediate = this.narrow(this, candidates, null, null, null, false, false, null, searchCriteria);
      NarrowResults results = immediate.getTimelessResults();
      if (!results.getErrors().isEmpty()) {
         currentErrorHandlers = new LinkedList<>(this.errorHandlers);
      }

      if (currentErrorHandlers != null) {
         Utilities.handleErrors(results, currentErrorHandlers);
      }

      SortedSet<ServiceHandle<?>> retVal = new TreeSet<>(HANDLE_COMPARATOR);

      for (ActiveDescriptor<?> candidate : results.getResults()) {
         retVal.add(this.getServiceHandle(candidate));
      }

      return new LinkedList<>(retVal);
   }

   public List<ServiceHandle<?>> getAllServiceHandles(Annotation qualifier, Annotation... qualifiers) throws MultiException {
      this.checkState();
      if (qualifier == null) {
         throw new IllegalArgumentException("qualifier is null");
      } else {
         final Set<String> allQualifiers = new LinkedHashSet<>();
         allQualifiers.add(qualifier.annotationType().getName());

         for (Annotation anno : qualifiers) {
            String addMe = anno.annotationType().getName();
            if (allQualifiers.contains(addMe)) {
               throw new IllegalArgumentException("Multiple qualifiers with name " + addMe);
            }

            allQualifiers.add(addMe);
         }

         return this.getAllServiceHandles(new Filter() {
            public boolean matches(Descriptor d) {
               return d.getQualifiers().containsAll(allQualifiers);
            }
         });
      }
   }

   List<InterceptionService> getInterceptionServices() {
      if (!this.hasInterceptionServices) {
         return null;
      } else {
         this.rLock.lock();

         LinkedList var1;
         try {
            var1 = new LinkedList<>(this.interceptionServices);
         } finally {
            this.rLock.unlock();
         }

         return var1;
      }
   }

   private ServiceLocatorImpl.CheckConfigurationData checkConfiguration(DynamicConfigurationImpl dci) {
      List<SystemDescriptor<?>> retVal = new LinkedList<>();
      boolean addOrRemoveOfInstanceListener = false;
      boolean addOrRemoveOfInjectionResolver = false;
      boolean addOrRemoveOfErrorHandler = false;
      boolean addOrRemoveOfClazzAnalyzer = false;
      boolean addOrRemoveOfConfigListener = false;
      boolean addOrRemoveOfInterceptionService = false;
      HashSet<String> affectedContracts = new HashSet<>();
      TwoPhaseTransactionDataImpl transactionData = new TwoPhaseTransactionDataImpl();

      for (Filter unbindFilter : dci.getUnbindFilters()) {
         for (SystemDescriptor<?> candidate : this.getDescriptors(unbindFilter, null, false, false, true)) {
            affectedContracts.addAll(getAllContracts(candidate));
            if (!retVal.contains(candidate)) {
               for (ValidationService vs : this.getAllValidators()) {
                  if (!this.callValidate(vs, new ValidationInformationImpl(Operation.UNBIND, candidate))) {
                     throw new MultiException(new IllegalArgumentException("Descriptor " + candidate + " did not pass the UNBIND validation"));
                  }
               }

               if (candidate.getAdvertisedContracts().contains(InstanceLifecycleListener.class.getName())) {
                  addOrRemoveOfInstanceListener = true;
               }

               if (candidate.getAdvertisedContracts().contains(InjectionResolver.class.getName())) {
                  addOrRemoveOfInjectionResolver = true;
               }

               if (candidate.getAdvertisedContracts().contains(ErrorService.class.getName())) {
                  addOrRemoveOfErrorHandler = true;
               }

               if (candidate.getAdvertisedContracts().contains(ClassAnalyzer.class.getName())) {
                  addOrRemoveOfClazzAnalyzer = true;
               }

               if (candidate.getAdvertisedContracts().contains(DynamicConfigurationListener.class.getName())) {
                  addOrRemoveOfConfigListener = true;
               }

               if (candidate.getAdvertisedContracts().contains(InterceptionService.class.getName())) {
                  addOrRemoveOfInterceptionService = true;
               }

               retVal.add(candidate);
               transactionData.toRemove(candidate);
            }
         }
      }

      for (SystemDescriptor<?> sd : dci.getAllDescriptors()) {
         transactionData.toAdd(sd);
         affectedContracts.addAll(getAllContracts(sd));
         boolean checkScope = false;
         if (sd.getAdvertisedContracts().contains(ValidationService.class.getName())
            || sd.getAdvertisedContracts().contains(ErrorService.class.getName())
            || sd.getAdvertisedContracts().contains(InterceptionService.class.getName())
            || sd.getAdvertisedContracts().contains(InstanceLifecycleListener.class.getName())) {
            this.reifyDescriptor(sd);
            checkScope = true;
            if (sd.getAdvertisedContracts().contains(ErrorService.class.getName())) {
               addOrRemoveOfErrorHandler = true;
            }

            if (sd.getAdvertisedContracts().contains(InstanceLifecycleListener.class.getName())) {
               addOrRemoveOfInstanceListener = true;
            }

            if (sd.getAdvertisedContracts().contains(InterceptionService.class.getName())) {
               addOrRemoveOfInterceptionService = true;
            }
         }

         if (sd.getAdvertisedContracts().contains(InjectionResolver.class.getName())) {
            this.reifyDescriptor(sd);
            checkScope = true;
            if (Utilities.getInjectionResolverType(sd) == null) {
               throw new MultiException(
                  new IllegalArgumentException("An implementation of InjectionResolver must be a parameterized type and the actual type must be an annotation")
               );
            }

            addOrRemoveOfInjectionResolver = true;
         }

         if (sd.getAdvertisedContracts().contains(DynamicConfigurationListener.class.getName())) {
            this.reifyDescriptor(sd);
            checkScope = true;
            addOrRemoveOfConfigListener = true;
         }

         if (sd.getAdvertisedContracts().contains(Context.class.getName())) {
            checkScope = true;
         }

         if (sd.getAdvertisedContracts().contains(ClassAnalyzer.class.getName())) {
            addOrRemoveOfClazzAnalyzer = true;
         }

         if (checkScope) {
            String scope = sd.getScope() == null ? PerLookup.class.getName() : sd.getScope();
            if (!scope.equals(Singleton.class.getName())) {
               throw new MultiException(new IllegalArgumentException("The implementation class " + sd.getImplementation() + " must be in the Singleton scope"));
            }
         }

         for (ValidationService vsx : this.getAllValidators()) {
            Validator validator = vsx.getValidator();
            if (validator == null) {
               throw new MultiException(new IllegalArgumentException("Validator was null from validation service" + vsx));
            }

            if (!this.callValidate(vsx, new ValidationInformationImpl(Operation.BIND, sd))) {
               throw new MultiException(new IllegalArgumentException("Descriptor " + sd + " did not pass the BIND validation"));
            }
         }
      }

      List<Filter> idempotentFilters = dci.getIdempotentFilters();
      if (!idempotentFilters.isEmpty()) {
         List<ActiveDescriptor<?>> allValidatedDescriptors = this.getDescriptors(BuilderHelper.allFilter());
         List<Throwable> idempotentFailures = new LinkedList<>();

         for (ActiveDescriptor<?> aValidatedDescriptor : allValidatedDescriptors) {
            for (Filter idempotentFilter : idempotentFilters) {
               if (BuilderHelper.filterMatches(aValidatedDescriptor, idempotentFilter)) {
                  idempotentFailures.add(new DuplicateServiceException(aValidatedDescriptor, this.locatorName));
               }
            }
         }

         if (!idempotentFailures.isEmpty()) {
            throw new MultiException(idempotentFailures);
         }
      }

      LinkedList<TwoPhaseResource> resources = dci.getResources();
      List<TwoPhaseResource> completedPrepares = new LinkedList<>();

      for (TwoPhaseResource resource : resources) {
         try {
            resource.prepareDynamicConfiguration(transactionData);
            completedPrepares.add(resource);
         } catch (Throwable var21) {
            for (TwoPhaseResource rollMe : completedPrepares) {
               try {
                  rollMe.rollbackDynamicConfiguration(transactionData);
               } catch (Throwable var20) {
                  Logger.getLogger().debug("Rollback of TwoPhaseResource " + resource + " failed with exception", var20);
               }
            }

            if (var21 instanceof RuntimeException) {
               throw (RuntimeException)var21;
            }

            throw new RuntimeException(var21);
         }
      }

      return new ServiceLocatorImpl.CheckConfigurationData(
         retVal,
         addOrRemoveOfInstanceListener,
         addOrRemoveOfInjectionResolver,
         addOrRemoveOfErrorHandler,
         addOrRemoveOfClazzAnalyzer,
         addOrRemoveOfConfigListener,
         affectedContracts,
         addOrRemoveOfInterceptionService,
         transactionData
      );
   }

   private static List<String> getAllContracts(ActiveDescriptor<?> desc) {
      LinkedList<String> allContracts = new LinkedList<>(desc.getAdvertisedContracts());
      allContracts.addAll(desc.getQualifiers());
      String scope = desc.getScope() == null ? PerLookup.class.getName() : desc.getScope();
      allContracts.add(scope);
      return allContracts;
   }

   private void removeConfigurationInternal(List<SystemDescriptor<?>> unbinds) {
      for (SystemDescriptor<?> unbind : unbinds) {
         if (BIND_TRACING_PATTERN != null && doTrace(unbind)) {
            Logger.getLogger().debug("HK2 Bind Tracing: Removing Descriptor " + unbind);
            if (BIND_TRACING_STACKS) {
               Logger.getLogger().debug("ServiceLocatorImpl", "removeConfigurationInternal", new Throwable());
            }
         }

         this.allDescriptors.removeDescriptor(unbind);

         for (String advertisedContract : getAllContracts(unbind)) {
            IndexedListData ild = this.descriptorsByAdvertisedContract.get(advertisedContract);
            if (ild != null) {
               ild.removeDescriptor(unbind);
               if (ild.isEmpty()) {
                  this.descriptorsByAdvertisedContract.remove(advertisedContract);
               }
            }
         }

         String unbindName = unbind.getName();
         if (unbindName != null) {
            IndexedListData ild = this.descriptorsByName.get(unbindName);
            if (ild != null) {
               ild.removeDescriptor(unbind);
               if (ild.isEmpty()) {
                  this.descriptorsByName.remove(unbindName);
               }
            }
         }

         if (unbind.getAdvertisedContracts().contains(ValidationService.class.getName())) {
            ServiceHandle<ValidationService> handle = this.getServiceHandle((ActiveDescriptor<ValidationService>)unbind);
            ValidationService vs = (ValidationService)handle.getService();
            this.allValidators.remove(vs);
         }

         if (unbind.isReified()) {
            for (Injectee injectee : unbind.getInjectees()) {
               if (injectee instanceof SystemInjecteeImpl) {
                  this.injecteeToResolverCache.remove((SystemInjecteeImpl)injectee);
               }
            }

            this.classReflectionHelper.clean(unbind.getImplementationClass());
         }
      }

      boolean hasOneUnbind = false;

      for (SystemDescriptor<?> unbind : unbinds) {
         hasOneUnbind = true;
         unbind.close();
      }

      if (hasOneUnbind) {
         this.perLocatorUtilities.releaseCaches();
      }
   }

   private static boolean doTrace(ActiveDescriptor<?> desc) {
      if (BIND_TRACING_PATTERN == null) {
         return false;
      } else if ("*".equals(BIND_TRACING_PATTERN)) {
         return true;
      } else if (desc.getImplementation() == null) {
         return true;
      } else {
         StringTokenizer st = new StringTokenizer(BIND_TRACING_PATTERN, "|");

         while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (desc.getImplementation().contains(token)) {
               return true;
            }

            for (String contract : desc.getAdvertisedContracts()) {
               if (contract.contains(token)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private List<SystemDescriptor<?>> addConfigurationInternal(DynamicConfigurationImpl dci) {
      List<SystemDescriptor<?>> thingsAdded = new LinkedList<>();

      for (SystemDescriptor<?> sd : dci.getAllDescriptors()) {
         if (BIND_TRACING_PATTERN != null && doTrace(sd)) {
            Logger.getLogger().debug("HK2 Bind Tracing: Adding Descriptor " + sd);
            if (BIND_TRACING_STACKS) {
               Logger.getLogger().debug("ServiceLocatorImpl", "addConfigurationInternal", new Throwable());
            }
         }

         thingsAdded.add(sd);
         this.allDescriptors.addDescriptor(sd);

         for (String advertisedContract : getAllContracts(sd)) {
            IndexedListData ild = this.descriptorsByAdvertisedContract.get(advertisedContract);
            if (ild == null) {
               ild = new IndexedListData();
               this.descriptorsByAdvertisedContract.put(advertisedContract, ild);
            }

            ild.addDescriptor(sd);
         }

         if (sd.getName() != null) {
            String name = sd.getName();
            IndexedListData ild = this.descriptorsByName.get(name);
            if (ild == null) {
               ild = new IndexedListData();
               this.descriptorsByName.put(name, ild);
            }

            ild.addDescriptor(sd);
         }

         if (sd.getAdvertisedContracts().contains(ValidationService.class.getName())) {
            ServiceHandle<ValidationService> handle = this.getServiceHandle((ActiveDescriptor<ValidationService>)sd);
            ValidationService vs = (ValidationService)handle.getService();
            this.allValidators.add(vs);
         }
      }

      return thingsAdded;
   }

   private void reupInjectionResolvers() {
      HashMap<Class<? extends Annotation>, InjectionResolver<?>> newResolvers = new HashMap<>();
      Filter injectionResolverFilter = BuilderHelper.createContractFilter(InjectionResolver.class.getName());

      for (ActiveDescriptor<?> resolverDescriptor : this.protectedGetDescriptors(injectionResolverFilter)) {
         Class<? extends Annotation> iResolve = Utilities.getInjectionResolverType(resolverDescriptor);
         if (iResolve != null && !newResolvers.containsKey(iResolve)) {
            InjectionResolver<?> resolver = (InjectionResolver<?>)this.getServiceHandle(resolverDescriptor).getService();
            newResolvers.put(iResolve, resolver);
         }
      }

      synchronized (this.allResolvers) {
         this.allResolvers.clear();
         this.allResolvers.putAll(newResolvers);
      }

      this.injecteeToResolverCache.clear();
   }

   private void reupInterceptionServices() {
      List<InterceptionService> allInterceptionServices = this.protectedGetAllServices(InterceptionService.class);
      this.interceptionServices.clear();
      this.interceptionServices.addAll(allInterceptionServices);
      this.hasInterceptionServices = !this.interceptionServices.isEmpty();
   }

   private void reupErrorHandlers() {
      List<ErrorService> allErrorServices = this.protectedGetAllServices(ErrorService.class);
      this.errorHandlers.clear();
      this.errorHandlers.addAll(allErrorServices);
   }

   private void reupConfigListeners() {
      List<ServiceHandle<?>> allConfigListeners = this.protectedGetAllServiceHandles(DynamicConfigurationListener.class);
      this.configListeners.clear();
      this.configListeners.addAll(allConfigListeners);
   }

   private void reupInstanceListenersHandlers(Collection<SystemDescriptor<?>> checkList) {
      List<InstanceLifecycleListener> allLifecycleListeners = this.protectedGetAllServices(InstanceLifecycleListener.class);

      for (SystemDescriptor<?> descriptor : checkList) {
         descriptor.reupInstanceListeners(allLifecycleListeners);
      }
   }

   private void reupClassAnalyzers() {
      List<ServiceHandle<?>> allAnalyzers = this.protectedGetAllServiceHandles(ClassAnalyzer.class);
      synchronized (this.classAnalyzerLock) {
         this.classAnalyzers.clear();

         for (ServiceHandle<?> handle : allAnalyzers) {
            ActiveDescriptor<?> descriptor = handle.getActiveDescriptor();
            String name = descriptor.getName();
            if (name != null) {
               ClassAnalyzer created = (ClassAnalyzer)handle.getService();
               if (created != null) {
                  this.classAnalyzers.put(name, created);
               }
            }
         }
      }
   }

   private void reupCache(HashSet<String> affectedContracts) {
      this.wLock.lock();

      try {
         for (String affectedContract : affectedContracts) {
            final String fAffectedContract = affectedContract;
            CacheKeyFilter<ServiceLocatorImpl.IgdCacheKey> cacheKeyFilter = new CacheKeyFilter<ServiceLocatorImpl.IgdCacheKey>() {
               public boolean matches(ServiceLocatorImpl.IgdCacheKey key) {
                  return key.cacheKey.matchesRemovalName(fAffectedContract);
               }
            };
            this.igdCache.releaseMatching(cacheKeyFilter);
            this.igashCache.releaseMatching(cacheKeyFilter);
         }
      } finally {
         this.wLock.unlock();
      }
   }

   private void reup(
      List<SystemDescriptor<?>> thingsAdded,
      boolean instanceListenersModified,
      boolean injectionResolversModified,
      boolean errorHandlersModified,
      boolean classAnalyzersModified,
      boolean dynamicConfigurationListenersModified,
      HashSet<String> affectedContracts,
      boolean interceptionServicesModified
   ) {
      this.reupCache(affectedContracts);
      if (injectionResolversModified) {
         this.reupInjectionResolvers();
      }

      if (errorHandlersModified) {
         this.reupErrorHandlers();
      }

      if (dynamicConfigurationListenersModified) {
         this.reupConfigListeners();
      }

      if (instanceListenersModified) {
         this.reupInstanceListenersHandlers(this.allDescriptors.getSortedList());
      } else {
         this.reupInstanceListenersHandlers(thingsAdded);
      }

      if (classAnalyzersModified) {
         this.reupClassAnalyzers();
      }

      if (interceptionServicesModified) {
         this.reupInterceptionServices();
      }

      this.contextCache.clear();
   }

   private void getAllChildren(LinkedList<ServiceLocatorImpl> allMyChildren) {
      LinkedList<ServiceLocatorImpl> addMe;
      synchronized (this.children) {
         addMe = new LinkedList<>(this.children.keySet());
      }

      allMyChildren.addAll(addMe);

      for (ServiceLocatorImpl sli : addMe) {
         sli.getAllChildren(allMyChildren);
      }
   }

   private void callAllConfigurationListeners(List<ServiceHandle<?>> allListeners) {
      if (allListeners != null) {
         for (ServiceHandle<?> listener : allListeners) {
            ActiveDescriptor<?> listenerDescriptor = listener.getActiveDescriptor();
            if (listenerDescriptor.getLocatorId() == this.id) {
               try {
                  ((DynamicConfigurationListener)listener.getService()).configurationChanged();
               } catch (Throwable var6) {
               }
            }
         }
      }
   }

   void addConfiguration(DynamicConfigurationImpl dci) {
      List<ServiceHandle<?>> allConfigurationListeners = null;
      MultiException configurationError = null;
      this.wLock.lock();

      ServiceLocatorImpl.CheckConfigurationData checkData;
      try {
         checkData = this.checkConfiguration(dci);
         this.removeConfigurationInternal(checkData.getUnbinds());
         List<SystemDescriptor<?>> thingsAdded = this.addConfigurationInternal(dci);
         this.reup(
            thingsAdded,
            checkData.getInstanceLifecycleModificationsMade(),
            checkData.getInjectionResolverModificationMade(),
            checkData.getErrorHandlerModificationMade(),
            checkData.getClassAnalyzerModificationMade(),
            checkData.getDynamicConfigurationListenerModificationMade(),
            checkData.getAffectedContracts(),
            checkData.getInterceptionServiceModificationMade()
         );
         allConfigurationListeners = new LinkedList<>(this.configListeners);
      } catch (MultiException var21) {
         configurationError = var21;
         throw var21;
      } finally {
         List<ErrorService> errorServices = null;
         if (configurationError != null) {
            errorServices = new LinkedList<>(this.errorHandlers);
         }

         this.wLock.unlock();
         if (errorServices != null && !errorServices.isEmpty()) {
            for (ErrorService errorService : errorServices) {
               try {
                  errorService.onFailure(new ErrorInformationImpl(ErrorType.DYNAMIC_CONFIGURATION_FAILURE, null, null, configurationError));
               } catch (Throwable var19) {
               }
            }
         }
      }

      LinkedList<ServiceLocatorImpl> allMyChildren = new LinkedList<>();
      this.getAllChildren(allMyChildren);

      for (ServiceLocatorImpl sli : allMyChildren) {
         sli.reupCache(checkData.getAffectedContracts());
      }

      this.callAllConfigurationListeners(allConfigurationListeners);

      for (TwoPhaseResource resource : dci.getResources()) {
         try {
            resource.activateDynamicConfiguration(checkData.getTransactionData());
         } catch (Throwable var20) {
            Logger.getLogger().debug("Activate of TwoPhaseResource " + resource + " failed with exception", var20);
         }
      }
   }

   boolean isInjectAnnotation(Annotation annotation) {
      return this.allResolvers.containsKey(annotation.annotationType());
   }

   boolean isInjectAnnotation(Annotation annotation, boolean isConstructor) {
      InjectionResolver<?> resolver = this.allResolvers.get(annotation.annotationType());
      if (resolver == null) {
         return false;
      } else {
         return isConstructor ? resolver.isConstructorParameterIndicator() : resolver.isMethodParameterIndicator();
      }
   }

   InjectionResolver<?> getInjectionResolver(Class<? extends Annotation> annoType) {
      return this.allResolvers.get(annoType);
   }

   private Context<?> _resolveContext(Class<? extends Annotation> scope) throws IllegalStateException {
      Context<?> retVal = null;
      Type[] actuals = new Type[]{scope};
      ParameterizedType findContext = new ParameterizedTypeImpl(Context.class, actuals);

      for (ServiceHandle<Context<?>> contextHandle : (List)ReflectionHelper.cast(this.protectedGetAllServiceHandles(findContext))) {
         Context<?> context = (Context<?>)contextHandle.getService();
         if (context.isActive()) {
            if (retVal != null) {
               throw new IllegalStateException("There is more than one active context for " + scope.getName());
            }

            retVal = context;
         }
      }

      if (retVal == null) {
         throw new IllegalStateException("Could not find an active context for " + scope.getName());
      } else {
         return retVal;
      }
   }

   Context<?> resolveContext(Class<? extends Annotation> scope) throws IllegalStateException {
      if (scope.equals(Singleton.class)) {
         return this.singletonContext;
      } else if (scope.equals(PerLookup.class)) {
         return this.perLookupContext;
      } else {
         Context<?> retVal = (Context<?>)this.contextCache.compute(scope);
         if (retVal.isActive()) {
            return retVal;
         } else {
            this.contextCache.remove(scope);
            return (Context<?>)this.contextCache.compute(scope);
         }
      }
   }

   private Class<?> loadClass(Descriptor descriptor, Injectee injectee) {
      if (descriptor == null) {
         throw new IllegalArgumentException();
      } else {
         HK2Loader loader = descriptor.getLoader();
         if (loader == null) {
            return Utilities.loadClass(descriptor.getImplementation(), injectee);
         } else {
            try {
               return loader.loadClass(descriptor.getImplementation());
            } catch (MultiException var7) {
               var7.addError(new IllegalStateException("Could not load descriptor " + descriptor));
               throw var7;
            } catch (Throwable var8) {
               MultiException me = new MultiException(var8);
               me.addError(new IllegalStateException("Could not load descriptor " + descriptor));
               throw me;
            }
         }
      }
   }

   private ImmediateResults narrow(
      ServiceLocator locator,
      List<SystemDescriptor<?>> candidates,
      Type requiredType,
      String name,
      Injectee injectee,
      boolean onlyOne,
      boolean doValidation,
      NarrowResults cachedResults,
      Filter filter,
      Annotation... qualifiers
   ) {
      ImmediateResults retVal = new ImmediateResults(cachedResults);
      cachedResults = retVal.getTimelessResults();
      if (candidates != null) {
         List<ActiveDescriptor<?>> lCandidates = (List<ActiveDescriptor<?>>)ReflectionHelper.cast(candidates);
         cachedResults.setUnnarrowedResults(lCandidates);
      }

      Set<Annotation> requiredAnnotations = Utilities.fixAndCheckQualifiers(qualifiers, name);

      for (ActiveDescriptor<?> previousResult : cachedResults.getResults()) {
         if (!doValidation || this.validate((SystemDescriptor<?>)previousResult, injectee, filter)) {
            retVal.addValidatedResult(previousResult);
            if (onlyOne) {
               return retVal;
            }
         }
      }

      if (requiredType != null && requiredType instanceof Class && ((Class)requiredType).isAnnotation()) {
         requiredType = null;
      }

      while (true) {
         boolean safe;
         ActiveDescriptor<?> candidate;
         do {
            while (true) {
               if ((candidate = cachedResults.removeUnnarrowedResult()) == null) {
                  return retVal;
               }

               boolean doReify = false;
               if ((requiredType != null || !requiredAnnotations.isEmpty()) && !candidate.isReified()) {
                  doReify = true;
               }

               if (!doReify) {
                  break;
               }

               try {
                  candidate = locator.reifyDescriptor(candidate, injectee);
                  break;
               } catch (MultiException var18) {
                  cachedResults.addError(candidate, injectee, var18);
               } catch (Throwable var19) {
                  cachedResults.addError(candidate, injectee, new MultiException(var19));
               }
            }

            if (requiredType == null) {
               break;
            }

            safe = false;

            for (Type candidateType : candidate.getContractTypes()) {
               if (Utilities.isTypeSafe(requiredType, candidateType)) {
                  safe = true;
                  break;
               }
            }
         } while (!safe);

         if (!requiredAnnotations.isEmpty()) {
            Set<Annotation> candidateAnnotations = candidate.getQualifierAnnotations();
            if (!ReflectionHelper.annotationContainsAll(candidateAnnotations, requiredAnnotations)) {
               continue;
            }
         }

         cachedResults.addGoodResult(candidate);
         if (!doValidation || this.validate((SystemDescriptor<?>)candidate, injectee, filter)) {
            retVal.addValidatedResult(candidate);
            if (onlyOne) {
               return retVal;
            }
         }
      }
   }

   public long getLocatorId() {
      return this.id;
   }

   long getNextServiceId() {
      return this.nextServiceId.getAndIncrement();
   }

   private void addChild(ServiceLocatorImpl child) {
      synchronized (this.children) {
         this.children.put(child, null);
      }
   }

   private void removeChild(ServiceLocatorImpl child) {
      synchronized (this.children) {
         this.children.remove(child);
      }
   }

   private void checkState() {
      if (ServiceLocatorState.SHUTDOWN.equals(this.state)) {
         throw new IllegalStateException(this + " has been shut down");
      }
   }

   private LinkedHashSet<ValidationService> getAllValidators() {
      if (this.parent == null) {
         return this.allValidators;
      } else {
         LinkedHashSet<ValidationService> retVal = new LinkedHashSet<>();
         retVal.addAll(this.parent.getAllValidators());
         retVal.addAll(this.allValidators);
         return retVal;
      }
   }

   public String getDefaultClassAnalyzerName() {
      synchronized (this.classAnalyzerLock) {
         return this.defaultClassAnalyzer;
      }
   }

   public void setDefaultClassAnalyzerName(String defaultClassAnalyzer) {
      synchronized (this.classAnalyzerLock) {
         if (defaultClassAnalyzer == null) {
            this.defaultClassAnalyzer = "default";
         } else {
            this.defaultClassAnalyzer = defaultClassAnalyzer;
         }
      }
   }

   public Unqualified getDefaultUnqualified() {
      this.rLock.lock();

      Unqualified var1;
      try {
         var1 = this.defaultUnqualified;
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   public void setDefaultUnqualified(Unqualified unqualified) {
      this.wLock.lock();

      try {
         this.defaultUnqualified = unqualified;
      } finally {
         this.wLock.unlock();
      }
   }

   ClassAnalyzer getAnalyzer(String name, Collector collector) {
      ClassAnalyzer retVal;
      synchronized (this.classAnalyzerLock) {
         if (name == null) {
            name = this.defaultClassAnalyzer;
         }

         retVal = this.classAnalyzers.get(name);
      }

      if (retVal == null) {
         collector.addThrowable(new IllegalStateException("Could not find an implementation of ClassAnalyzer with name " + name));
         return null;
      } else {
         return retVal;
      }
   }

   public ServiceLocator getParent() {
      return this.parent;
   }

   public boolean getNeutralContextClassLoader() {
      return this.neutralContextClassLoader;
   }

   public void setNeutralContextClassLoader(boolean neutralContextClassLoader) {
      this.wLock.lock();

      try {
         this.neutralContextClassLoader = neutralContextClassLoader;
      } finally {
         this.wLock.unlock();
      }
   }

   private ServiceLocatorImpl getMe() {
      return this;
   }

   boolean hasInjectAnnotation(AnnotatedElement annotated) {
      return this.perLocatorUtilities.hasInjectAnnotation(annotated);
   }

   InjectionResolver<?> getInjectionResolverForInjectee(SystemInjecteeImpl injectee) {
      return (InjectionResolver<?>)this.injecteeToResolverCache.compute(injectee);
   }

   ClassReflectionHelper getClassReflectionHelper() {
      return this.classReflectionHelper;
   }

   LinkedList<ErrorService> getErrorHandlers() {
      this.rLock.lock();

      LinkedList var1;
      try {
         var1 = new LinkedList<>(this.errorHandlers);
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   PerLocatorUtilities getPerLocatorUtilities() {
      return this.perLocatorUtilities;
   }

   int getNumberOfDescriptors() {
      this.rLock.lock();

      int var1;
      try {
         var1 = this.allDescriptors.size();
      } finally {
         this.rLock.unlock();
      }

      return var1;
   }

   int getNumberOfChildren() {
      return this.children.size();
   }

   int getServiceCacheSize() {
      return this.igdCache.getValueSize();
   }

   int getServiceCacheMaximumSize() {
      return this.igdCache.getMaxSize();
   }

   void clearServiceCache() {
      this.igdCache.clear();
   }

   int getReflectionCacheSize() {
      return this.classReflectionHelper.size();
   }

   void clearReflectionCache() {
      this.wLock.lock();

      try {
         this.classReflectionHelper.dispose();
      } finally {
         this.wLock.unlock();
      }
   }

   int unsortIndexes(int newRank, SystemDescriptor<?> desc, Set<IndexedListData> myLists) {
      this.wLock.lock();

      int var10;
      try {
         int retVal = desc.setRankWithLock(newRank);

         for (IndexedListData myList : myLists) {
            myList.unSort();
         }

         var10 = retVal;
      } finally {
         this.wLock.unlock();
      }

      return var10;
   }

   @Override
   public String toString() {
      return "ServiceLocatorImpl(" + this.locatorName + "," + this.id + "," + System.identityHashCode(this) + ")";
   }

   private static class CheckConfigurationData {
      private final List<SystemDescriptor<?>> unbinds;
      private final boolean instanceLifeycleModificationMade;
      private final boolean injectionResolverModificationMade;
      private final boolean errorHandlerModificationMade;
      private final boolean classAnalyzerModificationMade;
      private final boolean dynamicConfigurationListenerModificationMade;
      private final HashSet<String> affectedContracts;
      private final boolean interceptionServiceModificationMade;
      private final TwoPhaseTransactionData transactionData;

      private CheckConfigurationData(
         List<SystemDescriptor<?>> unbinds,
         boolean instanceLifecycleModificationMade,
         boolean injectionResolverModificationMade,
         boolean errorHandlerModificationMade,
         boolean classAnalyzerModificationMade,
         boolean dynamicConfigurationListenerModificationMade,
         HashSet<String> affectedContracts,
         boolean interceptionServiceModificationMade,
         TwoPhaseTransactionData transactionData
      ) {
         this.unbinds = unbinds;
         this.instanceLifeycleModificationMade = instanceLifecycleModificationMade;
         this.injectionResolverModificationMade = injectionResolverModificationMade;
         this.errorHandlerModificationMade = errorHandlerModificationMade;
         this.classAnalyzerModificationMade = classAnalyzerModificationMade;
         this.dynamicConfigurationListenerModificationMade = dynamicConfigurationListenerModificationMade;
         this.affectedContracts = affectedContracts;
         this.interceptionServiceModificationMade = interceptionServiceModificationMade;
         this.transactionData = transactionData;
      }

      private List<SystemDescriptor<?>> getUnbinds() {
         return this.unbinds;
      }

      private boolean getInstanceLifecycleModificationsMade() {
         return this.instanceLifeycleModificationMade;
      }

      private boolean getInjectionResolverModificationMade() {
         return this.injectionResolverModificationMade;
      }

      private boolean getErrorHandlerModificationMade() {
         return this.errorHandlerModificationMade;
      }

      private boolean getClassAnalyzerModificationMade() {
         return this.classAnalyzerModificationMade;
      }

      private boolean getDynamicConfigurationListenerModificationMade() {
         return this.dynamicConfigurationListenerModificationMade;
      }

      private HashSet<String> getAffectedContracts() {
         return this.affectedContracts;
      }

      private boolean getInterceptionServiceModificationMade() {
         return this.interceptionServiceModificationMade;
      }

      private TwoPhaseTransactionData getTransactionData() {
         return this.transactionData;
      }
   }

   private static final class IgdCacheKey {
      private final CacheKey cacheKey;
      private final String name;
      private final Injectee onBehalfOf;
      private final Type contractOrImpl;
      private final Annotation[] qualifiers;
      private final Filter filter;
      private final int hashCode;

      IgdCacheKey(CacheKey key, String name, Injectee onBehalfOf, Type contractOrImpl, Class<?> rawClass, Annotation[] qualifiers, Filter filter) {
         this.cacheKey = key;
         this.name = name;
         this.onBehalfOf = onBehalfOf;
         this.contractOrImpl = contractOrImpl;
         this.qualifiers = qualifiers;
         this.filter = filter;
         int hash = 5;
         hash = 41 * hash + this.cacheKey.hashCode();
         this.hashCode = hash;
      }

      @Override
      public int hashCode() {
         return this.hashCode;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (!(obj instanceof ServiceLocatorImpl.IgdCacheKey)) {
            return false;
         } else {
            ServiceLocatorImpl.IgdCacheKey other = (ServiceLocatorImpl.IgdCacheKey)obj;
            if (this.hashCode != other.hashCode) {
               return false;
            } else {
               return this.cacheKey == null ? other.cacheKey == null : this.cacheKey.equals(other.cacheKey);
            }
         }
      }

      @Override
      public String toString() {
         return "IgdCacheKey("
            + this.cacheKey
            + ","
            + this.name
            + ","
            + this.onBehalfOf
            + ","
            + this.contractOrImpl
            + ","
            + Arrays.toString((Object[])this.qualifiers)
            + ","
            + this.filter
            + ","
            + this.filter
            + ","
            + System.identityHashCode(this)
            + ")";
      }
   }

   private class IgdValue {
      final NarrowResults results;
      final ImmediateResults immediate;
      final AtomicInteger freshnessKeeper = new AtomicInteger(1);

      public IgdValue(NarrowResults results, ImmediateResults immediate) {
         this.results = results;
         this.immediate = immediate;
      }
   }

   private static class UnqualifiedIndexedFilter implements IndexedFilter {
      private final String contract;
      private final String name;
      private final Unqualified unqualified;

      private UnqualifiedIndexedFilter(String contract, String name, Unqualified unqualified) {
         this.contract = contract;
         this.name = name;
         this.unqualified = unqualified;
      }

      public boolean matches(Descriptor d) {
         if (this.unqualified == null) {
            return true;
         } else {
            Class<? extends Annotation>[] unqualifiedAnnos = this.unqualified.value();
            if (unqualifiedAnnos.length <= 0) {
               return d.getQualifiers().isEmpty();
            } else {
               Set<String> notAllowed = new HashSet<>();

               for (Class<? extends Annotation> notMe : unqualifiedAnnos) {
                  notAllowed.add(notMe.getName());
               }

               for (String qualifier : d.getQualifiers()) {
                  if (notAllowed.contains(qualifier)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      public String getAdvertisedContract() {
         return this.contract;
      }

      public String getName() {
         return this.name;
      }

      @Override
      public String toString() {
         return "UnqualifiedIndexFilter(" + this.contract + "," + this.name + "," + this.unqualified + "," + System.identityHashCode(this) + ")";
      }
   }
}
