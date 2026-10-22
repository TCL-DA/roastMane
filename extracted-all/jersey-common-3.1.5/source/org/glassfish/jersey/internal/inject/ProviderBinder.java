package org.glassfish.jersey.internal.inject;

import jakarta.inject.Singleton;
import jakarta.ws.rs.RuntimeType;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.spi.ComponentProvider;

public class ProviderBinder {
   private final InjectionManager injectionManager;

   public ProviderBinder(InjectionManager injectionManager) {
      this.injectionManager = injectionManager;
   }

   public static <T> void bindProvider(Class<T> providerClass, ContractProvider model, InjectionManager injectionManager) {
      T instance = injectionManager.getInstance(providerClass);
      if (instance != null) {
         injectionManager.register(createInstanceBinder(instance, model));
      } else {
         injectionManager.register(createClassBinder(model));
      }
   }

   private static <T> Binder createInstanceBinder(final T instance, final ContractProvider model) {
      return new AbstractBinder() {
         @Override
         protected void configure() {
            InstanceBinding binding = this.bind(instance).in(model.getScope()).qualifiedBy(CustomAnnotationLiteral.INSTANCE);
            binding.to(model.getContracts());
            int priority = model.getPriority(model.getImplementationClass());
            if (priority > -1) {
               binding.ranked(priority);
            }
         }
      };
   }

   private static Binder createClassBinder(final ContractProvider model) {
      return new AbstractBinder() {
         @Override
         protected void configure() {
            ClassBinding binding = (ClassBinding)((ClassBinding)this.bind(model.getImplementationClass()).in(model.getScope()))
               .qualifiedBy(CustomAnnotationLiteral.INSTANCE);
            binding.to(model.getContracts());
            int priority = model.getPriority(model.getImplementationClass());
            if (priority > -1) {
               binding.ranked(priority);
            }
         }
      };
   }

   private static Collection<Binder> createProviderBinders(Class<?> providerClass, ContractProvider model) {
      Function<Class, Binder> binderFunction = contract -> new AbstractBinder() {
            @Override
            protected void configure() {
               ClassBinding builder = (ClassBinding)((ClassBinding)((ClassBinding)this.bind(providerClass).in(model.getScope()))
                     .qualifiedBy(CustomAnnotationLiteral.INSTANCE))
                  .to(contract);
               int priority = model.getPriority(contract);
               if (priority > -1) {
                  builder.ranked(priority);
               }
            }
         };
      return model.getContracts().stream().map(binderFunction).collect(Collectors.toList());
   }

   public static void bindProvider(Object providerInstance, ContractProvider model, InjectionManager injectionManager) {
      injectionManager.register(createInstanceBinder(providerInstance, model));
   }

   private static Collection<Binder> createProviderBinders(Object providerInstance, ContractProvider model) {
      Function<Class, Binder> binderFunction = contract -> new AbstractBinder() {
            @Override
            protected void configure() {
               InstanceBinding builder = (InstanceBinding)((InstanceBinding)this.bind(providerInstance).qualifiedBy(CustomAnnotationLiteral.INSTANCE))
                  .to(contract);
               int priority = model.getPriority(contract);
               if (priority > -1) {
                  builder.ranked(priority);
               }
            }
         };
      return model.getContracts().stream().map(binderFunction).collect(Collectors.toList());
   }

   public static void bindProviders(ComponentBag componentBag, InjectionManager injectionManager) {
      bindProviders(componentBag, null, null, injectionManager, null);
   }

   @Deprecated
   public static void bindProviders(ComponentBag componentBag, RuntimeType constrainedTo, Set<Class<?>> registeredClasses, InjectionManager injectionManager) {
      bindProviders(componentBag, constrainedTo, registeredClasses, injectionManager, null);
   }

   public static void bindProviders(
      ComponentBag componentBag,
      RuntimeType constrainedTo,
      Set<Class<?>> registeredClasses,
      InjectionManager injectionManager,
      Collection<ComponentProvider> componentProviders
   ) {
      Predicate<ContractProvider> filter = ComponentBag.EXCLUDE_EMPTY.and(ComponentBag.excludeMetaProviders(injectionManager));
      Predicate<Class<?>> correctlyConfigured = componentClass -> Providers.checkProviderRuntime(
            componentClass,
            componentBag.getModel(componentClass),
            constrainedTo,
            registeredClasses == null || !registeredClasses.contains(componentClass),
            false
         );
      Collection<Binder> binderToRegister = new ArrayList<>();
      Set<Class<?>> classes = new LinkedHashSet<>(componentBag.getClasses(filter));
      if (constrainedTo != null) {
         classes = classes.stream().filter(correctlyConfigured).collect(Collectors.toSet());
      }

      for (Class<?> providerClass : classes) {
         ContractProvider model = componentBag.getModel(providerClass);
         if (componentProviders == null || !bindWithComponentProvider(providerClass, model, componentProviders)) {
            binderToRegister.addAll(createProviderBinders(providerClass, model));
         }
      }

      Set<Object> instances = componentBag.getInstances(filter);
      if (constrainedTo != null) {
         instances = instances.stream().filter(component -> correctlyConfigured.test(component.getClass())).collect(Collectors.toSet());
      }

      for (Object provider : instances) {
         ContractProvider model = componentBag.getModel(provider.getClass());
         binderToRegister.addAll(createProviderBinders(provider, model));
      }

      injectionManager.register((Binder)CompositeBinder.wrap(binderToRegister));
   }

   private static boolean bindWithComponentProvider(Class<?> component, ContractProvider providerModel, Iterable<ComponentProvider> componentProviders) {
      for (ComponentProvider provider : componentProviders) {
         if (provider.bind(component, providerModel)) {
            return true;
         }
      }

      return false;
   }

   private static <T> Collection<Binder> createInstanceBinders(T instance) {
      Function<Class, Binder> binderFunction = contract -> new AbstractBinder() {
            @Override
            protected void configure() {
               ((InstanceBinding)this.bind(instance).to(contract)).qualifiedBy(CustomAnnotationLiteral.INSTANCE);
            }
         };
      return Providers.getProviderContracts(instance.getClass()).stream().map(binderFunction).collect(Collectors.toList());
   }

   public void bindInstances(Iterable<Object> instances) {
      List<Object> instancesList = new ArrayList<>();
      instances.forEach(instancesList::add);
      this.bindInstances(instancesList);
   }

   public void bindInstances(Collection<Object> instances) {
      List<Binder> binders = instances.stream().map(ProviderBinder::createInstanceBinders).flatMap(Collection::stream).collect(Collectors.toList());
      this.injectionManager.register((Binder)CompositeBinder.wrap(binders));
   }

   public void bindClasses(Class<?>... classes) {
      this.bindClasses(Arrays.asList(classes), false);
   }

   public void bindClasses(Iterable<Class<?>> classes) {
      List<Class<?>> classesList = new ArrayList<>();
      classes.forEach(classesList::add);
      this.bindClasses(classesList, false);
   }

   public void bindClasses(Collection<Class<?>> classes) {
      this.bindClasses(classes, false);
   }

   public void bindClasses(Collection<Class<?>> classes, boolean bindResources) {
      List<Binder> binders = classes.stream().map(clazz -> this.createClassBinders((Class<?>)clazz, bindResources)).collect(Collectors.toList());
      this.injectionManager.register((Binder)CompositeBinder.wrap(binders));
   }

   private <T> Binder createClassBinders(final Class<T> clazz, boolean isResource) {
      final Class<? extends Annotation> scope = this.getProviderScope(clazz);
      return isResource ? new AbstractBinder() {
         @Override
         protected void configure() {
            ClassBinding<T> descriptor = this.bindAsContract(clazz).in(scope);

            for (Class contract : Providers.getProviderContracts(clazz)) {
               descriptor.addAlias(contract).in(scope.getName()).qualifiedBy(CustomAnnotationLiteral.INSTANCE);
            }
         }
      } : new AbstractBinder() {
         @Override
         protected void configure() {
            ClassBinding<T> builder = this.bind(clazz).in(scope).qualifiedBy(CustomAnnotationLiteral.INSTANCE);
            Providers.getProviderContracts(clazz).forEach(contract -> builder.to((Class<? super T>)contract));
         }
      };
   }

   private Class<? extends Annotation> getProviderScope(Class<?> clazz) {
      Class<? extends Annotation> scope = Singleton.class;
      if (clazz.isAnnotationPresent(PerLookup.class)) {
         scope = PerLookup.class;
      }

      return scope;
   }
}
