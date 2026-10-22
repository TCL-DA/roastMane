package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.Proxiable;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Unproxiable;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.utilities.reflection.ScopeInfo;

public class SystemDescriptor<T> implements ActiveDescriptor<T>, Closeable {
   private final Descriptor baseDescriptor;
   private final Long id;
   private final ActiveDescriptor<T> activeDescriptor;
   private final ServiceLocatorImpl sdLocator;
   private volatile boolean reified;
   private boolean reifying = false;
   private boolean preAnalyzed = false;
   private volatile boolean closed = false;
   private final Object cacheLock = new Object();
   private boolean cacheSet = false;
   private T cachedValue;
   private Class<?> implClass;
   private Annotation scopeAnnotation;
   private Class<? extends Annotation> scope;
   private Set<Type> contracts;
   private Set<Annotation> qualifiers;
   private Creator<T> creator;
   private Long factoryLocatorId;
   private Long factoryServiceId;
   private Type implType;
   private final HashMap<ValidationService, Boolean> validationServiceCache = new HashMap<>();
   private final List<InstanceLifecycleListener> instanceListeners = new LinkedList<>();
   private final Set<IndexedListData> myLists = new HashSet<>();
   private int singletonGeneration = Integer.MAX_VALUE;

   SystemDescriptor(Descriptor baseDescriptor, boolean requiresDeepCopy, ServiceLocatorImpl locator, Long serviceId) {
      if (requiresDeepCopy) {
         this.baseDescriptor = BuilderHelper.deepCopyDescriptor(baseDescriptor);
      } else {
         this.baseDescriptor = baseDescriptor;
      }

      this.sdLocator = locator;
      this.id = serviceId;
      if (baseDescriptor instanceof ActiveDescriptor) {
         ActiveDescriptor<T> active = (ActiveDescriptor<T>)baseDescriptor;
         if (active.isReified()) {
            this.activeDescriptor = active;
            this.reified = true;
            if (active instanceof AutoActiveDescriptor) {
               ((AutoActiveDescriptor)active).setHK2Parent(this);
            }
         } else {
            this.activeDescriptor = null;
            this.preAnalyzed = true;
            this.implClass = active.getImplementationClass();
            this.implType = active.getImplementationType();
            this.scopeAnnotation = active.getScopeAsAnnotation();
            this.scope = active.getScopeAnnotation();
            this.contracts = Collections.unmodifiableSet(active.getContractTypes());
            this.qualifiers = Collections.unmodifiableSet(active.getQualifierAnnotations());
         }
      } else {
         this.activeDescriptor = null;
      }
   }

   public String getImplementation() {
      return this.baseDescriptor.getImplementation();
   }

   public Set<String> getAdvertisedContracts() {
      return this.baseDescriptor.getAdvertisedContracts();
   }

   public String getScope() {
      return this.baseDescriptor.getScope();
   }

   public String getName() {
      return this.baseDescriptor.getName();
   }

   public Set<String> getQualifiers() {
      return this.baseDescriptor.getQualifiers();
   }

   public DescriptorType getDescriptorType() {
      return this.baseDescriptor.getDescriptorType();
   }

   public DescriptorVisibility getDescriptorVisibility() {
      return this.baseDescriptor.getDescriptorVisibility();
   }

   public Map<String, List<String>> getMetadata() {
      return this.baseDescriptor.getMetadata();
   }

   public HK2Loader getLoader() {
      return this.baseDescriptor.getLoader();
   }

   public int getRanking() {
      return this.baseDescriptor.getRanking();
   }

   public Boolean isProxiable() {
      return this.baseDescriptor.isProxiable();
   }

   public Boolean isProxyForSameScope() {
      return this.baseDescriptor.isProxyForSameScope();
   }

   public String getClassAnalysisName() {
      return this.baseDescriptor.getClassAnalysisName();
   }

   public int setRanking(int ranking) {
      return this.sdLocator.unsortIndexes(ranking, this, this.myLists);
   }

   int setRankWithLock(int ranking) {
      return this.baseDescriptor.setRanking(ranking);
   }

   void addList(IndexedListData indexedList) {
      this.myLists.add(indexedList);
   }

   void removeList(IndexedListData indexedList) {
      this.myLists.remove(indexedList);
   }

   public Long getServiceId() {
      return this.id;
   }

   public T getCache() {
      return this.cachedValue;
   }

   public boolean isCacheSet() {
      return this.cacheSet;
   }

   public void setCache(T cacheMe) {
      synchronized (this.cacheLock) {
         this.cachedValue = cacheMe;
         this.cacheSet = true;
      }
   }

   public void releaseCache() {
      synchronized (this.cacheLock) {
         this.cacheSet = false;
         this.cachedValue = null;
      }
   }

   public boolean isReified() {
      if (this.reified) {
         return true;
      } else {
         synchronized (this) {
            return this.reified;
         }
      }
   }

   public Class<?> getImplementationClass() {
      this.checkState();
      return this.activeDescriptor != null ? this.activeDescriptor.getImplementationClass() : this.implClass;
   }

   public Type getImplementationType() {
      this.checkState();
      return this.activeDescriptor != null ? this.activeDescriptor.getImplementationType() : this.implType;
   }

   public Set<Type> getContractTypes() {
      this.checkState();
      return this.activeDescriptor != null ? this.activeDescriptor.getContractTypes() : this.contracts;
   }

   public Annotation getScopeAsAnnotation() {
      this.checkState();
      return this.scopeAnnotation;
   }

   public Class<? extends Annotation> getScopeAnnotation() {
      this.checkState();
      return this.activeDescriptor != null ? this.activeDescriptor.getScopeAnnotation() : this.scope;
   }

   public Set<Annotation> getQualifierAnnotations() {
      this.checkState();
      return this.activeDescriptor != null ? this.activeDescriptor.getQualifierAnnotations() : this.qualifiers;
   }

   public List<Injectee> getInjectees() {
      this.checkState();
      return this.activeDescriptor != null ? this.activeDescriptor.getInjectees() : this.creator.getInjectees();
   }

   public Long getFactoryServiceId() {
      return this.activeDescriptor != null ? this.activeDescriptor.getFactoryServiceId() : this.factoryServiceId;
   }

   public Long getFactoryLocatorId() {
      return this.activeDescriptor != null ? this.activeDescriptor.getFactoryLocatorId() : this.factoryLocatorId;
   }

   void setFactoryIds(Long factoryLocatorId, Long factoryServiceId) {
      this.factoryLocatorId = factoryLocatorId;
      this.factoryServiceId = factoryServiceId;
   }

   void invokeInstanceListeners(InstanceLifecycleEvent event) {
      for (InstanceLifecycleListener listener : this.instanceListeners) {
         listener.lifecycleEvent(event);
      }
   }

   public T create(ServiceHandle<?> root) {
      this.checkState();

      try {
         T retVal;
         if (this.activeDescriptor != null) {
            if (!(this.activeDescriptor instanceof AutoActiveDescriptor)) {
               this.invokeInstanceListeners(new InstanceLifecycleEventImpl(InstanceLifecycleEventType.PRE_PRODUCTION, null, this));
            }

            retVal = (T)this.activeDescriptor.create(root);
            if (!(this.activeDescriptor instanceof AutoActiveDescriptor)) {
               this.invokeInstanceListeners(new InstanceLifecycleEventImpl(InstanceLifecycleEventType.POST_PRODUCTION, retVal, this));
            }
         } else {
            retVal = this.creator.create(root, this);
         }

         return retVal;
      } catch (Throwable var10) {
         Throwable re = var10;
         if (!(var10 instanceof MultiException)) {
            re = new MultiException(var10);
         }

         MultiException reported = (MultiException)re;
         if (!reported.getReportToErrorService()) {
            throw (RuntimeException)re;
         } else {
            for (ErrorService es : this.sdLocator.getErrorHandlers()) {
               ErrorInformation ei = new ErrorInformationImpl(ErrorType.SERVICE_CREATION_FAILURE, this, null, reported);

               try {
                  es.onFailure(ei);
               } catch (Throwable var9) {
               }
            }

            throw (RuntimeException)re;
         }
      }
   }

   public void dispose(T instance) {
      this.checkState();
      InstanceLifecycleEventImpl event = new InstanceLifecycleEventImpl(InstanceLifecycleEventType.PRE_DESTRUCTION, instance, this);
      this.invokeInstanceListeners(event);

      try {
         if (this.activeDescriptor != null) {
            this.activeDescriptor.dispose(instance);
         } else {
            this.creator.dispose(instance);
         }
      } catch (Throwable var11) {
         Throwable re = var11;
         if (!(var11 instanceof MultiException)) {
            re = new MultiException(var11);
         }

         MultiException reported = (MultiException)re;
         if (!reported.getReportToErrorService()) {
            throw (RuntimeException)re;
         } else {
            for (ErrorService es : this.sdLocator.getErrorHandlers()) {
               ErrorInformation ei = new ErrorInformationImpl(ErrorType.SERVICE_DESTRUCTION_FAILURE, this, null, reported);

               try {
                  es.onFailure(ei);
               } catch (Throwable var10) {
               }
            }

            throw (RuntimeException)re;
         }
      }
   }

   private void checkState() {
      if (!this.reified) {
         synchronized (this) {
            if (!this.reified) {
               throw new IllegalStateException();
            }
         }
      }
   }

   private ActiveDescriptor<?> getFactoryDescriptor(Method provideMethod, Type factoryProvidedType, ServiceLocatorImpl locator, Collector collector) {
      if (this.factoryServiceId != null && this.factoryLocatorId != null) {
         final Long fFactoryServiceId = this.factoryServiceId;
         final Long fFactoryLocatorId = this.factoryLocatorId;
         ActiveDescriptor<?> retVal = locator.getBestDescriptor(new IndexedFilter() {
            public boolean matches(Descriptor d) {
               return d.getServiceId() != fFactoryServiceId ? false : d.getLocatorId() == fFactoryLocatorId;
            }

            public String getAdvertisedContract() {
               return Factory.class.getName();
            }

            public String getName() {
               return null;
            }
         });
         if (retVal == null) {
            collector.addThrowable(new IllegalStateException("Could not find a pre-determined factory service for " + factoryProvidedType));
         }

         return retVal;
      } else {
         List<ServiceHandle<?>> factoryHandles = locator.getAllServiceHandles(new ParameterizedTypeImpl(Factory.class, new Type[]{factoryProvidedType}));
         ServiceHandle<?> factoryHandle = null;

         for (ServiceHandle<?> candidate : factoryHandles) {
            if (this.qualifiers.isEmpty()) {
               factoryHandle = candidate;
               break;
            }

            ActiveDescriptor<?> descriptorUnderTest = candidate.getActiveDescriptor();

            try {
               descriptorUnderTest = locator.reifyDescriptor(descriptorUnderTest);
            } catch (MultiException var12) {
               collector.addThrowable(var12);
               continue;
            }

            Method candidateMethod = Utilities.getFactoryProvideMethod(descriptorUnderTest.getImplementationClass());
            Set<Annotation> candidateQualifiers = Utilities.getAllQualifiers(
               candidateMethod, Utilities.getDefaultNameFromMethod(candidateMethod, collector), collector
            );
            if (ReflectionHelper.annotationContainsAll(candidateQualifiers, this.qualifiers)) {
               factoryHandle = candidate;
               break;
            }
         }

         if (factoryHandle == null) {
            collector.addThrowable(new IllegalStateException("Could not find a factory service for " + factoryProvidedType));
            return null;
         } else {
            ActiveDescriptor<?> retVal = factoryHandle.getActiveDescriptor();
            this.factoryServiceId = retVal.getServiceId();
            this.factoryLocatorId = retVal.getLocatorId();
            return retVal;
         }
      }
   }

   void reify(Class<?> implClass, Collector collector) {
      if (!this.reified) {
         synchronized (this) {
            if (this.reified) {
               return;
            }

            while (this.reifying) {
               try {
                  this.wait();
               } catch (InterruptedException var17) {
                  collector.addThrowable(var17);
                  return;
               }
            }

            if (this.reified) {
               return;
            }

            this.reifying = true;
         }

         try {
            this.internalReify(implClass, collector);
         } finally {
            synchronized (this) {
               this.reifying = false;
               this.notifyAll();
               if (!collector.hasErrors()) {
                  this.reified = true;
               } else {
                  collector.addThrowable(new IllegalArgumentException("Errors were discovered while reifying " + this));
               }
            }
         }
      }
   }

   private void internalReify(Class<?> implClass, Collector collector) {
      if (!this.preAnalyzed) {
         this.implClass = implClass;
         this.implType = implClass;
      } else if (!implClass.equals(this.implClass)) {
         collector.addThrowable(
            new IllegalArgumentException(
               "During reification a class mistmatch was found " + implClass.getName() + " is not the same as " + this.implClass.getName()
            )
         );
      }

      if (this.getDescriptorType().equals(DescriptorType.CLASS)) {
         if (!this.preAnalyzed) {
            this.qualifiers = Collections.unmodifiableSet(Utilities.getAllQualifiers(implClass, this.baseDescriptor.getName(), collector));
         }

         ClazzCreator<T> myClazzCreator = new ClazzCreator<>(this.sdLocator, implClass);
         myClazzCreator.initialize(this, collector);
         this.creator = myClazzCreator;
         if (!this.preAnalyzed) {
            ScopeInfo si = Utilities.getScopeAnnotationType(implClass, this.baseDescriptor, collector);
            this.scopeAnnotation = si.getScope();
            this.scope = si.getAnnoType();
            this.contracts = Collections.unmodifiableSet(ReflectionHelper.getTypeClosure(implClass, this.baseDescriptor.getAdvertisedContracts()));
         }
      } else {
         Utilities.checkFactoryType(implClass, collector);
         Method provideMethod = Utilities.getFactoryProvideMethod(implClass);
         if (provideMethod == null) {
            collector.addThrowable(new IllegalArgumentException("Could not find the provide method on the class " + implClass.getName()));
            return;
         }

         if (!this.preAnalyzed) {
            this.qualifiers = Collections.unmodifiableSet(
               Utilities.getAllQualifiers(provideMethod, Utilities.getDefaultNameFromMethod(provideMethod, collector), collector)
            );
         }

         Type factoryProvidedType = provideMethod.getGenericReturnType();
         if (factoryProvidedType instanceof TypeVariable) {
            factoryProvidedType = Utilities.getFactoryProductionType(implClass);
         }

         ActiveDescriptor<?> factoryDescriptor = this.getFactoryDescriptor(provideMethod, factoryProvidedType, this.sdLocator, collector);
         if (factoryDescriptor != null) {
            this.creator = new FactoryCreator<>(this.sdLocator, factoryDescriptor);
         }

         if (!this.preAnalyzed) {
            ScopeInfo si = Utilities.getScopeAnnotationType(provideMethod, this.baseDescriptor, collector);
            this.scopeAnnotation = si.getScope();
            this.scope = si.getAnnoType();
            this.contracts = Collections.unmodifiableSet(ReflectionHelper.getTypeClosure(factoryProvidedType, this.baseDescriptor.getAdvertisedContracts()));
         }
      }

      if (this.baseDescriptor.getScope() == null && this.scope == null) {
         this.scope = PerLookup.class;
      }

      if (this.baseDescriptor.getScope() != null && this.scope != null) {
         String scopeName = this.scope.getName();
         if (!scopeName.equals(this.baseDescriptor.getScope())) {
            collector.addThrowable(
               new IllegalArgumentException(
                  "The scope name given in the descriptor ("
                     + this.baseDescriptor.getScope()
                     + ") did not match the scope annotation on the class ("
                     + this.scope.getName()
                     + ") in class "
                     + Pretty.clazz(implClass)
               )
            );
         }
      }

      if (this.scope.isAnnotationPresent(Proxiable.class) && this.scope.isAnnotationPresent(Unproxiable.class)) {
         collector.addThrowable(new IllegalArgumentException("The scope " + this.scope.getName() + " is marked both @Proxiable and @Unproxiable"));
      }

      if (this.isProxiable() != null && this.isProxiable() && Utilities.isUnproxiableScope(this.scope)) {
         collector.addThrowable(new IllegalArgumentException("The descriptor is in an Unproxiable scope but has  isProxiable set to true"));
      }
   }

   public Long getLocatorId() {
      return this.sdLocator.getLocatorId();
   }

   @Override
   public boolean close() {
      if (this.closed) {
         return true;
      } else {
         synchronized (this) {
            if (this.closed) {
               return true;
            } else {
               this.closed = true;
               return false;
            }
         }
      }
   }

   @Override
   public boolean isClosed() {
      return this.closed;
   }

   boolean isValidating(ValidationService service) {
      Boolean cachedResult = this.validationServiceCache.get(service);
      if (cachedResult != null) {
         return cachedResult;
      } else {
         boolean decision = true;

         try {
            decision = BuilderHelper.filterMatches(this, service.getLookupFilter());
         } catch (Throwable var5) {
         }

         if (decision) {
            this.validationServiceCache.put(service, Boolean.TRUE);
         } else {
            this.validationServiceCache.put(service, Boolean.FALSE);
         }

         return decision;
      }
   }

   void reupInstanceListeners(List<InstanceLifecycleListener> listeners) {
      this.instanceListeners.clear();

      for (InstanceLifecycleListener listener : listeners) {
         Filter filter = listener.getFilter();
         if (BuilderHelper.filterMatches(this, filter)) {
            this.instanceListeners.add(listener);
         }
      }
   }

   Class<?> getPreAnalyzedClass() {
      return this.implClass;
   }

   int getSingletonGeneration() {
      return this.singletonGeneration;
   }

   void setSingletonGeneration(int gen) {
      this.singletonGeneration = gen;
   }

   @Override
   public int hashCode() {
      int low32 = this.id.intValue();
      int high32 = (int)(this.id >> 32);
      int locatorLow32 = (int)this.sdLocator.getLocatorId();
      int locatorHigh32 = (int)(this.sdLocator.getLocatorId() >> 32);
      return low32 ^ high32 ^ locatorLow32 ^ locatorHigh32;
   }

   @Override
   public boolean equals(Object o) {
      if (o == null) {
         return false;
      } else if (!(o instanceof SystemDescriptor)) {
         return false;
      } else {
         SystemDescriptor sd = (SystemDescriptor)o;
         return !sd.getServiceId().equals(this.id) ? false : sd.getLocatorId().equals(this.sdLocator.getLocatorId());
      }
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer("SystemDescriptor(");
      DescriptorImpl.pretty(sb, this);
      sb.append("\n\treified=").append(this.reified);
      sb.append(")");
      return sb.toString();
   }
}
