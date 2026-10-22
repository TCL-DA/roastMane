package org.glassfish.jersey.internal;

import jakarta.ws.rs.SeBootstrap.Instance;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Variant.VariantListBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.spi.HeaderDelegateProvider;

public class RuntimeDelegateDecorator {
   public static RuntimeDelegate configured(Configuration configuration) {
      return new RuntimeDelegateDecorator.ConfigurableRuntimeDelegate(RuntimeDelegate.getInstance(), configuration);
   }

   private static class ConfigurableRuntimeDelegate extends RuntimeDelegate {
      private final RuntimeDelegate runtimeDelegate;
      private final Configuration configuration;
      private static final Set<HeaderDelegateProvider> headerDelegateProviders;

      private ConfigurableRuntimeDelegate(RuntimeDelegate runtimeDelegate, Configuration configuration) {
         this.runtimeDelegate = runtimeDelegate;
         this.configuration = configuration;
      }

      public UriBuilder createUriBuilder() {
         return this.runtimeDelegate.createUriBuilder();
      }

      public ResponseBuilder createResponseBuilder() {
         return this.runtimeDelegate.createResponseBuilder();
      }

      public VariantListBuilder createVariantListBuilder() {
         return this.runtimeDelegate.createVariantListBuilder();
      }

      public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
         return (T)this.runtimeDelegate.createEndpoint(application, endpointType);
      }

      public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) throws IllegalArgumentException {
         HeaderDelegate<T> headerDelegate = null;
         if (this.configuration == null || PropertiesHelper.isMetaInfServicesEnabled(this.configuration.getProperties(), this.configuration.getRuntimeType())) {
            headerDelegate = this._createHeaderDelegate(type);
         }

         if (headerDelegate == null) {
            headerDelegate = this.runtimeDelegate.createHeaderDelegate(type);
         }

         return headerDelegate;
      }

      public Builder createLinkBuilder() {
         return this.runtimeDelegate.createLinkBuilder();
      }

      public jakarta.ws.rs.SeBootstrap.Configuration.Builder createConfigurationBuilder() {
         return this.runtimeDelegate.createConfigurationBuilder();
      }

      public CompletionStage<Instance> bootstrap(Application application, jakarta.ws.rs.SeBootstrap.Configuration configuration) {
         return this.runtimeDelegate.bootstrap(application, configuration);
      }

      public CompletionStage<Instance> bootstrap(Class<? extends Application> applicationClass, jakarta.ws.rs.SeBootstrap.Configuration configuration) {
         return this.runtimeDelegate.bootstrap(applicationClass, configuration);
      }

      public jakarta.ws.rs.core.EntityPart.Builder createEntityPartBuilder(String partName) throws IllegalArgumentException {
         return this.runtimeDelegate.createEntityPartBuilder(partName);
      }

      private <T> HeaderDelegate<T> _createHeaderDelegate(Class<T> type) {
         for (HeaderDelegateProvider hp : headerDelegateProviders) {
            if (hp.supports(type)) {
               return hp;
            }
         }

         return null;
      }

      static {
         Set<HeaderDelegateProvider> hps = new HashSet<>();

         for (HeaderDelegateProvider provider : ServiceFinder.find(HeaderDelegateProvider.class, true)) {
            hps.add(provider);
         }

         headerDelegateProviders = Collections.unmodifiableSet(hps);
      }
   }
}
