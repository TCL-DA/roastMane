package org.glassfish.jersey.client;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.ExtendedConfig;
import org.glassfish.jersey.client.innate.inject.NonInjectionManager;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.client.internal.inject.ParameterUpdaterConfigurator;
import org.glassfish.jersey.client.spi.Connector;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.internal.AutoDiscoverableConfigurator;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.FeatureConfigurator;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.ContextResolverFactory.ContextResolversConfigurator;
import org.glassfish.jersey.internal.ExceptionMapperFactory.ExceptionMappersConfigurator;
import org.glassfish.jersey.internal.JaxrsProviders.ProvidersConfigurator;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.ParamConverterConfigurator;
import org.glassfish.jersey.internal.inject.ProviderBinder;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.model.internal.CommonConfig;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.process.internal.RequestScope.RequestScopeConfigurator;
import org.glassfish.jersey.spi.ComponentProvider;

public class ClientConfig implements Configurable<ClientConfig>, ExtendedConfig {
   private ClientConfig.State state;

   public ClientConfig() {
      this.state = new ClientConfig.State(null);
   }

   public ClientConfig(Class<?>... providerClasses) {
      this();

      for (Class<?> providerClass : providerClasses) {
         this.state.register(providerClass);
      }
   }

   public ClientConfig(Object... providers) {
      this();

      for (Object provider : providers) {
         this.state.register(provider);
      }
   }

   ClientConfig(JerseyClient parent) {
      this.state = new ClientConfig.State(parent);
   }

   ClientConfig(JerseyClient parent, Configuration that) {
      if (that instanceof ClientConfig) {
         this.state = ((ClientConfig)that).state.copy(parent);
      } else {
         this.state = new ClientConfig.State(parent);
         this.state.loadFrom(that);
      }
   }

   private ClientConfig(ClientConfig.State state) {
      this.state = state;
   }

   ClientConfig snapshot() {
      this.state.markAsShared();
      return new ClientConfig(this.state);
   }

   public ClientConfig loadFrom(Configuration config) {
      if (config instanceof ClientConfig) {
         this.state = ((ClientConfig)config).state.copy();
      } else {
         this.state.loadFrom(config);
      }

      return this;
   }

   public ClientConfig register(Class<?> providerClass) {
      this.state = this.state.register(providerClass);
      return this;
   }

   public ClientConfig register(Object provider) {
      this.state = this.state.register(provider);
      return this;
   }

   public ClientConfig register(Class<?> providerClass, int bindingPriority) {
      this.state = this.state.register(providerClass, bindingPriority);
      return this;
   }

   public ClientConfig register(Class<?> providerClass, Class<?>... contracts) {
      this.state = this.state.register(providerClass, contracts);
      return this;
   }

   public ClientConfig register(Class<?> providerClass, Map<Class<?>, Integer> contracts) {
      this.state = this.state.register(providerClass, contracts);
      return this;
   }

   public ClientConfig register(Object provider, int bindingPriority) {
      this.state = this.state.register(provider, bindingPriority);
      return this;
   }

   public ClientConfig register(Object provider, Class<?>... contracts) {
      this.state = this.state.register(provider, contracts);
      return this;
   }

   public ClientConfig register(Object provider, Map<Class<?>, Integer> contracts) {
      this.state = this.state.register(provider, contracts);
      return this;
   }

   public ClientConfig property(String name, Object value) {
      this.state = this.state.property(name, value);
      return this;
   }

   public ClientConfig getConfiguration() {
      return this;
   }

   public RuntimeType getRuntimeType() {
      return this.state.getRuntimeType();
   }

   public Map<String, Object> getProperties() {
      return this.state.getProperties();
   }

   public Object getProperty(String name) {
      return this.state.getProperty(name);
   }

   public Collection<String> getPropertyNames() {
      return this.state.getPropertyNames();
   }

   public boolean isProperty(String name) {
      return this.state.isProperty(name);
   }

   public boolean isEnabled(Feature feature) {
      return this.state.isEnabled(feature);
   }

   public boolean isEnabled(Class<? extends Feature> featureClass) {
      return this.state.isEnabled(featureClass);
   }

   public boolean isRegistered(Object component) {
      return this.state.isRegistered(component);
   }

   public Map<Class<?>, Integer> getContracts(Class<?> componentClass) {
      return this.state.getContracts(componentClass);
   }

   public boolean isRegistered(Class<?> componentClass) {
      return this.state.isRegistered(componentClass);
   }

   public Set<Class<?>> getClasses() {
      return this.state.getClasses();
   }

   public Set<Object> getInstances() {
      return this.state.getInstances();
   }

   public ClientConfig connectorProvider(ConnectorProvider connectorProvider) {
      this.state = this.state.connectorProvider(connectorProvider);
      return this;
   }

   public ClientConfig executorService(ExecutorService executorService) {
      this.state = this.state.executorService(executorService);
      return this;
   }

   public ClientConfig scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
      this.state = this.state.scheduledExecutorService(scheduledExecutorService);
      return this;
   }

   public Connector getConnector() {
      return this.state.getConnector();
   }

   public ConnectorProvider getConnectorProvider() {
      return this.state.getConnectorProvider();
   }

   public ExecutorService getExecutorService() {
      return this.state.getExecutorService();
   }

   public ScheduledExecutorService getScheduledExecutorService() {
      return this.state.getScheduledExecutorService();
   }

   ClientRuntime getRuntime() {
      return (ClientRuntime)this.state.runtime.get();
   }

   public ClientExecutor getClientExecutor() {
      return (ClientExecutor)this.state.runtime.get();
   }

   public JerseyClient getClient() {
      return this.state.getClient();
   }

   ClientConfig preInitialize() {
      this.state = this.state.preInitialize();
      return this;
   }

   void checkClient() throws IllegalStateException {
      if (this.getClient() == null) {
         throw new IllegalStateException("Client configuration does not contain a parent client instance.");
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         ClientConfig other = (ClientConfig)obj;
         return this.state == other.state || this.state != null && this.state.equals(other.state);
      }
   }

   @Override
   public int hashCode() {
      int hash = 7;
      return 47 * hash + (this.state != null ? this.state.hashCode() : 0);
   }

   private static class RuntimeConfigConfigurator implements BootstrapConfigurator {
      private final ClientConfig.State runtimeConfig;

      private RuntimeConfigConfigurator(ClientConfig.State runtimeConfig) {
         this.runtimeConfig = runtimeConfig;
      }

      public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         bootstrapBag.setConfiguration(this.runtimeConfig);
         injectionManager.register(Bindings.service(this.runtimeConfig).to(Configuration.class));
      }
   }

   private static class State implements Configurable<ClientConfig.State>, ExtendedConfig {
      private static final ClientConfig.State.StateChangeStrategy IDENTITY = state -> state;
      private static final ClientConfig.State.StateChangeStrategy COPY_ON_CHANGE = ClientConfig.State::copy;
      private volatile ClientConfig.State.StateChangeStrategy strategy;
      private final CommonConfig commonConfig;
      private final JerseyClient client;
      private volatile ConnectorProvider connectorProvider;
      private volatile ExecutorService executorService;
      private volatile ScheduledExecutorService scheduledExecutorService;
      private final LazyValue<ClientRuntime> runtime = Values.lazy(this::initRuntime);

      State(JerseyClient client) {
         this.strategy = IDENTITY;
         this.commonConfig = new CommonConfig(RuntimeType.CLIENT, ComponentBag.EXCLUDE_EMPTY);
         this.client = client;
         Iterator<ConnectorProvider> iterator = ServiceFinder.find(ConnectorProvider.class).iterator();
         if (iterator.hasNext()) {
            this.connectorProvider = iterator.next();
         } else {
            this.connectorProvider = new HttpUrlConnectorProvider();
         }
      }

      private State(JerseyClient client, ClientConfig.State original) {
         this.strategy = IDENTITY;
         this.client = client;
         this.commonConfig = new CommonConfig(original.commonConfig);
         this.connectorProvider = original.connectorProvider;
         this.executorService = original.executorService;
         this.scheduledExecutorService = original.scheduledExecutorService;
      }

      ClientConfig.State copy() {
         return new ClientConfig.State(this.client, this);
      }

      ClientConfig.State copy(JerseyClient client) {
         return new ClientConfig.State(client, this);
      }

      void markAsShared() {
         this.strategy = COPY_ON_CHANGE;
      }

      ClientConfig.State preInitialize() {
         ClientConfig.State state = this.strategy.onChange(this);
         state.strategy = COPY_ON_CHANGE;
         ((ClientRuntime)state.runtime.get()).preInitialize();
         return state;
      }

      public ClientConfig.State property(String name, Object value) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.property(name, value);
         return state;
      }

      public ClientConfig.State loadFrom(Configuration config) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.loadFrom(config);
         return state;
      }

      public ClientConfig.State register(Class<?> providerClass) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(providerClass);
         return state;
      }

      public ClientConfig.State register(Object provider) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(provider);
         return state;
      }

      public ClientConfig.State register(Class<?> providerClass, int bindingPriority) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(providerClass, bindingPriority);
         return state;
      }

      public ClientConfig.State register(Class<?> providerClass, Class<?>... contracts) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(providerClass, contracts);
         return state;
      }

      public ClientConfig.State register(Class<?> providerClass, Map<Class<?>, Integer> contracts) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(providerClass, contracts);
         return state;
      }

      public ClientConfig.State register(Object provider, int bindingPriority) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(provider, bindingPriority);
         return state;
      }

      public ClientConfig.State register(Object provider, Class<?>... contracts) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(provider, contracts);
         return state;
      }

      public ClientConfig.State register(Object provider, Map<Class<?>, Integer> contracts) {
         ClientConfig.State state = this.strategy.onChange(this);
         state.commonConfig.register(provider, contracts);
         return state;
      }

      ClientConfig.State connectorProvider(ConnectorProvider provider) {
         if (provider == null) {
            throw new NullPointerException(LocalizationMessages.NULL_CONNECTOR_PROVIDER());
         } else {
            ClientConfig.State state = this.strategy.onChange(this);
            state.connectorProvider = provider;
            return state;
         }
      }

      ClientConfig.State executorService(ExecutorService executorService) {
         if (executorService == null) {
            throw new NullPointerException(LocalizationMessages.NULL_EXECUTOR_SERVICE());
         } else {
            ClientConfig.State state = this.strategy.onChange(this);
            state.executorService = executorService;
            return state;
         }
      }

      ClientConfig.State scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
         if (scheduledExecutorService == null) {
            throw new NullPointerException(LocalizationMessages.NULL_SCHEDULED_EXECUTOR_SERVICE());
         } else {
            ClientConfig.State state = this.strategy.onChange(this);
            state.scheduledExecutorService = scheduledExecutorService;
            return state;
         }
      }

      Connector getConnector() {
         return this.runtime.isInitialized() ? ((ClientRuntime)this.runtime.get()).getConnector() : null;
      }

      ConnectorProvider getConnectorProvider() {
         return this.connectorProvider;
      }

      ExecutorService getExecutorService() {
         return this.executorService;
      }

      ScheduledExecutorService getScheduledExecutorService() {
         return this.scheduledExecutorService;
      }

      JerseyClient getClient() {
         return this.client;
      }

      public ClientConfig.State getConfiguration() {
         return this;
      }

      public RuntimeType getRuntimeType() {
         return this.commonConfig.getConfiguration().getRuntimeType();
      }

      public Map<String, Object> getProperties() {
         return this.commonConfig.getConfiguration().getProperties();
      }

      public boolean hasProperty(String name) {
         return this.commonConfig.getConfiguration().hasProperty(name);
      }

      public Object getProperty(String name) {
         return this.commonConfig.getConfiguration().getProperty(name);
      }

      public Collection<String> getPropertyNames() {
         return this.commonConfig.getConfiguration().getPropertyNames();
      }

      public boolean isProperty(String name) {
         return this.commonConfig.getConfiguration().isProperty(name);
      }

      public boolean isEnabled(Feature feature) {
         return this.commonConfig.getConfiguration().isEnabled(feature);
      }

      public boolean isEnabled(Class<? extends Feature> featureClass) {
         return this.commonConfig.getConfiguration().isEnabled(featureClass);
      }

      public boolean isRegistered(Object component) {
         return this.commonConfig.getConfiguration().isRegistered(component);
      }

      public boolean isRegistered(Class<?> componentClass) {
         return this.commonConfig.getConfiguration().isRegistered(componentClass);
      }

      public Map<Class<?>, Integer> getContracts(Class<?> componentClass) {
         return this.commonConfig.getConfiguration().getContracts(componentClass);
      }

      public Set<Class<?>> getClasses() {
         return this.commonConfig.getConfiguration().getClasses();
      }

      public Set<Object> getInstances() {
         return this.commonConfig.getConfiguration().getInstances();
      }

      public void configureAutoDiscoverableProviders(InjectionManager injectionManager, List<AutoDiscoverable> autoDiscoverables) {
         this.commonConfig.configureAutoDiscoverableProviders(injectionManager, autoDiscoverables, false);
      }

      public void configureForcedAutoDiscoverableProviders(InjectionManager injectionManager) {
         this.commonConfig.configureAutoDiscoverableProviders(injectionManager, Collections.emptyList(), true);
      }

      public void configureMetaProviders(InjectionManager injectionManager, ManagedObjectsFinalizer finalizer) {
         this.commonConfig.configureMetaProviders(injectionManager, finalizer);
      }

      public ComponentBag getComponentBag() {
         return this.commonConfig.getComponentBag();
      }

      private ClientRuntime initRuntime() {
         this.markAsShared();
         ClientConfig.State runtimeCfgState = this.copy();
         runtimeCfgState.markAsShared();
         InjectionManager injectionManager = this.findInjectionManager();
         injectionManager.register(new ClientBinder(runtimeCfgState.getProperties()));
         ClientBootstrapBag bootstrapBag = new ClientBootstrapBag();
         bootstrapBag.setManagedObjectsFinalizer(new ManagedObjectsFinalizer(injectionManager));
         ClientMessageBodyFactory.MessageBodyWorkersConfigurator messageBodyWorkersConfigurator = new ClientMessageBodyFactory.MessageBodyWorkersConfigurator();
         List<BootstrapConfigurator> bootstrapConfigurators = Arrays.asList(
            new RequestScopeConfigurator(),
            new ParamConverterConfigurator(),
            new ParameterUpdaterConfigurator(),
            new ClientConfig.RuntimeConfigConfigurator(runtimeCfgState),
            new ContextResolversConfigurator(),
            messageBodyWorkersConfigurator,
            new ExceptionMappersConfigurator(),
            new ProvidersConfigurator(),
            new AutoDiscoverableConfigurator(RuntimeType.CLIENT),
            new ClientComponentConfigurator(),
            new FeatureConfigurator(RuntimeType.CLIENT)
         );
         bootstrapConfigurators.forEach(configurator -> configurator.init(injectionManager, bootstrapBag));
         if (!(Boolean)CommonProperties.getValue(
            runtimeCfgState.getProperties(), RuntimeType.CLIENT, "jersey.config.disableAutoDiscovery", Boolean.FALSE, Boolean.class
         )) {
            runtimeCfgState.configureAutoDiscoverableProviders(injectionManager, bootstrapBag.getAutoDiscoverables());
         } else {
            runtimeCfgState.configureForcedAutoDiscoverableProviders(injectionManager);
         }

         runtimeCfgState.configureMetaProviders(injectionManager, bootstrapBag.getManagedObjectsFinalizer());
         Collection<ComponentProvider> componentProviders = (Collection<ComponentProvider>)bootstrapBag.getComponentProviders().get();
         ProviderBinder.bindProviders(runtimeCfgState.getComponentBag(), RuntimeType.CLIENT, null, injectionManager, componentProviders);
         ClientExecutorProvidersConfigurator executorProvidersConfigurator = new ClientExecutorProvidersConfigurator(
            runtimeCfgState.getComponentBag(), runtimeCfgState.client, this.executorService, this.scheduledExecutorService
         );
         executorProvidersConfigurator.init(injectionManager, bootstrapBag);
         injectionManager.completeRegistration();
         bootstrapConfigurators.forEach(configurator -> configurator.postInit(injectionManager, bootstrapBag));
         ClientConfig configuration = new ClientConfig(runtimeCfgState);
         Connector connector = this.connectorProvider.getConnector(this.client, configuration);
         ClientRuntime crt = new ClientRuntime(configuration, connector, injectionManager, bootstrapBag);
         this.client.registerShutdownHook(crt);
         messageBodyWorkersConfigurator.setClientRuntime(crt);
         return crt;
      }

      private final InjectionManager findInjectionManager() {
         try {
            return Injections.createInjectionManager(RuntimeType.CLIENT);
         } catch (IllegalStateException var2) {
            return new NonInjectionManager(true);
         }
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            ClientConfig.State state = (ClientConfig.State)o;
            if (this.client != null ? this.client.equals(state.client) : state.client == null) {
               if (!this.commonConfig.equals(state.commonConfig)) {
                  return false;
               } else {
                  return this.connectorProvider == null ? state.connectorProvider == null : this.connectorProvider.equals(state.connectorProvider);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int result = this.commonConfig.hashCode();
         result = 31 * result + (this.client != null ? this.client.hashCode() : 0);
         return 31 * result + (this.connectorProvider != null ? this.connectorProvider.hashCode() : 0);
      }

      private interface StateChangeStrategy {
         ClientConfig.State onChange(ClientConfig.State var1);
      }
   }
}
