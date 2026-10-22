package org.jvnet.hk2.internal;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Qualifier;
import jakarta.inject.Scope;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.Context;
import org.glassfish.hk2.api.ContractIndicator;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.MethodParameter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.PreDestroy;
import org.glassfish.hk2.api.Proxiable;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.ProxyForSameScope;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.Self;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Unproxiable;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.api.UseProxy;
import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.Constants;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.utilities.reflection.ScopeInfo;
import org.glassfish.hk2.utilities.reflection.TypeChecker;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Optional;
import org.jvnet.hk2.annotations.Service;

public class Utilities {
   private static final String USE_SOFT_REFERENCE_PROPERTY = "org.jvnet.hk2.properties.useSoftReference";
   static final boolean USE_SOFT_REFERENCE = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.useSoftReference", "true"));
      }
   });
   private static final Utilities.AnnotationInformation DEFAULT_ANNOTATION_INFORMATION = new Utilities.AnnotationInformation(
      Collections.emptySet(), false, false, null
   );
   private static final String PROVIDE_METHOD = "provide";
   private static final HashSet<String> NOT_INTERCEPTED = new HashSet<>();
   private static final Utilities.Interceptors EMTPY_INTERCEPTORS = new Utilities.Interceptors() {
      @Override
      public Map<Method, List<MethodInterceptor>> getMethodInterceptors() {
         return null;
      }

      @Override
      public List<ConstructorInterceptor> getConstructorInterceptors() {
         return null;
      }
   };
   private static Boolean proxiesAvailable = null;

   public static ClassAnalyzer getClassAnalyzer(ServiceLocatorImpl sli, String analyzerName, Collector errorCollector) {
      return sli.getAnalyzer(analyzerName, errorCollector);
   }

   public static <T> Constructor<T> getConstructor(Class<T> implClass, ClassAnalyzer analyzer, Collector collector) {
      Constructor<T> element = null;

      try {
         element = analyzer.getConstructor(implClass);
      } catch (MultiException var5) {
         collector.addMultiException(var5);
         return element;
      } catch (Throwable var6) {
         collector.addThrowable(var6);
         return element;
      }

      if (element == null) {
         collector.addThrowable(new AssertionError("null return from getConstructor method of analyzer " + analyzer + " for class " + implClass.getName()));
         return element;
      } else {
         final Constructor<T> result = element;
         AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
               result.setAccessible(true);
               return null;
            }
         });
         return element;
      }
   }

   public static Set<Method> getInitMethods(Class<?> implClass, ClassAnalyzer analyzer, Collector collector) {
      Set<Method> retVal;
      try {
         retVal = analyzer.getInitializerMethods(implClass);
      } catch (MultiException var5) {
         collector.addMultiException(var5);
         return Collections.emptySet();
      } catch (Throwable var6) {
         collector.addThrowable(var6);
         return Collections.emptySet();
      }

      if (retVal == null) {
         collector.addThrowable(
            new AssertionError("null return from getInitializerMethods method of analyzer " + analyzer + " for class " + implClass.getName())
         );
         return Collections.emptySet();
      } else {
         return retVal;
      }
   }

   public static Set<Field> getInitFields(Class<?> implClass, ClassAnalyzer analyzer, Collector collector) {
      Set<Field> retVal;
      try {
         retVal = analyzer.getFields(implClass);
      } catch (MultiException var5) {
         collector.addMultiException(var5);
         return Collections.emptySet();
      } catch (Throwable var6) {
         collector.addThrowable(var6);
         return Collections.emptySet();
      }

      if (retVal == null) {
         collector.addThrowable(new AssertionError("null return from getFields method of analyzer " + analyzer + " for class " + implClass.getName()));
         return Collections.emptySet();
      } else {
         return retVal;
      }
   }

   public static Method getPostConstruct(Class<?> implClass, ClassAnalyzer analyzer, Collector collector) {
      try {
         return analyzer.getPostConstructMethod(implClass);
      } catch (MultiException var4) {
         collector.addMultiException(var4);
         return null;
      } catch (Throwable var5) {
         collector.addThrowable(var5);
         return null;
      }
   }

   public static Method getPreDestroy(Class<?> implClass, ClassAnalyzer analyzer, Collector collector) {
      try {
         return analyzer.getPreDestroyMethod(implClass);
      } catch (MultiException var4) {
         collector.addMultiException(var4);
         return null;
      } catch (Throwable var5) {
         collector.addThrowable(var5);
         return null;
      }
   }

   public static Class<?> getFactoryAwareImplementationClass(ActiveDescriptor<?> descriptor) {
      return descriptor.getDescriptorType().equals(DescriptorType.CLASS) ? descriptor.getImplementationClass() : getFactoryProductionClass(descriptor);
   }

   public static void checkLookupType(Class<?> checkMe) {
      if (checkMe.isAnnotation()) {
         if (!checkMe.isAnnotationPresent(Scope.class)) {
            if (!checkMe.isAnnotationPresent(Qualifier.class)) {
               throw new IllegalArgumentException("Lookup type " + checkMe + " must be a scope or annotation");
            }
         }
      }
   }

   public static Class<?> translatePrimitiveType(Class<?> type) {
      Class<?> translation = (Class<?>)Constants.PRIMITIVE_MAP.get(type);
      return translation == null ? type : translation;
   }

   public static void handleErrors(NarrowResults results, LinkedList<ErrorService> callThese) {
      Collector collector = new Collector();

      for (ErrorResults errorResult : results.getErrors()) {
         for (ErrorService eService : callThese) {
            try {
               eService.onFailure(
                  new ErrorInformationImpl(ErrorType.FAILURE_TO_REIFY, errorResult.getDescriptor(), errorResult.getInjectee(), errorResult.getMe())
               );
            } catch (MultiException var10) {
               for (Throwable th : var10.getErrors()) {
                  collector.addThrowable(th);
               }
            } catch (Throwable var11) {
               collector.addThrowable(var11);
            }
         }
      }

      collector.throwIfErrors();
   }

   public static Class<?> loadClass(String loadMe, Descriptor fromMe, Collector collector) {
      HK2Loader loader = fromMe.getLoader();
      if (loader == null) {
         ClassLoader cl = Utilities.class.getClassLoader();
         if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
         }

         try {
            return cl.loadClass(loadMe);
         } catch (Throwable var8) {
            collector.addThrowable(var8);
            return null;
         }
      } else {
         try {
            return loader.loadClass(loadMe);
         } catch (Throwable var9) {
            if (var9 instanceof MultiException) {
               MultiException me = (MultiException)var9;

               for (Throwable th2 : me.getErrors()) {
                  collector.addThrowable(th2);
               }
            } else {
               collector.addThrowable(var9);
            }

            return null;
         }
      }
   }

   public static Class<?> loadClass(String implementation, Injectee injectee) {
      ClassLoader loader;
      if (injectee != null) {
         AnnotatedElement parent = injectee.getParent();
         if (parent instanceof Constructor) {
            loader = ((Constructor)parent).getDeclaringClass().getClassLoader();
         } else if (parent instanceof Method) {
            loader = ((Method)parent).getDeclaringClass().getClassLoader();
         } else if (parent instanceof Field) {
            loader = ((Field)parent).getDeclaringClass().getClassLoader();
         } else {
            loader = injectee.getClass().getClassLoader();
         }
      } else {
         loader = Utilities.class.getClassLoader();
      }

      try {
         return loader.loadClass(implementation);
      } catch (Throwable var8) {
         ClassLoader ccl = Thread.currentThread().getContextClassLoader();
         if (ccl != null) {
            try {
               return ccl.loadClass(implementation);
            } catch (Throwable var7) {
               MultiException me = new MultiException(var8);
               me.addError(var7);
               throw me;
            }
         } else {
            throw new MultiException(var8);
         }
      }
   }

   public static Class<? extends Annotation> getInjectionResolverType(ActiveDescriptor<?> desc) {
      for (Type advertisedType : desc.getContractTypes()) {
         Class<?> rawClass = ReflectionHelper.getRawClass(advertisedType);
         if (InjectionResolver.class.equals(rawClass)) {
            if (!(advertisedType instanceof ParameterizedType)) {
               return null;
            }

            Type firstType = ReflectionHelper.getFirstTypeArgument(advertisedType);
            if (!(firstType instanceof Class)) {
               return null;
            }

            Class<?> retVal = (Class<?>)firstType;
            if (!Annotation.class.isAssignableFrom(retVal)) {
               return null;
            }

            return (Class<? extends Annotation>)retVal;
         }
      }

      return null;
   }

   private static Class<?> getFactoryProductionClass(ActiveDescriptor<?> descriptor) {
      Class<?> factoryClass = descriptor.getImplementationClass();
      Type factoryProvidedType = getFactoryProductionType(factoryClass);
      Class<?> retVal = ReflectionHelper.getRawClass(factoryProvidedType);
      if (retVal == null && descriptor.getContractTypes().size() == 1) {
         Type contract = (Type)descriptor.getContractTypes().iterator().next();
         retVal = ReflectionHelper.getRawClass(contract);
      }

      if (retVal == null) {
         throw new MultiException(new AssertionError("Could not find true produced type of factory " + factoryClass.getName()));
      } else {
         return retVal;
      }
   }

   public static Type getFactoryProductionType(Class<?> factoryClass) {
      Set<Type> factoryTypes = ReflectionHelper.getTypeClosure(factoryClass, Collections.singleton(Factory.class.getName()));
      ParameterizedType parameterizedType = (ParameterizedType)factoryTypes.iterator().next();
      return parameterizedType.getActualTypeArguments()[0];
   }

   public static void checkFactoryType(Class<?> factoryClass, Collector collector) {
      for (Type type : factoryClass.getGenericInterfaces()) {
         Class<?> rawClass = ReflectionHelper.getRawClass(type);
         if (rawClass != null && Factory.class.equals(rawClass)) {
            Type firstType = ReflectionHelper.getFirstTypeArgument(type);
            if (firstType instanceof WildcardType) {
               collector.addThrowable(new IllegalArgumentException("The class " + Pretty.clazz(factoryClass) + " has a Wildcard as its type"));
            }
         }
      }
   }

   private static boolean hasContract(Class<?> clazz) {
      if (clazz == null) {
         return false;
      } else if (clazz.isAnnotationPresent(Contract.class)) {
         return true;
      } else {
         for (Annotation clazzAnnotation : clazz.getAnnotations()) {
            if (clazzAnnotation.annotationType().isAnnotationPresent(ContractIndicator.class)) {
               return true;
            }
         }

         return false;
      }
   }

   private static Set<Type> getAutoAdvertisedTypes(Type t) {
      LinkedHashSet<Type> retVal = new LinkedHashSet<>();
      retVal.add(t);
      Class<?> rawClass = ReflectionHelper.getRawClass(t);
      if (rawClass == null) {
         return retVal;
      } else {
         ContractsProvided provided = rawClass.getAnnotation(ContractsProvided.class);
         if (provided == null) {
            for (Type candidate : ReflectionHelper.getAllTypes(t)) {
               if (hasContract(ReflectionHelper.getRawClass(candidate))) {
                  retVal.add(candidate);
               }
            }

            return retVal;
         } else {
            retVal.clear();

            for (Class<?> providedContract : provided.value()) {
               retVal.add(providedContract);
            }

            return retVal;
         }
      }
   }

   public static <T> AutoActiveDescriptor<T> createAutoDescriptor(Class<T> clazz, ServiceLocatorImpl locator) throws MultiException, IllegalArgumentException, IllegalStateException {
      if (clazz == null) {
         throw new IllegalArgumentException();
      } else {
         Collector collector = new Collector();
         Boolean proxy = null;
         Boolean proxyForSameScope = null;
         String serviceMetadata = null;
         String serviceName = null;
         Service serviceAnno = clazz.getAnnotation(Service.class);
         if (serviceAnno != null) {
            if (!"".equals(serviceAnno.name())) {
               serviceName = serviceAnno.name();
            }

            if (!"".equals(serviceAnno.metadata())) {
               serviceMetadata = serviceAnno.metadata();
            }
         }

         Set<Annotation> qualifiers = ReflectionHelper.getQualifierAnnotations(clazz);
         String name = ReflectionHelper.getNameFromAllQualifiers(qualifiers, clazz);
         if (serviceName != null && name != null) {
            if (!serviceName.equals(name)) {
               throw new IllegalArgumentException(
                  "The class "
                     + clazz.getName()
                     + " has an @Service name of "
                     + serviceName
                     + " and has an @Named value of "
                     + name
                     + " which names do not match"
               );
            }
         } else if (name == null && serviceName != null) {
            name = serviceName;
         }

         qualifiers = getAllQualifiers(clazz, name, collector);
         Set<Type> contracts = getAutoAdvertisedTypes(clazz);
         ScopeInfo scopeInfo = getScopeInfo(clazz, null, collector);
         Class<? extends Annotation> scope = scopeInfo.getAnnoType();
         String analyzerName = locator.getPerLocatorUtilities().getAutoAnalyzerName(clazz);
         ClazzCreator<T> creator = new ClazzCreator<>(locator, clazz);
         Map<String, List<String>> metadata = new HashMap<>();
         if (serviceMetadata != null) {
            try {
               ReflectionHelper.readMetadataMap(serviceMetadata, metadata);
            } catch (IOException var22) {
               metadata.clear();
               ReflectionHelper.parseServiceMetadataString(serviceMetadata, metadata);
            }
         }

         collector.throwIfErrors();
         if (scopeInfo.getScope() != null) {
            BuilderHelper.getMetadataValues(scopeInfo.getScope(), metadata);
         }

         for (Annotation qualifier : qualifiers) {
            BuilderHelper.getMetadataValues(qualifier, metadata);
         }

         UseProxy useProxy = clazz.getAnnotation(UseProxy.class);
         if (useProxy != null) {
            proxy = useProxy.value();
         }

         ProxyForSameScope pfss = clazz.getAnnotation(ProxyForSameScope.class);
         if (pfss != null) {
            proxyForSameScope = pfss.value();
         }

         DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
         Visibility vi = clazz.getAnnotation(Visibility.class);
         if (vi != null) {
            visibility = vi.value();
         }

         int rank = BuilderHelper.getRank(clazz);
         AutoActiveDescriptor<T> retVal = new AutoActiveDescriptor<>(
            clazz, creator, contracts, scope, name, qualifiers, visibility, rank, proxy, proxyForSameScope, analyzerName, metadata, DescriptorType.CLASS, clazz
         );
         retVal.setScopeAsAnnotation(scopeInfo.getScope());
         creator.initialize(retVal, analyzerName, collector);
         collector.throwIfErrors();
         return retVal;
      }
   }

   public static <T> AutoActiveDescriptor<T> createAutoFactoryDescriptor(
      Class<T> parentClazz, ActiveDescriptor<?> factoryDescriptor, ServiceLocatorImpl locator
   ) throws MultiException, IllegalArgumentException, IllegalStateException {
      if (parentClazz == null) {
         throw new IllegalArgumentException();
      } else {
         Collector collector = new Collector();
         Type factoryProductionType = getFactoryProductionType(parentClazz);
         Method provideMethod = getFactoryProvideMethod(parentClazz);
         if (provideMethod == null) {
            collector.addThrowable(new IllegalArgumentException("Could not find the provide method on the class " + parentClazz.getName()));
            collector.throwIfErrors();
         }

         Boolean proxy = null;
         Boolean proxyForSameScope = null;
         Set<Annotation> qualifiers = ReflectionHelper.getQualifierAnnotations(provideMethod);
         String name = ReflectionHelper.getNameFromAllQualifiers(qualifiers, provideMethod);
         Set<Type> contracts = getAutoAdvertisedTypes(factoryProductionType);
         ScopeInfo scopeInfo = getScopeInfo(provideMethod, null, collector);
         Class<? extends Annotation> scope = scopeInfo.getAnnoType();
         FactoryCreator<T> creator = new FactoryCreator<>(locator, factoryDescriptor);
         collector.throwIfErrors();
         Map<String, List<String>> metadata = new HashMap<>();
         if (scopeInfo.getScope() != null) {
            BuilderHelper.getMetadataValues(scopeInfo.getScope(), metadata);
         }

         for (Annotation qualifier : qualifiers) {
            BuilderHelper.getMetadataValues(qualifier, metadata);
         }

         UseProxy useProxy = provideMethod.getAnnotation(UseProxy.class);
         if (useProxy != null) {
            proxy = useProxy.value();
         }

         ProxyForSameScope pfss = provideMethod.getAnnotation(ProxyForSameScope.class);
         if (pfss != null) {
            proxyForSameScope = pfss.value();
         }

         DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
         Visibility vi = provideMethod.getAnnotation(Visibility.class);
         if (vi != null) {
            visibility = vi.value();
         }

         int rank = 0;
         Rank ranking = provideMethod.getAnnotation(Rank.class);
         if (ranking != null) {
            rank = ranking.value();
         }

         AutoActiveDescriptor<T> retVal = new AutoActiveDescriptor<>(
            factoryDescriptor.getImplementationClass(),
            creator,
            contracts,
            scope,
            name,
            qualifiers,
            visibility,
            rank,
            proxy,
            proxyForSameScope,
            null,
            metadata,
            DescriptorType.PROVIDE_METHOD,
            null
         );
         retVal.setScopeAsAnnotation(scopeInfo.getScope());
         collector.throwIfErrors();
         return retVal;
      }
   }

   public static void justPreDestroy(Object preMe, ServiceLocatorImpl locator, String strategy) {
      if (preMe == null) {
         throw new IllegalArgumentException();
      } else {
         Collector collector = new Collector();
         ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
         collector.throwIfErrors();
         collector.throwIfErrors();
         Class<?> baseClass = preMe.getClass();
         Method preDestroy = getPreDestroy(baseClass, analyzer, collector);
         collector.throwIfErrors();
         if (preDestroy != null) {
            try {
               ReflectionHelper.invoke(preMe, preDestroy, new Object[0], locator.getNeutralContextClassLoader());
            } catch (Throwable var8) {
               throw new MultiException(var8);
            }
         }
      }
   }

   public static void justPostConstruct(Object postMe, ServiceLocatorImpl locator, String strategy) {
      if (postMe == null) {
         throw new IllegalArgumentException();
      } else {
         Collector collector = new Collector();
         ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
         collector.throwIfErrors();
         Class<?> baseClass = postMe.getClass();
         Method postConstruct = getPostConstruct(baseClass, analyzer, collector);
         collector.throwIfErrors();
         if (postConstruct != null) {
            try {
               ReflectionHelper.invoke(postMe, postConstruct, new Object[0], locator.getNeutralContextClassLoader());
            } catch (Throwable var8) {
               throw new MultiException(var8);
            }
         }
      }
   }

   public static Object justAssistedInject(Object injectMe, Method method, ServiceLocatorImpl locator, ServiceHandle<?> root, MethodParameter... givenValues) {
      if (injectMe != null && method != null) {
         if (givenValues == null) {
            givenValues = new MethodParameter[0];
         }

         int numParameters = method.getParameterTypes().length;
         Map<Integer, MethodParameter> knownValues = new HashMap<>();

         for (MethodParameter mp : givenValues) {
            int index = mp.getParameterPosition();
            if (knownValues.containsKey(index)) {
               throw new IllegalArgumentException("The given values contain more than one value for index " + index);
            }

            knownValues.put(index, mp);
            if (index < 0 || index >= numParameters) {
               throw new IllegalArgumentException("Index of " + mp + " is out of range of the method parameters " + method);
            }
         }

         List<SystemInjecteeImpl> injectees = getMethodInjectees(injectMe.getClass(), method, null, knownValues);
         Object[] args = new Object[numParameters];

         for (int lcv = 0; lcv < injectees.size(); lcv++) {
            SystemInjecteeImpl injectee = injectees.get(lcv);
            if (injectee == null) {
               MethodParameter mp = knownValues.get(lcv);
               if (mp == null) {
                  throw new AssertionError("Error getting values " + lcv + " method=" + method + " injectMe=" + injectMe + " knownValues=" + knownValues);
               }

               args[lcv] = mp.getParameterValue();
            } else {
               InjectionResolver<?> resolver = locator.getPerLocatorUtilities().getInjectionResolver(locator, injectee);
               args[lcv] = resolver.resolve(injectee, root);
            }
         }

         try {
            return ReflectionHelper.invoke(injectMe, method, args, locator.getNeutralContextClassLoader());
         } catch (MultiException var12) {
            throw var12;
         } catch (Throwable var13) {
            throw new MultiException(var13);
         }
      } else {
         throw new IllegalArgumentException("injectMe=" + injectMe + " method=" + method);
      }
   }

   public static void justInject(Object injectMe, ServiceLocatorImpl locator, String strategy) {
      if (injectMe == null) {
         throw new IllegalArgumentException();
      } else {
         Collector collector = new Collector();
         ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
         collector.throwIfErrors();
         Class<?> baseClass = injectMe.getClass();
         Set<Field> fields = getInitFields(baseClass, analyzer, collector);
         Set<Method> methods = getInitMethods(baseClass, analyzer, collector);
         collector.throwIfErrors();

         for (Field field : fields) {
            InjectionResolver<?> resolver = locator.getPerLocatorUtilities().getInjectionResolver(locator, field);
            List<SystemInjecteeImpl> injecteeFields = getFieldInjectees(baseClass, field, null);
            validateSelfInjectees(null, injecteeFields, collector);
            collector.throwIfErrors();
            Injectee injectee = injecteeFields.get(0);
            Object fieldValue = resolver.resolve(injectee, null);

            try {
               ReflectionHelper.setField(field, injectMe, fieldValue);
            } catch (MultiException var17) {
               throw var17;
            } catch (Throwable var18) {
               throw new MultiException(var18);
            }
         }

         for (Method method : methods) {
            List<SystemInjecteeImpl> injectees = getMethodInjectees(baseClass, method, null);
            validateSelfInjectees(null, injectees, collector);
            collector.throwIfErrors();
            Object[] args = new Object[injectees.size()];

            for (SystemInjecteeImpl injectee : injectees) {
               InjectionResolver<?> resolver = locator.getPerLocatorUtilities().getInjectionResolver(locator, injectee);
               args[injectee.getPosition()] = resolver.resolve(injectee, null);
            }

            try {
               ReflectionHelper.invoke(injectMe, method, args, locator.getNeutralContextClassLoader());
            } catch (MultiException var15) {
               throw var15;
            } catch (Throwable var16) {
               throw new MultiException(var16);
            }
         }
      }
   }

   public static <T> T justCreate(Class<T> createMe, ServiceLocatorImpl locator, String strategy) {
      if (createMe == null) {
         throw new IllegalArgumentException();
      } else {
         Collector collector = new Collector();
         ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
         collector.throwIfErrors();
         Constructor<?> c = getConstructor(createMe, analyzer, collector);
         collector.throwIfErrors();
         List<SystemInjecteeImpl> injectees = getConstructorInjectees(c, null);
         validateSelfInjectees(null, injectees, collector);
         collector.throwIfErrors();
         Object[] args = new Object[injectees.size()];

         for (SystemInjecteeImpl injectee : injectees) {
            InjectionResolver<?> resolver = locator.getPerLocatorUtilities().getInjectionResolver(locator, injectee);
            args[injectee.getPosition()] = resolver.resolve(injectee, null);
         }

         try {
            return (T)ReflectionHelper.makeMe(c, args, locator.getNeutralContextClassLoader());
         } catch (Throwable var11) {
            throw new MultiException(var11);
         }
      }
   }

   public static Class<?>[] getInterfacesForProxy(Set<Type> contracts) {
      LinkedList<Class<?>> retVal = new LinkedList<>();
      retVal.add(ProxyCtl.class);

      for (Type type : contracts) {
         Class<?> rawClass = ReflectionHelper.getRawClass(type);
         if (rawClass != null && rawClass.isInterface()) {
            retVal.add(rawClass);
         }
      }

      return retVal.toArray(new Class[retVal.size()]);
   }

   public static boolean isProxiableScope(Class<? extends Annotation> scope) {
      return scope.isAnnotationPresent(Proxiable.class);
   }

   public static boolean isUnproxiableScope(Class<? extends Annotation> scope) {
      return scope.isAnnotationPresent(Unproxiable.class);
   }

   private static boolean isProxiable(ActiveDescriptor<?> desc, Injectee injectee) {
      Boolean directed = desc.isProxiable();
      if (directed != null) {
         if (injectee == null) {
            return directed;
         } else if (!directed) {
            return false;
         } else {
            ActiveDescriptor<?> injecteeDescriptor = injectee.getInjecteeDescriptor();
            if (injecteeDescriptor == null) {
               return true;
            } else {
               Boolean sameScope = desc.isProxyForSameScope();
               return sameScope != null && !sameScope ? !desc.getScope().equals(injecteeDescriptor.getScope()) : true;
            }
         }
      } else {
         Class<? extends Annotation> scopeAnnotation = desc.getScopeAnnotation();
         if (!scopeAnnotation.isAnnotationPresent(Proxiable.class)) {
            return false;
         } else if (injectee == null) {
            return true;
         } else {
            ActiveDescriptor<?> injecteeDescriptor = injectee.getInjecteeDescriptor();
            if (injecteeDescriptor == null) {
               return true;
            } else {
               Proxiable proxiable = scopeAnnotation.getAnnotation(Proxiable.class);
               Boolean proxyForSameScope = desc.isProxyForSameScope();
               if (proxyForSameScope != null) {
                  if (proxyForSameScope) {
                     return true;
                  }
               } else if (proxiable == null || proxiable.proxyForSameScope()) {
                  return true;
               }

               return !desc.getScope().equals(injecteeDescriptor.getScope());
            }
         }
      }
   }

   public static <T> T getFirstThingInList(List<T> set) {
      Iterator var1 = set.iterator();
      return (T)(var1.hasNext() ? var1.next() : null);
   }

   public static ActiveDescriptor<ServiceLocator> getLocatorDescriptor(ServiceLocator locator) {
      HashSet<Type> contracts = new HashSet<>();
      contracts.add(ServiceLocator.class);
      Set<Annotation> qualifiers = Collections.emptySet();
      ActiveDescriptor<ServiceLocator> retVal = new ConstantActiveDescriptor<ServiceLocator>(
         locator, contracts, PerLookup.class, null, qualifiers, DescriptorVisibility.LOCAL, 0, null, null, null, locator.getLocatorId(), null
      );
      return retVal;
   }

   public static ActiveDescriptor<InjectionResolver<Inject>> getThreeThirtyDescriptor(ServiceLocatorImpl locator) {
      ThreeThirtyResolver threeThirtyResolver = new ThreeThirtyResolver(locator);
      HashSet<Type> contracts = new HashSet<>();
      Type[] actuals = new Type[]{Inject.class};
      contracts.add(new ParameterizedTypeImpl(InjectionResolver.class, actuals));
      Set<Annotation> qualifiers = new HashSet<>();
      qualifiers.add(new NamedImpl("SystemInjectResolver"));
      ActiveDescriptor<InjectionResolver<Inject>> retVal = new ConstantActiveDescriptor<InjectionResolver<Inject>>(
         threeThirtyResolver,
         contracts,
         Singleton.class,
         "SystemInjectResolver",
         qualifiers,
         DescriptorVisibility.LOCAL,
         0,
         null,
         null,
         null,
         locator.getLocatorId(),
         null
      );
      return retVal;
   }

   public static Constructor<?> findProducerConstructor(Class<?> annotatedType, ServiceLocatorImpl locator, Collector collector) {
      Constructor<?> zeroArgConstructor = null;
      Constructor<?> aConstructorWithInjectAnnotation = null;

      for (Constructor<?> constructor : getAllConstructors(annotatedType)) {
         Type[] rawParameters = constructor.getGenericParameterTypes();
         if (rawParameters.length <= 0) {
            zeroArgConstructor = constructor;
         }

         if (locator.hasInjectAnnotation(constructor)) {
            if (aConstructorWithInjectAnnotation != null) {
               collector.addThrowable(new IllegalArgumentException("There is more than one constructor on class " + Pretty.clazz(annotatedType)));
               return null;
            }

            aConstructorWithInjectAnnotation = constructor;
         }

         if (!isProperConstructor(constructor)) {
            collector.addThrowable(
               new IllegalArgumentException("The constructor for " + Pretty.clazz(annotatedType) + " may not have an annotation as a parameter")
            );
            return null;
         }
      }

      if (aConstructorWithInjectAnnotation != null) {
         return aConstructorWithInjectAnnotation;
      } else if (zeroArgConstructor == null) {
         collector.addThrowable(
            new NoSuchMethodException("The class " + Pretty.clazz(annotatedType) + " has no constructor marked @Inject and no zero argument constructor")
         );
         return null;
      } else {
         return zeroArgConstructor;
      }
   }

   private static boolean isProperConstructor(Constructor<?> c) {
      for (Class<?> pClazz : c.getParameterTypes()) {
         if (pClazz.isAnnotation()) {
            return false;
         }
      }

      return true;
   }

   private static Set<Constructor<?>> getAllConstructors(final Class<?> clazz) {
      HashSet<Constructor<?>> retVal = new LinkedHashSet<>();
      Constructor<?>[] constructors = AccessController.doPrivileged(new PrivilegedAction<Constructor<?>[]>() {
         public Constructor<?>[] run() {
            return clazz.getDeclaredConstructors();
         }
      });

      for (Constructor<?> constructor : constructors) {
         retVal.add(constructor);
      }

      return retVal;
   }

   private static boolean hasSubscribeToAnnotation(Method method) {
      Annotation[][] paramAnnotations = method.getParameterAnnotations();

      for (int outer = 0; outer < paramAnnotations.length; outer++) {
         Annotation[] paramAnnos = paramAnnotations[outer];

         for (int inner = 0; inner < paramAnnos.length; inner++) {
            if (SubscribeTo.class.equals(paramAnnos[inner].annotationType())) {
               return true;
            }
         }
      }

      return false;
   }

   public static Set<Method> findInitializerMethods(Class<?> annotatedType, ServiceLocatorImpl locator, Collector errorCollector) {
      LinkedHashSet<Method> retVal = new LinkedHashSet<>();
      ClassReflectionHelper crh = locator.getClassReflectionHelper();

      for (MethodWrapper methodWrapper : crh.getAllMethods(annotatedType)) {
         Method method = methodWrapper.getMethod();
         if (locator.hasInjectAnnotation(method) && !method.isSynthetic() && !method.isBridge() && !hasSubscribeToAnnotation(method)) {
            if (!isProperMethod(method)) {
               errorCollector.addThrowable(
                  new IllegalArgumentException(
                     "An initializer method " + Pretty.method(method) + " is static, abstract or has a parameter that is an annotation"
                  )
               );
            } else if (!Modifier.isStatic(method.getModifiers())) {
               retVal.add(method);
            }
         }
      }

      return retVal;
   }

   public static Method findPostConstruct(Class<?> clazz, ServiceLocatorImpl locator, Collector collector) {
      try {
         return locator.getClassReflectionHelper().findPostConstruct(clazz, PostConstruct.class);
      } catch (IllegalArgumentException var4) {
         collector.addThrowable(var4);
         return null;
      }
   }

   public static Method findPreDestroy(Class<?> clazz, ServiceLocatorImpl locator, Collector collector) {
      try {
         return locator.getClassReflectionHelper().findPreDestroy(clazz, PreDestroy.class);
      } catch (IllegalArgumentException var4) {
         collector.addThrowable(var4);
         return null;
      }
   }

   public static Set<Field> findInitializerFields(Class<?> annotatedType, ServiceLocatorImpl locator, Collector errorCollector) {
      LinkedHashSet<Field> retVal = new LinkedHashSet<>();
      ClassReflectionHelper crh = locator.getClassReflectionHelper();

      for (Field field : crh.getAllFields(annotatedType)) {
         if (locator.hasInjectAnnotation(field)) {
            if (!isProperField(field)) {
               errorCollector.addThrowable(
                  new IllegalArgumentException("The field " + Pretty.field(field) + " may not be static, final or have an Annotation type")
               );
            } else if (!Modifier.isStatic(field.getModifiers())) {
               retVal.add(field);
            }
         }
      }

      return retVal;
   }

   static AnnotatedElementAnnotationInfo computeAEAI(AnnotatedElement annotatedElement) {
      if (annotatedElement instanceof Method) {
         Method m = (Method)annotatedElement;
         return new AnnotatedElementAnnotationInfo(m.getAnnotations(), true, m.getParameterAnnotations(), false);
      } else if (annotatedElement instanceof Constructor) {
         Constructor<?> c = (Constructor<?>)annotatedElement;
         return new AnnotatedElementAnnotationInfo(c.getAnnotations(), true, c.getParameterAnnotations(), true);
      } else {
         return new AnnotatedElementAnnotationInfo(annotatedElement.getAnnotations(), false, new Annotation[0][], false);
      }
   }

   private static boolean isProperMethod(Method member) {
      if (isAbstract(member)) {
         return false;
      } else {
         for (Class<?> paramClazz : member.getParameterTypes()) {
            if (paramClazz.isAnnotation()) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isProperField(Field field) {
      if (isFinal(field)) {
         return false;
      } else {
         Class<?> type = field.getType();
         return !type.isAnnotation();
      }
   }

   public static boolean isAbstract(Member member) {
      int modifiers = member.getModifiers();
      return (modifiers & 1024) != 0;
   }

   public static boolean isFinal(Member member) {
      int modifiers = member.getModifiers();
      return (modifiers & 16) != 0;
   }

   private static boolean isFinal(Class<?> clazz) {
      int modifiers = clazz.getModifiers();
      return (modifiers & 16) != 0;
   }

   private static ScopeInfo getScopeInfo(AnnotatedElement annotatedGuy, Descriptor defaultScope, Collector collector) {
      AnnotatedElement topLevelElement = annotatedGuy;
      Annotation winnerScope = null;

      while (annotatedGuy != null) {
         Annotation current = internalGetScopeAnnotationType(annotatedGuy, collector);
         if (current != null) {
            if (annotatedGuy.equals(topLevelElement)) {
               winnerScope = current;
            } else if (current.annotationType().isAnnotationPresent(Inherited.class)) {
               winnerScope = current;
            }
            break;
         }

         if (annotatedGuy instanceof Class) {
            annotatedGuy = ((Class)annotatedGuy).getSuperclass();
         } else {
            Method theMethod = (Method)annotatedGuy;
            Class<?> methodClass = theMethod.getDeclaringClass();
            annotatedGuy = null;

            for (Class<?> methodSuperclass = methodClass.getSuperclass(); methodSuperclass != null; methodSuperclass = methodSuperclass.getSuperclass()) {
               if (Factory.class.isAssignableFrom(methodSuperclass)) {
                  annotatedGuy = getFactoryProvideMethod(methodSuperclass);
                  break;
               }
            }
         }
      }

      if (winnerScope != null) {
         return new ScopeInfo(winnerScope, winnerScope.annotationType());
      } else if (topLevelElement.isAnnotationPresent(Service.class)) {
         return new ScopeInfo(ServiceLocatorUtilities.getSingletonAnnotation(), Singleton.class);
      } else {
         if (defaultScope != null && defaultScope.getScope() != null) {
            Class<? extends Annotation> descScope = (Class<? extends Annotation>)loadClass(defaultScope.getScope(), defaultScope, collector);
            if (descScope != null) {
               return new ScopeInfo(null, descScope);
            }
         }

         return new ScopeInfo(ServiceLocatorUtilities.getPerLookupAnnotation(), PerLookup.class);
      }
   }

   public static Class<? extends Annotation> getScopeAnnotationType(Class<?> fromThis, Descriptor defaultScope) {
      Collector collector = new Collector();
      ScopeInfo si = getScopeInfo(fromThis, defaultScope, collector);
      collector.throwIfErrors();
      return si.getAnnoType();
   }

   public static ScopeInfo getScopeAnnotationType(AnnotatedElement annotatedGuy, Descriptor defaultScope, Collector collector) {
      return getScopeInfo(annotatedGuy, defaultScope, collector);
   }

   private static Annotation internalGetScopeAnnotationType(AnnotatedElement annotatedGuy, Collector collector) {
      boolean epicFail = false;
      Annotation retVal = null;

      for (Annotation annotation : annotatedGuy.getDeclaredAnnotations()) {
         if (annotation.annotationType().isAnnotationPresent(Scope.class)) {
            if (retVal != null) {
               collector.addThrowable(
                  new IllegalArgumentException(
                     "The type "
                        + annotatedGuy
                        + " may not have more than one scope.  It has at least "
                        + Pretty.clazz(retVal.annotationType())
                        + " and "
                        + Pretty.clazz(annotation.annotationType())
                  )
               );
               epicFail = true;
            } else {
               retVal = annotation;
            }
         }
      }

      return epicFail ? null : retVal;
   }

   public static Method getFactoryProvideMethod(Class<?> clazz) {
      try {
         return clazz.getMethod("provide");
      } catch (NoSuchMethodException var2) {
         return null;
      }
   }

   public static String getDefaultNameFromMethod(Method parent, Collector collector) {
      Named named = parent.getAnnotation(Named.class);
      if (named == null) {
         return null;
      } else {
         if (named.value() == null || named.value().equals("")) {
            collector.addThrowable(new IllegalArgumentException("@Named on the provide method of a factory must have an explicit value"));
         }

         return named.value();
      }
   }

   public static Set<Annotation> getAllQualifiers(AnnotatedElement annotatedGuy, String name, Collector collector) {
      Named namedQualifier = null;
      Set<Annotation> retVal = ReflectionHelper.getQualifierAnnotations(annotatedGuy);

      for (Annotation anno : retVal) {
         if (anno instanceof Named) {
            namedQualifier = (Named)anno;
            break;
         }
      }

      if (name == null) {
         if (namedQualifier != null) {
            collector.addThrowable(
               new IllegalArgumentException(
                  "No name was in the descriptor, but this element(" + annotatedGuy + " has a Named annotation with value: " + namedQualifier.value()
               )
            );
            retVal.remove(namedQualifier);
         }

         return retVal;
      } else {
         if (namedQualifier == null || namedQualifier.value().equals("")) {
            if (namedQualifier != null) {
               retVal.remove(namedQualifier);
            }

            namedQualifier = new NamedImpl(name);
            retVal.add(namedQualifier);
         }

         if (!name.equals(namedQualifier.value())) {
            collector.addThrowable(
               new IllegalArgumentException(
                  "The class had an @Named qualifier that was inconsistent.  The expected name is "
                     + name
                     + " but the annotation has name "
                     + namedQualifier.value()
               )
            );
         }

         return retVal;
      }
   }

   private static Utilities.AnnotationInformation getParamInformation(Annotation[] memberAnnotations) {
      boolean useDefault = true;
      Set<Annotation> qualifiers = null;
      boolean optional = false;
      boolean self = false;
      Unqualified unqualified = null;

      for (Annotation anno : memberAnnotations) {
         if (ReflectionHelper.isAnnotationAQualifier(anno)) {
            if (qualifiers == null) {
               qualifiers = new HashSet<>();
            }

            qualifiers.add(anno);
            useDefault = false;
         } else if (Optional.class.equals(anno.annotationType())) {
            optional = true;
            useDefault = false;
         } else if (Self.class.equals(anno.annotationType())) {
            self = true;
            useDefault = false;
         } else if (Unqualified.class.equals(anno.annotationType())) {
            unqualified = (Unqualified)anno;
            useDefault = false;
         }
      }

      if (useDefault) {
         return DEFAULT_ANNOTATION_INFORMATION;
      } else {
         if (qualifiers == null) {
            qualifiers = DEFAULT_ANNOTATION_INFORMATION.qualifiers;
         }

         return new Utilities.AnnotationInformation(qualifiers, optional, self, unqualified);
      }
   }

   public static List<SystemInjecteeImpl> getConstructorInjectees(Constructor<?> c, ActiveDescriptor<?> injecteeDescriptor) {
      Type[] genericTypeParams = c.getGenericParameterTypes();
      Annotation[][] paramAnnotations = c.getParameterAnnotations();
      List<SystemInjecteeImpl> retVal = new LinkedList<>();

      for (int lcv = 0; lcv < genericTypeParams.length; lcv++) {
         Utilities.AnnotationInformation ai = getParamInformation(paramAnnotations[lcv]);
         retVal.add(new SystemInjecteeImpl(genericTypeParams[lcv], ai.qualifiers, lcv, c, ai.optional, ai.self, ai.unqualified, injecteeDescriptor));
      }

      return retVal;
   }

   public static List<SystemInjecteeImpl> getMethodInjectees(Class<?> actualClass, Method c, ActiveDescriptor<?> injecteeDescriptor) {
      return getMethodInjectees(actualClass, c, injecteeDescriptor, Collections.emptyMap());
   }

   public static List<SystemInjecteeImpl> getMethodInjectees(
      Class<?> actualClass, Method c, ActiveDescriptor<?> injecteeDescriptor, Map<Integer, MethodParameter> knownValues
   ) {
      Type[] genericTypeParams = c.getGenericParameterTypes();
      Annotation[][] paramAnnotations = c.getParameterAnnotations();
      List<SystemInjecteeImpl> retVal = new ArrayList<>();
      Class<?> declaringClass = c.getDeclaringClass();

      for (int lcv = 0; lcv < genericTypeParams.length; lcv++) {
         if (knownValues.containsKey(lcv)) {
            retVal.add(null);
         } else {
            Utilities.AnnotationInformation ai = getParamInformation(paramAnnotations[lcv]);
            Type adjustedType = ReflectionHelper.resolveMember(actualClass, genericTypeParams[lcv], declaringClass);
            retVal.add(new SystemInjecteeImpl(adjustedType, ai.qualifiers, lcv, c, ai.optional, ai.self, ai.unqualified, injecteeDescriptor));
         }
      }

      return retVal;
   }

   private static Set<Annotation> getFieldAdjustedQualifierAnnotations(Field f, Set<Annotation> qualifiers) {
      Named n = f.getAnnotation(Named.class);
      if (n == null) {
         return qualifiers;
      } else if (n.value() != null && !"".equals(n.value())) {
         return qualifiers;
      } else {
         HashSet<Annotation> retVal = new HashSet<>();

         for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Named.class)) {
               retVal.add(new NamedImpl(f.getName()));
            } else {
               retVal.add(qualifier);
            }
         }

         return retVal;
      }
   }

   public static List<SystemInjecteeImpl> getFieldInjectees(Class<?> actualClass, Field f, ActiveDescriptor<?> injecteeDescriptor) {
      List<SystemInjecteeImpl> retVal = new LinkedList<>();
      Utilities.AnnotationInformation ai = getParamInformation(f.getAnnotations());
      Type adjustedType = ReflectionHelper.resolveField(actualClass, f);
      retVal.add(
         new SystemInjecteeImpl(
            adjustedType, getFieldAdjustedQualifierAnnotations(f, ai.qualifiers), -1, f, ai.optional, ai.self, ai.unqualified, injecteeDescriptor
         )
      );
      return retVal;
   }

   public static void validateSelfInjectees(ActiveDescriptor<?> givenDescriptor, List<SystemInjecteeImpl> injectees, Collector collector) {
      for (Injectee injectee : injectees) {
         if (injectee.isSelf()) {
            Class<?> requiredRawClass = ReflectionHelper.getRawClass(injectee.getRequiredType());
            if (requiredRawClass == null || !ActiveDescriptor.class.equals(requiredRawClass)) {
               collector.addThrowable(new IllegalArgumentException("Injection point " + injectee + " does not have the required type of ActiveDescriptor"));
            }

            if (injectee.isOptional()) {
               collector.addThrowable(new IllegalArgumentException("Injection point " + injectee + " is marked both @Optional and @Self"));
            }

            if (!injectee.getRequiredQualifiers().isEmpty()) {
               collector.addThrowable(new IllegalArgumentException("Injection point " + injectee + " is marked @Self but has other qualifiers"));
            }

            if (givenDescriptor == null) {
               collector.addThrowable(
                  new IllegalArgumentException(
                     "A class with injection point " + injectee + " is being created or injected via the non-managed ServiceLocator API"
                  )
               );
            }
         }
      }
   }

   public static Set<Annotation> fixAndCheckQualifiers(Annotation[] qualifiers, String name) {
      Set<Annotation> retVal = new HashSet<>();
      Set<String> dupChecker = new HashSet<>();
      Named named = null;

      for (Annotation qualifier : qualifiers) {
         String annotationType = qualifier.annotationType().getName();
         if (dupChecker.contains(annotationType)) {
            throw new IllegalArgumentException(annotationType + " appears more than once in the qualifier list");
         }

         dupChecker.add(annotationType);
         retVal.add(qualifier);
         if (qualifier instanceof Named) {
            named = (Named)qualifier;
            if (named.value().equals("")) {
               throw new IllegalArgumentException("The @Named qualifier must have a value");
            }

            if (name != null && !name.equals(named.value())) {
               throw new IllegalArgumentException(
                  "The name passed to the method (" + name + ") does not match the value of the @Named qualifier (" + named.value() + ")"
               );
            }
         }
      }

      if (named == null && name != null) {
         retVal.add(new NamedImpl(name));
      }

      return retVal;
   }

   public static <T> T createService(ActiveDescriptor<T> root, Injectee injectee, ServiceLocatorImpl locator, ServiceHandle<T> handle, Class<?> requestedClass) {
      if (root == null) {
         throw new IllegalArgumentException();
      } else {
         T service = null;
         if (!root.isReified()) {
            root = (ActiveDescriptor<T>)locator.reifyDescriptor(root, injectee);
         }

         if (isProxiable(root, injectee)) {
            if (!proxiesAvailable()) {
               throw new IllegalStateException("A descriptor " + root + " requires a proxy, but the proxyable library is not on the classpath");
            } else {
               return locator.getPerLocatorUtilities().getProxyUtilities().generateProxy(requestedClass, locator, root, (ServiceHandleImpl<T>)handle, injectee);
            }
         } else {
            Context<?> context;
            try {
               context = locator.resolveContext(root.getScopeAnnotation());
            } catch (Throwable var12) {
               if (injectee != null && injectee.isOptional()) {
                  return null;
               }

               Exception addMe = new IllegalStateException(
                  "While attempting to create a service for " + root + " in scope " + root.getScope() + " an error occured while locating the context"
               );
               if (var12 instanceof MultiException) {
                  MultiException me = (MultiException)var12;
                  me.addError(addMe);
                  throw me;
               }

               MultiException me = new MultiException(var12);
               me.addError(addMe);
               throw me;
            }

            try {
               service = (T)context.findOrCreate(root, handle);
            } catch (MultiException var10) {
               throw var10;
            } catch (Throwable var11) {
               throw new MultiException(var11);
            }

            if (service == null && !context.supportsNullCreation()) {
               throw new MultiException(
                  new IllegalStateException("Context " + context + " findOrCreate returned a null for descriptor " + root + " and handle " + handle)
               );
            } else {
               return service;
            }
         }
      }
   }

   static Utilities.Interceptors getAllInterceptors(ServiceLocatorImpl impl, ActiveDescriptor<?> descriptor, Class<?> clazz, Constructor<?> c) {
      if (descriptor != null && clazz != null && !isFinal(clazz)) {
         ClassReflectionHelper crh = impl.getClassReflectionHelper();
         List<InterceptionService> interceptionServices = impl.getInterceptionServices();
         if (interceptionServices != null && !interceptionServices.isEmpty()) {
            for (String contract : descriptor.getAdvertisedContracts()) {
               if (NOT_INTERCEPTED.contains(contract)) {
                  return EMTPY_INTERCEPTORS;
               }
            }

            final LinkedHashMap<Method, List<MethodInterceptor>> retVal = new LinkedHashMap<>();
            final ArrayList<ConstructorInterceptor> cRetVal = new ArrayList<>();

            for (InterceptionService interceptionService : interceptionServices) {
               Filter filter = interceptionService.getDescriptorFilter();
               if (BuilderHelper.filterMatches(descriptor, filter)) {
                  for (MethodWrapper methodWrapper : crh.getAllMethods(clazz)) {
                     Method method = methodWrapper.getMethod();
                     if (!isFinal(method)) {
                        List<MethodInterceptor> interceptors = interceptionService.getMethodInterceptors(method);
                        if (interceptors != null && !interceptors.isEmpty()) {
                           List<MethodInterceptor> addToMe = retVal.get(method);
                           if (addToMe == null) {
                              addToMe = new ArrayList<>();
                              retVal.put(method, addToMe);
                           }

                           addToMe.addAll(interceptors);
                        }
                     }
                  }

                  List<ConstructorInterceptor> cInterceptors = interceptionService.getConstructorInterceptors(c);
                  if (cInterceptors != null && !cInterceptors.isEmpty()) {
                     cRetVal.addAll(cInterceptors);
                  }
               }
            }

            return new Utilities.Interceptors() {
               @Override
               public Map<Method, List<MethodInterceptor>> getMethodInterceptors() {
                  return retVal;
               }

               @Override
               public List<ConstructorInterceptor> getConstructorInterceptors() {
                  return cRetVal;
               }
            };
         } else {
            return EMTPY_INTERCEPTORS;
         }
      } else {
         return EMTPY_INTERCEPTORS;
      }
   }

   public static boolean isTypeSafe(Type requiredType, Type beanType) {
      if (TypeChecker.isRawTypeSafe(requiredType, beanType)) {
         return true;
      } else {
         Class<?> requiredClass = ReflectionHelper.getRawClass(requiredType);
         if (requiredClass == null) {
            return false;
         } else if (!requiredClass.isAnnotation()) {
            return false;
         } else {
            Class<?> beanClass = ReflectionHelper.getRawClass(beanType);
            if (beanClass == null) {
               return false;
            } else if (beanClass.isAnnotationPresent((Class<? extends Annotation>)requiredClass)) {
               return true;
            } else {
               Class<? extends Annotation> trueScope = getScopeAnnotationType(beanClass, null);
               return trueScope.equals(requiredClass);
            }
         }
      }
   }

   public static synchronized boolean proxiesAvailable() {
      if (proxiesAvailable != null) {
         return proxiesAvailable;
      } else {
         ClassLoader loader = Utilities.class.getClassLoader();
         if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
         }

         try {
            loader.loadClass("javassist.util.proxy.MethodHandler");
            proxiesAvailable = true;
            return true;
         } catch (Throwable var2) {
            proxiesAvailable = false;
            return false;
         }
      }
   }

   static {
      NOT_INTERCEPTED.add(ServiceLocator.class.getName());
      NOT_INTERCEPTED.add(InstanceLifecycleListener.class.getName());
      NOT_INTERCEPTED.add(InjectionResolver.class.getName());
      NOT_INTERCEPTED.add(ErrorService.class.getName());
      NOT_INTERCEPTED.add(ClassAnalyzer.class.getName());
      NOT_INTERCEPTED.add(DynamicConfigurationListener.class.getName());
      NOT_INTERCEPTED.add(DynamicConfigurationService.class.getName());
      NOT_INTERCEPTED.add(InterceptionService.class.getName());
      NOT_INTERCEPTED.add(ValidationService.class.getName());
      NOT_INTERCEPTED.add(Context.class.getName());
   }

   private static class AnnotationInformation {
      private final Set<Annotation> qualifiers;
      private final boolean optional;
      private final boolean self;
      private final Unqualified unqualified;

      private AnnotationInformation(Set<Annotation> qualifiers, boolean optional, boolean self, Unqualified unqualified) {
         this.qualifiers = qualifiers;
         this.optional = optional;
         this.self = self;
         this.unqualified = unqualified;
      }
   }

   public interface Interceptors {
      Map<Method, List<MethodInterceptor>> getMethodInterceptors();

      List<ConstructorInterceptor> getConstructorInterceptors();
   }
}
