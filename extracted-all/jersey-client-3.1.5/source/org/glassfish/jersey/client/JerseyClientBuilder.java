package org.glassfish.jersey.client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Configuration;
import java.security.AccessController;
import java.security.KeyStore;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import org.glassfish.jersey.client.innate.inject.NonInjectionManager;
import org.glassfish.jersey.client.spi.ClientBuilderListener;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.config.ExternalPropertiesConfigurationFactory;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.model.internal.RankedProvider;
import org.glassfish.jersey.model.internal.RankedComparator.Order;

public class JerseyClientBuilder extends ClientBuilder {
   private final ClientConfig config;
   private HostnameVerifier hostnameVerifier;
   private final SslContextClientBuilder sslContextClientBuilder = new SslContextClientBuilder();
   private static final List<ClientBuilderListener> CLIENT_BUILDER_LISTENERS;

   public static JerseyClient createClient() {
      return new JerseyClientBuilder().build();
   }

   public static JerseyClient createClient(Configuration configuration) {
      return new JerseyClientBuilder().withConfig(configuration).build();
   }

   public JerseyClientBuilder() {
      this.config = new ClientConfig();
      init(this);
   }

   private static void init(ClientBuilder builder) {
      for (ClientBuilderListener listener : CLIENT_BUILDER_LISTENERS) {
         listener.onNewBuilder(builder);
      }
   }

   public JerseyClientBuilder sslContext(SSLContext sslContext) {
      this.sslContextClientBuilder.sslContext(sslContext);
      return this;
   }

   public JerseyClientBuilder keyStore(KeyStore keyStore, char[] password) {
      this.sslContextClientBuilder.keyStore(keyStore, password);
      return this;
   }

   public JerseyClientBuilder trustStore(KeyStore trustStore) {
      this.sslContextClientBuilder.trustStore(trustStore);
      return this;
   }

   public JerseyClientBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
      this.hostnameVerifier = hostnameVerifier;
      return this;
   }

   public ClientBuilder executorService(ExecutorService executorService) {
      this.config.executorService(executorService);
      return this;
   }

   public ClientBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
      this.config.scheduledExecutorService(scheduledExecutorService);
      return this;
   }

   public ClientBuilder connectTimeout(long timeout, TimeUnit unit) {
      if (timeout < 0L) {
         throw new IllegalArgumentException("Negative timeout.");
      } else {
         this.property("jersey.config.client.connectTimeout", Math.toIntExact(unit.toMillis(timeout)));
         return this;
      }
   }

   public ClientBuilder readTimeout(long timeout, TimeUnit unit) {
      if (timeout < 0L) {
         throw new IllegalArgumentException("Negative timeout.");
      } else {
         this.property("jersey.config.client.readTimeout", Math.toIntExact(unit.toMillis(timeout)));
         return this;
      }
   }

   public JerseyClient build() {
      ExternalPropertiesConfigurationFactory.configure(this.config);
      this.setConnectorFromProperties();
      return new JerseyClient(this.config, this.sslContextClientBuilder, this.hostnameVerifier, null);
   }

   private void setConnectorFromProperties() {
      Object connectorClass = this.config.getProperty("jersey.config.client.connector.provider");
      if (connectorClass != null) {
         if (!String.class.isInstance(connectorClass)) {
            throw new IllegalArgumentException();
         }

         Class<? extends ConnectorProvider> clazz = AccessController.doPrivileged(ReflectionHelper.classForNamePA((String)connectorClass));
         ConnectorProvider connectorProvider = new NonInjectionManager().justCreate(clazz);
         this.config.connectorProvider(connectorProvider);
      }
   }

   public ClientConfig getConfiguration() {
      return this.config;
   }

   public JerseyClientBuilder property(String name, Object value) {
      this.config.property(name, value);
      return this;
   }

   public JerseyClientBuilder register(Class<?> componentClass) {
      this.config.register(componentClass);
      return this;
   }

   public JerseyClientBuilder register(Class<?> componentClass, int priority) {
      this.config.register(componentClass, priority);
      return this;
   }

   public JerseyClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
      this.config.register(componentClass, contracts);
      return this;
   }

   public JerseyClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
      this.config.register(componentClass, contracts);
      return this;
   }

   public JerseyClientBuilder register(Object component) {
      this.config.register(component);
      return this;
   }

   public JerseyClientBuilder register(Object component, int priority) {
      this.config.register(component, priority);
      return this;
   }

   public JerseyClientBuilder register(Object component, Class<?>... contracts) {
      this.config.register(component, contracts);
      return this;
   }

   public JerseyClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
      this.config.register(component, contracts);
      return this;
   }

   public JerseyClientBuilder withConfig(Configuration config) {
      this.config.loadFrom(config);
      return this;
   }

   static {
      List<RankedProvider<ClientBuilderListener>> listeners = new LinkedList<>();

      for (ClientBuilderListener listener : ServiceFinder.find(ClientBuilderListener.class)) {
         listeners.add(new RankedProvider(listener));
      }

      listeners.sort(new RankedComparator(Order.ASCENDING));
      List<ClientBuilderListener> sortedList = new LinkedList<>();

      for (RankedProvider<ClientBuilderListener> listener : listeners) {
         sortedList.add((ClientBuilderListener)listener.getProvider());
      }

      CLIENT_BUILDER_LISTENERS = Collections.unmodifiableList(sortedList);
   }
}
