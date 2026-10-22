package org.glassfish.jersey.model.internal;

import jakarta.annotation.Priority;
import jakarta.inject.Scope;
import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.core.Feature;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;

public class ComponentBag {
   private static final Predicate<ContractProvider> EXCLUDE_META_PROVIDERS = model -> {
      Set<Class<?>> contracts = model.getContracts();
      if (contracts.isEmpty()) {
         return true;
      } else {
         byte count = 0;
         if (contracts.contains(Feature.class)) {
            count++;
         }

         if (contracts.contains(Binder.class)) {
            count++;
         }

         return contracts.size() > count;
      }
   };
   private static final Function<Object, Binder> CAST_TO_BINDER = Binder.class::cast;
   public static final BiPredicate<ContractProvider, InjectionManager> EXTERNAL_ONLY = (model, injectionManager) -> model.getImplementationClass() != null
         && injectionManager.isRegistrable(model.getImplementationClass());
   public static final Predicate<ContractProvider> BINDERS_ONLY = model -> model.getContracts().contains(Binder.class);
   public static final Predicate<ContractProvider> EXECUTOR_SERVICE_PROVIDER_ONLY = model -> model.getContracts().contains(ExecutorServiceProvider.class)
         && !model.getContracts().contains(ScheduledExecutorServiceProvider.class);
   public static final Predicate<ContractProvider> SCHEDULED_EXECUTOR_SERVICE_PROVIDER_ONLY = model -> model.getContracts()
         .contains(ScheduledExecutorServiceProvider.class);
   public static final Predicate<ContractProvider> EXCLUDE_EMPTY = model -> !model.getContracts().isEmpty();
   public static final Predicate<ContractProvider> INCLUDE_ALL = contractProvider -> true;
   static final Inflector<ContractProvider.Builder, ContractProvider> AS_IS = ContractProvider.Builder::build;
   private final Predicate<ContractProvider> registrationStrategy;
   private final Set<Class<?>> classes;
   private final Set<Class<?>> classesView;
   private final Set<Object> instances;
   private final Set<Object> instancesView;
   private final Map<Class<?>, ContractProvider> models;
   private final Set<Class<?>> modelKeysView;

   public static Predicate<ContractProvider> excludeMetaProviders(InjectionManager injectionManager) {
      return EXCLUDE_META_PROVIDERS.and(model -> !injectionManager.isRegistrable(model.getImplementationClass()));
   }

   public static ComponentBag newInstance(Predicate<ContractProvider> registrationStrategy) {
      return new ComponentBag(registrationStrategy);
   }

   public static <T> List<T> getFromBinders(InjectionManager injectionManager, ComponentBag componentBag, Function<Object, T> cast, Predicate<Binding> filter) {
      Function<Binding, Object> bindingToObject = binding -> {
         if (binding instanceof ClassBinding) {
            ClassBinding classBinding = (ClassBinding)binding;
            return injectionManager.createAndInitialize(classBinding.getService());
         } else {
            InstanceBinding instanceBinding = (InstanceBinding)binding;
            return instanceBinding.getService();
         }
      };
      return componentBag.getInstances(BINDERS_ONLY)
         .stream()
         .map(CAST_TO_BINDER)
         .flatMap(binder -> Bindings.getBindings(injectionManager, binder).stream())
         .filter(filter)
         .map(bindingToObject)
         .map(cast)
         .collect(Collectors.toList());
   }

   private ComponentBag(Predicate<ContractProvider> registrationStrategy) {
      this.registrationStrategy = registrationStrategy;
      this.classes = new LinkedHashSet<>();
      this.instances = new LinkedHashSet<>();
      this.models = new IdentityHashMap<>();
      this.classesView = Collections.unmodifiableSet(this.classes);
      this.instancesView = Collections.unmodifiableSet(this.instances);
      this.modelKeysView = Collections.unmodifiableSet(this.models.keySet());
   }

   private ComponentBag(Predicate<ContractProvider> registrationStrategy, Set<Class<?>> classes, Set<Object> instances, Map<Class<?>, ContractProvider> models) {
      this.registrationStrategy = registrationStrategy;
      this.classes = classes;
      this.instances = instances;
      this.models = models;
      this.classesView = Collections.unmodifiableSet(classes);
      this.instancesView = Collections.unmodifiableSet(instances);
      this.modelKeysView = Collections.unmodifiableSet(models.keySet());
   }

   public boolean register(Class<?> componentClass, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      boolean result = this.registerModel(componentClass, -1, null, modelEnhancer);
      if (result) {
         this.classes.add(componentClass);
      }

      return result;
   }

   public boolean register(Class<?> componentClass, int priority, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      boolean result = this.registerModel(componentClass, priority, null, modelEnhancer);
      if (result) {
         this.classes.add(componentClass);
      }

      return result;
   }

   public boolean register(Class<?> componentClass, Set<Class<?>> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      boolean result = this.registerModel(componentClass, -1, asMap(contracts), modelEnhancer);
      if (result) {
         this.classes.add(componentClass);
      }

      return result;
   }

   public boolean register(Class<?> componentClass, Map<Class<?>, Integer> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      boolean result = this.registerModel(componentClass, -1, contracts, modelEnhancer);
      if (result) {
         this.classes.add(componentClass);
      }

      return result;
   }

   public boolean register(Object component, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      Class<?> componentClass = component.getClass();
      boolean result = this.registerModel(componentClass, -1, null, modelEnhancer);
      if (result) {
         this.instances.add(component);
      }

      return result;
   }

   public boolean register(Object component, int priority, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      Class<?> componentClass = component.getClass();
      boolean result = this.registerModel(componentClass, priority, null, modelEnhancer);
      if (result) {
         this.instances.add(component);
      }

      return result;
   }

   public boolean register(Object component, Set<Class<?>> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      Class<?> componentClass = component.getClass();
      boolean result = this.registerModel(componentClass, -1, asMap(contracts), modelEnhancer);
      if (result) {
         this.instances.add(component);
      }

      return result;
   }

   public boolean register(Object component, Map<Class<?>, Integer> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
      Class<?> componentClass = component.getClass();
      boolean result = this.registerModel(componentClass, -1, contracts, modelEnhancer);
      if (result) {
         this.instances.add(component);
      }

      return result;
   }

   private boolean registerModel(
      Class<?> componentClass, int defaultPriority, Map<Class<?>, Integer> contractMap, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer
   ) {
      return Errors.process((Producer<Boolean>)(() -> {
         if (this.models.containsKey(componentClass)) {
            Errors.error(LocalizationMessages.COMPONENT_TYPE_ALREADY_REGISTERED(componentClass), Severity.HINT);
            return false;
         } else {
            ContractProvider model = modelFor(componentClass, defaultPriority, contractMap, modelEnhancer);
            if (!this.registrationStrategy.test(model)) {
               return false;
            } else {
               this.models.put(componentClass, model);
               return true;
            }
         }
      }));
   }

   public static ContractProvider modelFor(Class<?> componentClass) {
      return modelFor(componentClass, -1, null, AS_IS);
   }

   private static ContractProvider modelFor(
      Class<?> componentClass, int defaultPriority, Map<Class<?>, Integer> contractMap, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer
   ) {
      Map<Class<?>, Integer> contracts;
      if (contractMap == null) {
         contracts = asMap(Providers.getProviderContracts(componentClass));
      } else {
         contracts = new HashMap<>(contractMap);
         Iterator<Class<?>> it = contracts.keySet().iterator();

         while (it.hasNext()) {
            Class<?> contract = it.next();
            if (contract == null) {
               it.remove();
            } else {
               boolean failed = false;
               if (!Providers.isSupportedContract(contract)) {
                  Errors.error(LocalizationMessages.CONTRACT_NOT_SUPPORTED(contract, componentClass), Severity.WARNING);
                  failed = true;
               }

               if (!contract.isAssignableFrom(componentClass)) {
                  Errors.error(LocalizationMessages.CONTRACT_NOT_ASSIGNABLE(contract, componentClass), Severity.WARNING);
                  failed = true;
               }

               if (failed) {
                  it.remove();
               }
            }
         }
      }

      ContractProvider.Builder builder = ContractProvider.builder(componentClass).addContracts(contracts).defaultPriority(defaultPriority);
      boolean useAnnotationPriority = defaultPriority == -1;

      for (Annotation annotation : componentClass.getAnnotations()) {
         if (annotation instanceof Priority) {
            if (useAnnotationPriority) {
               builder.defaultPriority(((Priority)annotation).value());
            }
         } else {
            for (Annotation metaAnnotation : annotation.annotationType().getAnnotations()) {
               if (metaAnnotation instanceof NameBinding) {
                  builder.addNameBinding(annotation.annotationType());
               }

               if (metaAnnotation instanceof Scope) {
                  builder.scope(annotation.annotationType());
               }
            }
         }
      }

      return modelEnhancer.apply(builder);
   }

   private static Map<Class<?>, Integer> asMap(Set<Class<?>> contractSet) {
      Map<Class<?>, Integer> contracts = new IdentityHashMap<>();

      for (Class<?> contract : contractSet) {
         contracts.put(contract, -1);
      }

      return contracts;
   }

   public Set<Class<?>> getClasses() {
      return this.classesView;
   }

   public Set<Object> getInstances() {
      return this.instancesView;
   }

   public Set<Class<?>> getClasses(Predicate<ContractProvider> filter) {
      return this.classesView.stream().filter(input -> {
         ContractProvider model = this.getModel((Class<?>)input);
         return filter.test(model);
      }).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public Set<Object> getInstances(Predicate<ContractProvider> filter) {
      return this.instancesView.stream().filter(input -> {
         ContractProvider model = this.getModel(input.getClass());
         return filter.test(model);
      }).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public Set<Class<?>> getRegistrations() {
      return this.modelKeysView;
   }

   public ContractProvider getModel(Class<?> componentClass) {
      return this.models.get(componentClass);
   }

   public ComponentBag copy() {
      return new ComponentBag(
         this.registrationStrategy, new LinkedHashSet<>(this.classes), new LinkedHashSet<>(this.instances), new IdentityHashMap<>(this.models)
      );
   }

   public ComponentBag immutableCopy() {
      return new ComponentBag.ImmutableComponentBag(this);
   }

   public void clear() {
      this.classes.clear();
      this.instances.clear();
      this.models.clear();
   }

   void loadFrom(ComponentBag bag) {
      this.clear();
      this.classes.addAll(bag.classes);
      this.instances.addAll(bag.instances);
      this.models.putAll(bag.models);
   }

   private static class ImmutableComponentBag extends ComponentBag {
      ImmutableComponentBag(ComponentBag original) {
         super(
            original.registrationStrategy,
            new LinkedHashSet<>(original.classes),
            new LinkedHashSet<>(original.instances),
            new IdentityHashMap<>(original.models)
         );
      }

      @Override
      public boolean register(Class<?> componentClass, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public boolean register(Class<?> componentClass, int priority, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public boolean register(Class<?> componentClass, Set<Class<?>> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public boolean register(Class<?> componentClass, Map<Class<?>, Integer> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public boolean register(Object component, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public boolean register(Object component, int priority, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public boolean register(Object component, Set<Class<?>> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public boolean register(Object component, Map<Class<?>, Integer> contracts, Inflector<ContractProvider.Builder, ContractProvider> modelEnhancer) {
         throw new IllegalStateException("This instance is read-only.");
      }

      @Override
      public ComponentBag copy() {
         return this;
      }

      @Override
      public ComponentBag immutableCopy() {
         return this;
      }

      @Override
      public void clear() {
         throw new IllegalStateException("This instance is read-only.");
      }
   }
}
