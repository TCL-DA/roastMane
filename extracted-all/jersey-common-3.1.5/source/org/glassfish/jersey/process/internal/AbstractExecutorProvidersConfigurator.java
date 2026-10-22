package org.glassfish.jersey.process.internal;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;

public abstract class AbstractExecutorProvidersConfigurator implements BootstrapConfigurator {
   private static final Function<Object, ExecutorServiceProvider> CAST_TO_EXECUTOR_PROVIDER = ExecutorServiceProvider.class::cast;
   private static final Function<Object, ScheduledExecutorServiceProvider> CAST_TO_SCHEDULED_EXECUTOR_PROVIDER = ScheduledExecutorServiceProvider.class::cast;

   protected void registerExecutors(
      InjectionManager injectionManager,
      ComponentBag componentBag,
      ExecutorServiceProvider defaultAsyncExecutorProvider,
      ScheduledExecutorServiceProvider defaultScheduledExecutorProvider,
      ManagedObjectsFinalizer finalizer
   ) {
      List<ExecutorServiceProvider> customExecutors = Stream.concat(
            componentBag.getClasses(ComponentBag.EXECUTOR_SERVICE_PROVIDER_ONLY).stream().map(injectionManager::createAndInitialize),
            componentBag.getInstances(ComponentBag.EXECUTOR_SERVICE_PROVIDER_ONLY).stream()
         )
         .map(CAST_TO_EXECUTOR_PROVIDER)
         .collect(Collectors.toList());
      customExecutors.add(defaultAsyncExecutorProvider);
      customExecutors.stream().forEach(e -> finalizer.registerForPreDestroyCall(e));
      List<ScheduledExecutorServiceProvider> customScheduledExecutors = Stream.concat(
            componentBag.getClasses(ComponentBag.SCHEDULED_EXECUTOR_SERVICE_PROVIDER_ONLY).stream().map(injectionManager::createAndInitialize),
            componentBag.getInstances(ComponentBag.SCHEDULED_EXECUTOR_SERVICE_PROVIDER_ONLY).stream()
         )
         .map(CAST_TO_SCHEDULED_EXECUTOR_PROVIDER)
         .collect(Collectors.toList());
      customScheduledExecutors.add(defaultScheduledExecutorProvider);
      customScheduledExecutors.stream().forEach(e -> finalizer.registerForPreDestroyCall(e));
      ExecutorProviders.registerExecutorBindings(injectionManager, customExecutors, customScheduledExecutors);
   }
}
