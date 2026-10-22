package org.glassfish.jersey.server;

import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.process.internal.AbstractExecutorProvidersConfigurator;
import org.glassfish.jersey.spi.ExecutorServiceProvider;
import org.glassfish.jersey.spi.ScheduledExecutorServiceProvider;
import org.glassfish.jersey.spi.ScheduledThreadPoolExecutorProvider;
import org.glassfish.jersey.spi.ThreadPoolExecutorProvider;

class ServerExecutorProvidersConfigurator extends AbstractExecutorProvidersConfigurator {
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      ResourceConfig runtimeConfig = serverBag.getRuntimeConfig();
      ComponentBag componentBag = runtimeConfig.getComponentBag();
      ScheduledExecutorServiceProvider defaultScheduledExecutorProvider = new ServerExecutorProvidersConfigurator.DefaultBackgroundSchedulerProvider();
      InstanceBinding<ScheduledExecutorServiceProvider> schedulerBinding = (InstanceBinding<ScheduledExecutorServiceProvider>)((InstanceBinding)Bindings.service(
               defaultScheduledExecutorProvider
            )
            .to(ScheduledExecutorServiceProvider.class))
         .qualifiedBy(BackgroundSchedulerLiteral.INSTANCE);
      injectionManager.register(schedulerBinding);
      ExecutorServiceProvider defaultAsyncExecutorProvider = new ServerExecutorProvidersConfigurator.DefaultManagedAsyncExecutorProvider();
      InstanceBinding<ExecutorServiceProvider> executorBinding = (InstanceBinding<ExecutorServiceProvider>)Bindings.service(defaultAsyncExecutorProvider)
         .to(ExecutorServiceProvider.class);
      injectionManager.register(executorBinding);
      this.registerExecutors(
         injectionManager, componentBag, defaultAsyncExecutorProvider, defaultScheduledExecutorProvider, serverBag.getManagedObjectsFinalizer()
      );
   }

   @BackgroundScheduler
   private static class DefaultBackgroundSchedulerProvider extends ScheduledThreadPoolExecutorProvider {
      public DefaultBackgroundSchedulerProvider() {
         super("jersey-background-task-scheduler");
      }

      protected int getCorePoolSize() {
         return 1;
      }
   }

   @ManagedAsyncExecutor
   private static class DefaultManagedAsyncExecutorProvider extends ThreadPoolExecutorProvider {
      public DefaultManagedAsyncExecutorProvider() {
         super("jersey-server-managed-async-executor");
      }
   }
}
