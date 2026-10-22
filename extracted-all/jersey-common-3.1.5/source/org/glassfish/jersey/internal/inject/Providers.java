package org.glassfish.jersey.internal.inject;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.JerseyPriorities;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.model.internal.RankedProvider;
import org.glassfish.jersey.spi.Contract;

public final class Providers {
   private static final Logger LOGGER = Logger.getLogger(Providers.class.getName());
   private static final Map<Class<?>, Providers.ProviderRuntime> JAX_RS_PROVIDER_INTERFACE_WHITELIST = getJaxRsProviderInterfaces();
   private static final Map<Class<?>, Providers.ProviderRuntime> EXTERNAL_PROVIDER_INTERFACE_WHITELIST = getExternalProviderInterfaces();

   private Providers() {
   }

   private static Map<Class<?>, Providers.ProviderRuntime> getJaxRsProviderInterfaces() {
      Map<Class<?>, Providers.ProviderRuntime> interfaces = new HashMap<>();
      interfaces.put(ContextResolver.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(ExceptionMapper.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(MessageBodyReader.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(MessageBodyWriter.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(ReaderInterceptor.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(WriterInterceptor.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(ParamConverterProvider.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(ContainerRequestFilter.class, Providers.ProviderRuntime.SERVER);
      interfaces.put(ContainerResponseFilter.class, Providers.ProviderRuntime.SERVER);
      interfaces.put(DynamicFeature.class, Providers.ProviderRuntime.SERVER);
      interfaces.put(ClientResponseFilter.class, Providers.ProviderRuntime.CLIENT);
      interfaces.put(ClientRequestFilter.class, Providers.ProviderRuntime.CLIENT);
      interfaces.put(RxInvokerProvider.class, Providers.ProviderRuntime.CLIENT);
      return interfaces;
   }

   private static Map<Class<?>, Providers.ProviderRuntime> getExternalProviderInterfaces() {
      Map<Class<?>, Providers.ProviderRuntime> interfaces = new HashMap<>();
      interfaces.putAll(JAX_RS_PROVIDER_INTERFACE_WHITELIST);
      interfaces.put(Feature.class, Providers.ProviderRuntime.BOTH);
      interfaces.put(Binder.class, Providers.ProviderRuntime.BOTH);
      return interfaces;
   }

   public static <T> Set<T> getProviders(InjectionManager injectionManager, Class<T> contract) {
      Collection<ServiceHolder<T>> providers = getServiceHolders(injectionManager, contract);
      return getProviderClasses(providers);
   }

   public static <T> Set<T> getCustomProviders(InjectionManager injectionManager, Class<T> contract) {
      List<ServiceHolder<T>> providers = getServiceHolders(
         injectionManager, contract, Comparator.comparingInt(Providers::getPriority), CustomAnnotationLiteral.INSTANCE
      );
      return getProviderClasses(providers);
   }

   public static <T> Iterable<T> getAllProviders(InjectionManager injectionManager, Class<T> contract) {
      return getAllProviders(injectionManager, contract, (Comparator<T>)null);
   }

   public static <T> Iterable<RankedProvider<T>> getAllRankedProviders(InjectionManager injectionManager, Class<T> contract) {
      List<ServiceHolder<T>> providers = getServiceHolders(injectionManager, contract, CustomAnnotationLiteral.INSTANCE);
      providers.addAll(getServiceHolders(injectionManager, contract));
      LinkedHashMap<Class<T>, RankedProvider<T>> providerMap = new LinkedHashMap<>();

      for (ServiceHolder<T> provider : providers) {
         Class<T> implClass = getImplementationClass(contract, provider);
         if (!providerMap.containsKey(implClass)) {
            Set<Type> contracts = isProxyGenerated(contract, provider) ? provider.getContractTypes() : null;
            providerMap.put(implClass, new RankedProvider<>(provider.getInstance(), provider.getRank(), contracts));
         }
      }

      return providerMap.values();
   }

   public static <T> Iterable<T> sortRankedProviders(RankedComparator<T> comparator, Iterable<RankedProvider<T>> providers) {
      return StreamSupport.stream(providers.spliterator(), false).sorted(comparator).map(RankedProvider::getProvider).collect(Collectors.toList());
   }

   public static <T> Iterable<T> getAllRankedSortedProviders(InjectionManager injectionManager, Class<T> contract) {
      Iterable<RankedProvider<T>> allRankedProviders = getAllRankedProviders(injectionManager, contract);
      return sortRankedProviders(new RankedComparator<>(), allRankedProviders);
   }

   public static <T> Iterable<T> mergeAndSortRankedProviders(RankedComparator<T> comparator, Iterable<Iterable<RankedProvider<T>>> providerIterables) {
      return StreamSupport.stream(providerIterables.spliterator(), false)
         .flatMap(rankedProviders -> StreamSupport.stream(rankedProviders.spliterator(), false))
         .sorted(comparator)
         .map(RankedProvider::getProvider)
         .collect(Collectors.toList());
   }

   public static <T> Iterable<T> getAllProviders(InjectionManager injectionManager, Class<T> contract, RankedComparator<T> comparator) {
      return sortRankedProviders(comparator, getAllRankedProviders(injectionManager, contract));
   }

   public static <T> Collection<ServiceHolder<T>> getAllServiceHolders(InjectionManager injectionManager, Class<T> contract) {
      List<ServiceHolder<T>> providers = getServiceHolders(
         injectionManager, contract, Comparator.comparingInt(Providers::getPriority), CustomAnnotationLiteral.INSTANCE
      );
      providers.addAll(getServiceHolders(injectionManager, contract));
      LinkedHashMap<Class<T>, ServiceHolder<T>> providerMap = new LinkedHashMap<>();

      for (ServiceHolder<T> provider : providers) {
         Class<T> implClass = getImplementationClass(contract, provider);
         if (!providerMap.containsKey(implClass)) {
            providerMap.put(implClass, provider);
         }
      }

      return providerMap.values();
   }

   private static <T> List<ServiceHolder<T>> getServiceHolders(InjectionManager bm, Class<T> contract, Annotation... qualifiers) {
      return bm.getAllServiceHolders(contract, qualifiers);
   }

   private static <T> List<ServiceHolder<T>> getServiceHolders(
      InjectionManager injectionManager, Class<T> contract, Comparator<Class<?>> objectComparator, Annotation... qualifiers
   ) {
      List<ServiceHolder<T>> serviceHolders = injectionManager.getAllServiceHolders(contract, qualifiers);
      serviceHolders.sort(
         (o1, o2) -> objectComparator.compare(getImplementationClass(contract, (ServiceHolder<T>)o1), getImplementationClass(contract, (ServiceHolder<T>)o2))
      );
      return serviceHolders;
   }

   public static boolean isJaxRsProvider(Class<?> clazz) {
      for (Class<?> providerType : JAX_RS_PROVIDER_INTERFACE_WHITELIST.keySet()) {
         if (providerType.isAssignableFrom(clazz)) {
            return true;
         }
      }

      return false;
   }

   public static <T> Iterable<T> getAllProviders(InjectionManager injectionManager, Class<T> contract, Comparator<T> comparator) {
      List<T> providerList = new ArrayList<>(getProviderClasses(getAllServiceHolders(injectionManager, contract)));
      if (comparator != null) {
         providerList.sort(comparator);
      }

      return providerList;
   }

   private static <T> Set<T> getProviderClasses(Collection<ServiceHolder<T>> providers) {
      return providers.stream().map(Providers::holder2service).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   private static <T> T holder2service(ServiceHolder<T> holder) {
      return holder != null ? holder.getInstance() : null;
   }

   private static int getPriority(Class<?> serviceClass) {
      return JerseyPriorities.getPriorityValue(serviceClass, 5000);
   }

   private static <T> Class<T> getImplementationClass(Class<T> contract, ServiceHolder<T> serviceHolder) {
      return isProxyGenerated(contract, serviceHolder)
         ? serviceHolder.getContractTypes()
            .stream()
            .filter(a -> Class.class.isInstance(a))
            .map(a -> (Class<T>)a)
            .reduce(contract, (a, b) -> a.isAssignableFrom(b) ? b : a)
         : serviceHolder.getImplementationClass();
   }

   private static <T> boolean isProxyGenerated(Class<T> contract, ServiceHolder<T> serviceHolder) {
      return !contract.isAssignableFrom(serviceHolder.getImplementationClass());
   }

   public static Set<Class<?>> getProviderContracts(Class<?> clazz) {
      Set<Class<?>> contracts = Collections.newSetFromMap(new IdentityHashMap<>());
      computeProviderContracts(clazz, contracts);
      return contracts;
   }

   private static void computeProviderContracts(Class<?> clazz, Set<Class<?>> contracts) {
      for (Class<?> contract : getImplementedContracts(clazz)) {
         if (isSupportedContract(contract)) {
            contracts.add(contract);
         }

         computeProviderContracts(contract, contracts);
      }
   }

   public static boolean checkProviderRuntime(Class<?> component, ContractProvider model, RuntimeType runtimeConstraint, boolean scanned, boolean isResource) {
      Set<Class<?>> contracts = model.getContracts();
      ConstrainedTo constrainedTo = component.getAnnotation(ConstrainedTo.class);
      RuntimeType componentConstraint = constrainedTo == null ? null : constrainedTo.value();
      if (Feature.class.isAssignableFrom(component)) {
         return true;
      } else {
         StringBuilder warnings = new StringBuilder();

         boolean var19;
         try {
            boolean foundComponentCompatible = componentConstraint == null;
            boolean foundRuntimeCompatibleContract = isResource && runtimeConstraint == RuntimeType.SERVER;

            for (Class<?> contract : contracts) {
               RuntimeType contractConstraint = getContractConstraint(contract, componentConstraint);
               foundRuntimeCompatibleContract |= contractConstraint == null || contractConstraint == runtimeConstraint;
               if (componentConstraint != null) {
                  if (contractConstraint != componentConstraint) {
                     warnings.append(
                           LocalizationMessages.WARNING_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(
                              component.getName(), componentConstraint.name(), contract.getName(), contractConstraint.name()
                           )
                        )
                        .append(" ");
                  } else {
                     foundComponentCompatible = true;
                  }
               }
            }

            if (!foundComponentCompatible) {
               warnings.append(LocalizationMessages.ERROR_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(component.getName(), componentConstraint.name())).append(" ");
               logProviderSkipped(warnings, component, isResource);
               return false;
            }

            boolean isProviderRuntimeCompatible = componentConstraint == null || componentConstraint == runtimeConstraint;
            if (!isProviderRuntimeCompatible && !scanned) {
               warnings.append(
                     LocalizationMessages.ERROR_PROVIDER_CONSTRAINED_TO_WRONG_RUNTIME(component.getName(), componentConstraint.name(), runtimeConstraint.name())
                  )
                  .append(" ");
               logProviderSkipped(warnings, component, isResource);
            }

            if (foundRuntimeCompatibleContract || scanned) {
               return isProviderRuntimeCompatible && foundRuntimeCompatibleContract;
            }

            warnings.append(LocalizationMessages.ERROR_PROVIDER_REGISTERED_WRONG_RUNTIME(component.getName(), runtimeConstraint.name())).append(" ");
            logProviderSkipped(warnings, component, isResource);
            var19 = false;
         } finally {
            if (warnings.length() > 0) {
               LOGGER.log(Level.WARNING, warnings.toString());
            }
         }

         return var19;
      }
   }

   private static void logProviderSkipped(StringBuilder sb, Class<?> provider, boolean alsoResourceClass) {
      sb.append(
            alsoResourceClass
               ? LocalizationMessages.ERROR_PROVIDER_AND_RESOURCE_CONSTRAINED_TO_IGNORED(provider.getName())
               : LocalizationMessages.ERROR_PROVIDER_CONSTRAINED_TO_IGNORED(provider.getName())
         )
         .append(" ");
   }

   public static boolean isSupportedContract(Class<?> type) {
      return EXTERNAL_PROVIDER_INTERFACE_WHITELIST.get(type) != null || type.isAnnotationPresent(Contract.class);
   }

   private static RuntimeType getContractConstraint(Class<?> clazz, RuntimeType defaultConstraint) {
      Providers.ProviderRuntime jaxRsProvider = EXTERNAL_PROVIDER_INTERFACE_WHITELIST.get(clazz);
      RuntimeType result = null;
      if (jaxRsProvider != null) {
         result = jaxRsProvider.getRuntime();
      } else if (clazz.getAnnotation(Contract.class) != null) {
         ConstrainedTo constrainedToAnnotation = clazz.getAnnotation(ConstrainedTo.class);
         if (constrainedToAnnotation != null) {
            result = constrainedToAnnotation.value();
         }
      }

      return result == null ? defaultConstraint : result;
   }

   private static Iterable<Class<?>> getImplementedContracts(Class<?> clazz) {
      Collection<Class<?>> list = new LinkedList<>();
      Collections.addAll(list, clazz.getInterfaces());
      Class<?> superclass = clazz.getSuperclass();
      if (superclass != null) {
         list.add(superclass);
      }

      return list;
   }

   public static boolean isProvider(Class<?> clazz) {
      return findFirstProviderContract(clazz);
   }

   public static void ensureContract(Class<?> contract, Class<?>... implementations) {
      if (implementations != null && implementations.length > 0) {
         StringBuilder invalidClassNames = new StringBuilder();

         for (Class<?> impl : implementations) {
            if (!contract.isAssignableFrom(impl)) {
               if (invalidClassNames.length() > 0) {
                  invalidClassNames.append(", ");
               }

               invalidClassNames.append(impl.getName());
            }
         }

         if (invalidClassNames.length() > 0) {
            throw new IllegalArgumentException(LocalizationMessages.INVALID_SPI_CLASSES(contract.getName(), invalidClassNames.toString()));
         }
      }
   }

   private static boolean findFirstProviderContract(Class<?> clazz) {
      for (Class<?> contract : getImplementedContracts(clazz)) {
         if (isSupportedContract(contract)) {
            return true;
         }

         if (findFirstProviderContract(contract)) {
            return true;
         }
      }

      return false;
   }

   private static enum ProviderRuntime {
      BOTH(null),
      SERVER(RuntimeType.SERVER),
      CLIENT(RuntimeType.CLIENT);

      private final RuntimeType runtime;

      private ProviderRuntime(RuntimeType runtime) {
         this.runtime = runtime;
      }

      public RuntimeType getRuntime() {
         return this.runtime;
      }
   }
}
