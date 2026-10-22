package org.glassfish.jersey.server.internal;

import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.SeBootstrap.Instance;
import jakarta.ws.rs.SeBootstrap.Instance.StopResult;
import jakarta.ws.rs.core.Application;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.glassfish.jersey.internal.AbstractRuntimeDelegate;
import org.glassfish.jersey.message.internal.MessagingBinders.HeaderDelegateProviders;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.JerseySeBootstrapConfiguration;
import org.glassfish.jersey.server.WebServerFactory;
import org.glassfish.jersey.server.spi.WebServer;

public class RuntimeDelegateImpl extends AbstractRuntimeDelegate {
   public RuntimeDelegateImpl() {
      super(new HeaderDelegateProviders().getHeaderDelegateProviders());
   }

   public <T> T createEndpoint(Application application, Class<T> endpointType) throws IllegalArgumentException, UnsupportedOperationException {
      if (application == null) {
         throw new IllegalArgumentException("application is null.");
      } else {
         return ContainerFactory.createContainer(endpointType, application);
      }
   }

   public JerseySeBootstrapConfiguration.Builder createConfigurationBuilder() {
      return JerseySeBootstrapConfiguration.builder();
   }

   public CompletableFuture<Instance> bootstrap(Application application, Configuration configuration) {
      return CompletableFuture.supplyAsync(
         () -> {
            Class<WebServer> httpServerClass = configuration.hasProperty("jersey.config.server.bootstrap.webserver.class")
               ? (Class)configuration.property("jersey.config.server.bootstrap.webserver.class")
               : WebServer.class;
            WebServer webServer = WebServerFactory.createServer(httpServerClass, application, configuration);
            return this.instance(configuration, webServer);
         }
      );
   }

   public CompletableFuture<Instance> bootstrap(Class<? extends Application> applicationClass, Configuration configuration) {
      return CompletableFuture.supplyAsync(
         () -> {
            Class<WebServer> httpServerClass = configuration.hasProperty("jersey.config.server.bootstrap.webserver.class")
               ? (Class)configuration.property("jersey.config.server.bootstrap.webserver.class")
               : WebServer.class;
            WebServer webServer = WebServerFactory.createServer(httpServerClass, applicationClass, configuration);
            return this.instance(configuration, webServer);
         }
      );
   }

   private Instance instance(final Configuration configuration, final WebServer _webServer) {
      return new Instance() {
         final WebServer webServer = _webServer;

         public final JerseySeBootstrapConfiguration configuration() {
            return JerseySeBootstrapConfiguration.from(name -> {
               switch (name) {
                  case "jakarta.ws.rs.SeBootstrap.Port":
                     return this.webServer.port();
                  case "jersey.config.server.bootstrap.webserver.class":
                     return this.webServer.getClass();
                  default:
                     return configuration.property(name);
               }
            });
         }

         public final CompletionStage<StopResult> stop() {
            return this.webServer.stop().thenApply(nativeResult -> new StopResult() {
                  public final <T> T unwrap(Class<T> nativeClass) {
                     return (T)nativeClass.cast(nativeResult);
                  }
               });
         }

         public final <T> T unwrap(Class<T> nativeClass) {
            return nativeClass.isInstance(this.webServer) ? nativeClass.cast(this.webServer) : this.webServer.unwrap(nativeClass);
         }
      };
   }
}
