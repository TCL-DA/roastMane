package org.glassfish.jersey.server;

import jakarta.ws.rs.RuntimeType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.ProviderBinder;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.server.internal.JerseyResourceContext;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.ModelProcessor;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;
import org.glassfish.jersey.server.spi.ComponentProvider;

public class ResourceModelConfigurator implements BootstrapConfigurator {
   private static final Logger LOGGER = Logger.getLogger(ResourceModelConfigurator.class.getName());

   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      Collection<ModelProcessor> modelProcessors = serverBag.getModelProcessors();
      ResourceConfig runtimeConfig = serverBag.getRuntimeConfig();
      ResourceBag resourceBag = serverBag.getResourceBag();
      ComponentBag componentBag = runtimeConfig.getComponentBag();
      this.bindProvidersAndResources(injectionManager, serverBag, componentBag, resourceBag.classes, resourceBag.instances, runtimeConfig);
      ResourceModel resourceModel = new ResourceModel.Builder(resourceBag.getRootResources(), false).build();
      resourceModel = this.processResourceModel(modelProcessors, resourceModel, runtimeConfig);
      this.bindEnhancingResourceClasses(injectionManager, serverBag, resourceModel, resourceBag, runtimeConfig);
      serverBag.setResourceModel(resourceModel);
      serverBag.getResourceContext().setResourceModel(resourceModel);
   }

   private ResourceModel processResourceModel(Collection<ModelProcessor> modelProcessors, ResourceModel resourceModel, ResourceConfig runtimeConfig) {
      for (ModelProcessor modelProcessor : modelProcessors) {
         resourceModel = modelProcessor.processResourceModel(resourceModel, runtimeConfig);
      }

      return resourceModel;
   }

   private void bindEnhancingResourceClasses(
      InjectionManager injectionManager, ServerBootstrapBag bootstrapBag, ResourceModel resourceModel, ResourceBag resourceBag, ResourceConfig runtimeConfig
   ) {
      Set<Class<?>> newClasses = new HashSet<>();
      Set<Object> newInstances = new HashSet<>();

      for (Resource res : resourceModel.getRootResources()) {
         newClasses.addAll(res.getHandlerClasses());
         newInstances.addAll(res.getHandlerInstances());
      }

      newClasses.removeAll(resourceBag.classes);
      newInstances.removeAll(resourceBag.instances);
      ComponentBag emptyComponentBag = ComponentBag.newInstance(input -> false);
      this.bindProvidersAndResources(injectionManager, bootstrapBag, emptyComponentBag, newClasses, newInstances, runtimeConfig);
   }

   private void bindProvidersAndResources(
      InjectionManager injectionManager,
      ServerBootstrapBag bootstrapBag,
      ComponentBag componentBag,
      Collection<Class<?>> resourceClasses,
      Collection<Object> resourceInstances,
      ResourceConfig runtimeConfig
   ) {
      Collection<ComponentProvider> componentProviders = (Collection<ComponentProvider>)bootstrapBag.getComponentProviders().get();
      JerseyResourceContext resourceContext = bootstrapBag.getResourceContext();
      Set<Class<?>> registeredClasses = runtimeConfig.getRegisteredClasses();
      Predicate<Class<?>> correctlyConfigured = componentClassx -> Providers.checkProviderRuntime(
            componentClassx,
            componentBag.getModel(componentClassx),
            RuntimeType.SERVER,
            !registeredClasses.contains(componentClassx),
            resourceClasses.contains(componentClassx)
         );
      BiPredicate<Class<?>, ContractProvider> correctlyConfiguredResource = (resourceClass, modelx) -> Providers.checkProviderRuntime(
            resourceClass, modelx, RuntimeType.SERVER, !registeredClasses.contains(resourceClass), true
         );
      Set<Class<?>> componentClasses = componentBag.getClasses(ComponentBag.excludeMetaProviders(injectionManager))
         .stream()
         .filter(correctlyConfigured)
         .collect(Collectors.toSet());
      Set<Class<?>> classes = Collections.newSetFromMap(new IdentityHashMap<>());
      classes.addAll(componentClasses);
      classes.addAll(resourceClasses);

      for (Class<?> componentClass : classes) {
         ContractProvider model = componentBag.getModel(componentClass);
         if (!this.bindWithComponentProvider(componentClass, model, componentProviders)) {
            if (resourceClasses.contains(componentClass)) {
               if (!Resource.isAcceptable(componentClass)) {
                  LOGGER.warning(LocalizationMessages.NON_INSTANTIABLE_COMPONENT(componentClass));
               } else {
                  if (model != null && !correctlyConfiguredResource.test(componentClass, model)) {
                     model = null;
                  }

                  resourceContext.unsafeBindResource(componentClass, model);
               }
            } else {
               ProviderBinder.bindProvider(componentClass, model, injectionManager);
            }
         }
      }

      Set<Object> instances = componentBag.getInstances(ComponentBag.excludeMetaProviders(injectionManager))
         .stream()
         .filter(instance -> correctlyConfigured.test(instance.getClass()))
         .collect(Collectors.toSet());
      instances.addAll(resourceInstances);

      for (Object component : instances) {
         ContractProvider model = componentBag.getModel(component.getClass());
         if (resourceInstances.contains(component)) {
            if (model != null && !correctlyConfiguredResource.test(component.getClass(), model)) {
               model = null;
            }

            resourceContext.unsafeBindResource(component, model);
         } else {
            ProviderBinder.bindProvider(component, model, injectionManager);
         }
      }
   }

   private boolean bindWithComponentProvider(Class<?> component, ContractProvider providerModel, Iterable<ComponentProvider> componentProviders) {
      for (ComponentProvider provider : componentProviders) {
         if (provider.bind(component, providerModel)) {
            return true;
         }
      }

      return false;
   }
}
